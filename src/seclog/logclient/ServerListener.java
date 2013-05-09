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

package seclog.logclient;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import seclog.logclient.util.Constants;

public class ServerListener implements Constants
{
	private boolean shutdown = false;
	
    ThreadPoolExecutor threadPool = null;
    ArrayBlockingQueue<Runnable> threadPoolQueue = null;
	private int listenport; // server socket port
	
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
		// init other modules
		initialize();
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
	
	private void initialize()
	{
		// read config
		RemoteHostConfig.getInstance().initialize(SHAMIR_REMOTE_HOST_CONFIG);
		RemoteHostConfig.getInstance().readFile();
		// generate keys
		KeyCrypto.getInstance().initialize();
		// send keys
		//KeyCrypto.getInstance().sendKeys();
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
		final Socket socket;	
		private ArrayList<String> logbuffer = null;

        private Handler(Socket socket)
        {
        	this.socket = socket;  
        	logbuffer = new ArrayList<String>(CLIENT_BUFFER_SIZE);
        }        
        
        public void run()
        {
        	try 
        	{
        		String logMessage;
        		BufferedReader br = new BufferedReader(new InputStreamReader(getSocket().getInputStream()));
			              		
        		while ( (logMessage = br.readLine()) != null ) 
        		{       		
        			// add log message to the log buffer
        			setLogBufferEntry(logMessage);
        			
        			// check if buffer is full
        			if (getLogBufferSize() == CLIENT_BUFFER_SIZE)
        			{
            			// process log buffer   			
            			BatchCrypto.getInstance().processLogMessage(getLogBufferAsString());
            			
            			// clear log buffer
            			clearLogBuffer();
        			}			
        		}
        	} catch (IOException e) {
				e.printStackTrace();
			}
        	
        	// close socket
        	try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}        	
        }
        
        private Socket getSocket()
        {        	
        	return socket;
        }
        
        private int getLogBufferSize()
        {
        	return logbuffer.size();
        }
        
        private String getLogBufferEntry(int i)
        {
        	return logbuffer.get(i);
        }
        
        private void setLogBufferEntry(String line)
        {
        	logbuffer.add(line);
        }
        
        private void clearLogBuffer()
        {
        	logbuffer.clear();
        }
        
        private String getLogBufferAsString()
        {
        	String delim = "\n";
        	StringBuffer sb = new StringBuffer();
        	for (int i = 0; i <  getLogBufferSize(); i++)
        	{
        		sb.append(getLogBufferEntry(i));
        		sb.append(delim);
        	}
        	return sb.toString();
        }
    }
}

