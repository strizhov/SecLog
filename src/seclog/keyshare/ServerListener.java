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

package seclog.keyshare;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import seclog.wireformats.ShamirMessageType;

public class ServerListener 
{
	private int listenport;
	
	public ServerListener(String listenport)
	{
		this.listenport = Integer.parseInt(listenport);
	}
	
	public int getListenPort()
	{
		return listenport;
	}

	public void launchSever()
	{
		ServerSocket sock = null;
		try {
			sock = new ServerSocket(listenport);
		} catch (IOException e) {
			System.out.println("Unable to listen the socket port: " + e.getMessage());
			e.printStackTrace();
		}

	    // for each incoming connection spawn a thread
	    while (!Thread.interrupted()) 
	    {
	    	try {
				new Thread(new Handler(sock.accept())).start();
			} catch (IOException e) {
				System.out.println("Unable to spawn thread: " + e.getMessage());
				e.printStackTrace();
			}
	    }     
   	}
	
	private class Handler implements Runnable
    {
		final Socket socket;

        private Handler(Socket socket)
        {
        	this.socket = socket;
        }
        
        public void run()
        {
        	ObjectInputStream  ois = null;
    		try {
				ois = new ObjectInputStream(getSocket().getInputStream());
			} catch (IOException e) {
				System.out.println("Unable to get an object input stream: " + e.getMessage());
				e.printStackTrace();
			}

    		ShamirMessageType mt = null;
            try {
				mt = (ShamirMessageType)ois.readObject();
			} catch (IOException e) {
				System.out.println("Unable to get MessageType object: " + e.getMessage());
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				System.out.println("Unable to locate MessageType class: " + e.getMessage());
				e.printStackTrace();
			}            	

       		System.out.println("Type is: " + mt.getMessageType());
       		
       		// check message type
       	 	switch (mt.getMessageType())
       	 	{
       	 		// add new key to the key storate
       	 		case ShamirMessageType.KEY_SEND_TYPE:
       	 		    KeyStorage.getInstance().addKey(mt);     	 		    
                    break;
                // retrieve key    
       	 		case ShamirMessageType.KEY_RECV_TYPE:
       	 			KeyStorage.getInstance().getKey(mt);
       	 			break;
       	 		default: 
       	 			System.out.println("Invalid Message Type: " + mt.getMessageType());
                	break;
       	 	}			
		}
        
        private Socket getSocket()
        {        	
        	return socket;
        }  
    } // class Handler	   
}

