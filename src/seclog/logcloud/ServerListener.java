/* 
 *      Copyright (c) 2012 Colorado State University
 * 
 *      Permission is hereby granted, free of charge, to any person
 *      obtaining a copy of this software and associated documentation
 *      files (the "Software"), to deal in the Software without
 *      restriction, including without limitation the rights to use,
 *      copy, modify, merge, publish, distribute, sublicense, and/or
 *      sell copies of the Software, and to permit persons to whom
 *      the Software is furnished to do so, subject to the following
 *      conditions:
 *
 *      The above copyright notice and this permission notice shall be
 *      included in all copies or substantial portions of the Software.
 *
 *      THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *      EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *      OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *      NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *      HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *      WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *      FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *      OTHER DEALINGS IN THE SOFTWARE.
 * 
 *      Authors: Mikhail Strizhov
 *      Date: May 9, 2012
 */

package seclog.logcloud;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import seclog.logcloud.util.CloudConstants;
import seclog.wireformats.CloudDHUploadMessage;
import seclog.wireformats.CloudMessageType;
import seclog.wireformats.CloudDHRequestMessage;
import seclog.wireformats.CloudTagMessage;
import seclog.wireformats.util.TagConstants;


public class ServerListener implements CloudConstants, TagConstants
{
	private boolean shutdown = false;
	
    ThreadPoolExecutor threadPool = null;
    ArrayBlockingQueue<Runnable> threadPoolQueue = null;
	private int listenport; // server socket port
	
	// hash map: key is hostname, object is cloud tag message
	HashMap<String,CloudDHUploadMessage> hashmap = new HashMap<String,CloudDHUploadMessage>();
	
	public ServerListener(String listenport)
	{
		this.listenport = Integer.parseInt(listenport);
		
		// create thread pool
		threadPoolQueue= new ArrayBlockingQueue<Runnable>(THREAD_POOL_QUEUE_SIZE);
		threadPool = new ThreadPoolExecutor( 	THREAD_POOL_MIN_SIZE, 
												THREAD_POOL_MAX_SIZE,
												THREAD_POOL_KEEP_ALIVE_TIME, 
												THREAD_POOL_TIMEUNIT, 
												threadPoolQueue);
	}
	
	public int getListenPort()
	{
		return listenport;
	}
	
	public void shutDown()
	{
		shutdown = true;
		threadPool.shutdown();
	}
	
	public void launchSever()
	{
		try 
		{
			// spawn socket server
			ServerSocket sock = new ServerSocket(listenport);
			
			while (getShutDown() != true)
			{
				// add new connection to the thread pool
				getThreadPool().execute(new Handler(sock.accept()));
			}
		} catch (IOException e) {
			System.out.println("Unable to listen the socket port: " + e.getMessage());
			e.printStackTrace();
		}
		      
   	}
	
	private boolean getShutDown()
	{
		return shutdown;
	}
	
	private ThreadPoolExecutor getThreadPool()
	{
		return threadPool;
	}
	
	private class Handler implements Runnable
    {
		private Socket socket;	
		private ObjectInputStream ois;
		private ObjectOutputStream oos;


        private Handler(Socket socket)
        {
        	this.socket = socket;  
        	// init input streams
        	try {
				this.ois = new ObjectInputStream(getSocket().getInputStream());
    			this.oos = new ObjectOutputStream(getSocket().getOutputStream());
    			

    			
			} catch (IOException e) {
				e.printStackTrace();
			}
        }        
  
        public void run()
        {      
        	try 
        	{
        		String outstr = System.currentTimeMillis()+" "+getSocket().getRemoteSocketAddress().toString()+"\n";
        		BufferedWriter out = new BufferedWriter(new FileWriter("Server-log.txt", true));
        		out.write(outstr);
				out.close();
				
        		CloudMessageType mt = (CloudMessageType)getInputStream().readObject();
				
        		// check message type
       	 		switch (mt.getCloudTagMessageType())
       	 		{
       	 			case DHTAG_UPLOAD:
						processDHUploadTag(mt);				
       	 				break;
       	 			case DHTAG_REQUEST:
       	 				processDHRequestTag(mt);
       	 				break;
       	 			case LOG_UPLOAD:
       	 				processLogUpdate(mt);
       	 				break;
       	 			default: 
       	 				System.out.println("Invalid Message Type: " + mt.getCloudTagMessageType());
       	 				break;
       	 		}	
        	} catch (IOException e1) {
				e1.printStackTrace();
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {    
       	 		// close objects and socket
            	try {
       	 			getInputStream().close();
       	 			getOutputStream().close();
					getSocket().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
        }
        
        /*
         * Purpose: process DH upload tag
         */
        private void processDHUploadTag(CloudMessageType mt)
        {
        	System.out.println("Received DH upload message type...");
        	CloudDHUploadMessage ctm = (CloudDHUploadMessage)mt;
        	String hostname = ctm.getID();
        	System.out.println("Tag from: "+hostname);
        	// add DH tag
        	addDHTag(hostname, ctm);
        }
		
        /*
         * Purpose: process DH request for tag
         */
        private void processDHRequestTag(CloudMessageType mt)
        {
        	try 
        	{        
        		System.out.println("Received DH request message type...");
        		CloudDHRequestMessage ctrm = (CloudDHRequestMessage)mt;
        		String hostname = ctrm.getID();
        		
    			// check if hashmap contains a tag object
        		if (getHashMap().containsKey(hostname) == true)
        		{
        			// get the right message
            		CloudDHUploadMessage ctmlookup = getHashMap().get(hostname);
            		// change type        		
            		ctmlookup.setCloudTagMessageType(DHTAG_REPLY);
            		getOutputStream().writeObject(ctmlookup); 

        		}
        		else
        		{
        			CloudDHUploadMessage ctmnull = 
        					new CloudDHUploadMessage(DHTAG_NA, null, null, null, null);
        			getOutputStream().writeObject(ctmnull);
        		}
        		
        		// write all buffered output bytes 
        		getOutputStream().flush();

			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        
        /*
         * Purpose: process Log Batch
         */
        private void processLogUpdate(CloudMessageType mt)
        {
/*        	CloudTagMessage ctm = (CloudTagMessage)mt;
        	DBConnection dbc =  new DBConnection();
        	dbc.storeLog(ctm);
*/        	   
        }
        
        private Socket getSocket()
        {        	
        	return socket;
        }
        
        private ObjectInputStream getInputStream()
        {
        	return ois;
        }
        
        private ObjectOutputStream getOutputStream()
        {
        	return oos;
        }
        
        private HashMap<String,CloudDHUploadMessage> getHashMap()
        {
        	return hashmap;
        }
        private synchronized void addDHTag(String hostname, CloudDHUploadMessage ctm)
        {
        	getHashMap().put(hostname, ctm);
        }
    }
}
