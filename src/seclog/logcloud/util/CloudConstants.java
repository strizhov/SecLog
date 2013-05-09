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

package seclog.logcloud.util;

import java.util.concurrent.TimeUnit;

public interface CloudConstants {

	/*
	 * Thread Pool constants
	 */
	public static final int 		THREAD_POOL_MIN_SIZE = 5;
    public static final int 		THREAD_POOL_MAX_SIZE = 100; 
    public static final long 		THREAD_POOL_KEEP_ALIVE_TIME = 10;
    public static final TimeUnit	THREAD_POOL_TIMEUNIT = TimeUnit.SECONDS;
    public static final int 		THREAD_POOL_QUEUE_SIZE = 500;
    
    /*
     * DB constants
     */
    public static final String		DB_JDBC_DRIVER = "com.mysql.jdbc.Driver";
	public static final String 		DB_SERVER = "jdbc:mysql://test6/seclog";  
	public static final String		DB_USER = "seclog";
	public static final String		DB_PASSWD = "seclog";
    public static final String 		DB_LOG_INSERT="insert into batches (uploadTag, deleteTag, batch) values (?, ?, ?) ;";
}
