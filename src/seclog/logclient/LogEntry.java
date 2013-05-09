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

import seclog.logclient.util.Constants;

public class LogEntry implements Constants
{	
	public LogEntry() {}
	/*
	 *  Log init Method: it takes the batch size
	 *  Format:  TIMESTAMP|LOGINIT|BATCHNUM
	 */	
	public static String makeLog(int n)
	{	
		long TS = getTimeStamp();
		// create StringBuffer
		StringBuffer sb = new StringBuffer();		
		sb.append(TS);
		sb.append(LOG_ENTRY_PIPE);
		sb.append(LOG_ENTRY_INIT);
		sb.append(LOG_ENTRY_PIPE);
		sb.append(n);
		//System.out.println("INIT LOG: "+sb.toString());
		return sb.toString();
	}
	
	/*
	 * Log message method: takes log from syslog and calculated mac
	 * Format: TIMESTAMP|LOGMSG|MAC
	 */
	public static String makeLog(String log, String mac)
	{
		long TS = getTimeStamp();
		StringBuffer sb = new StringBuffer();
		sb.append(TS);
		sb.append(LOG_ENTRY_PIPE);
		sb.append(log);
		sb.append(LOG_ENTRY_PIPE);
		sb.append(mac);
		//System.out.println("LOG MSG: "+sb.toString());
		return sb.toString();
	}
	
	// 
	/*
	 * Log Close method, takes the calculated mac
	 * Format: TIMESTAMP|LOGCLOSE|MAC
	 */
	public static String makeLog(String mac)
	{
		long TS = getTimeStamp();
		StringBuffer sb = new StringBuffer();
		sb.append(TS);
		sb.append(LOG_ENTRY_PIPE);
		sb.append(LOG_ENTRY_CLOSE);
		sb.append(LOG_ENTRY_PIPE);
		sb.append(mac);
		//System.out.println("CLOSE LOG: "+sb.toString());
		return sb.toString();
	}

	private static long getTimeStamp()
	{
		return System.currentTimeMillis();
	}
	
}//end of class
