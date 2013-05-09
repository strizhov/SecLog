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

package seclog.util;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class Networking {
	
	private static int socketTimeout = 5; // socket connection timeout
	
    /*
     * Opens a socket connection to a host
     * Input: server name, port number
     * Output: socket
     * Author: Mikhail Strizhov	
     */
    public static Socket openSocket(String server, int port) throws Exception 
    {
    	InetAddress inteAddress = InetAddress.getByName(server);
    	SocketAddress socketAddress = new InetSocketAddress(inteAddress, port);

        // create a socket
        Socket socket = new Socket();

        int timeoutInMs = socketTimeout * 1000;
        socket.connect(socketAddress, timeoutInMs);
            
        return socket;
    }
    
    /*
     * Return machine hostname
     */
    public static  String getHostName() throws Exception
    {
    	InetAddress addr = InetAddress.getLocalHost();
    	return addr.getHostName();
    }
}
