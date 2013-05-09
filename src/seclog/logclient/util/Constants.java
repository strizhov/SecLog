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

package seclog.logclient.util;

import java.util.concurrent.TimeUnit;

public interface Constants {

	/*
	 * Thread Pool constants
	 */
	public static final int 		THREAD_POOL_MIN_SIZE = 5;
    public static final int 		THREAD_POOL_MAX_SIZE = 100; 
    public static final long 		THREAD_POOL_KEEP_ALIVE_TIME = 10;
    public static final TimeUnit	THREAD_POOL_TIMEUNIT = TimeUnit.SECONDS;
    public static final int 		THREAD_POOL_QUEUE_SIZE = 500;
    
    /*
     * Shamir's shared constants
     */
    // location of config file
    public static final String 		SHAMIR_REMOTE_HOST_CONFIG = "../config/RemoteHosts.txt";
    // number of shares for solve the secret (t <= num hosts)
	public static final int 		SHAMIR_SHARES_MIN_NUM = 3; 
	// secret level (number of bits) for Shamir algorithm
	public static final int 		SHAMIR_CIPHER_SIZE = 512; 
    
    /*
     * Batch constants
     */
    // Specifies how many messages to buffer from client before process it to the batch
	public static final int 		CLIENT_BUFFER_SIZE = 1;
	// Size of batch
	public static final int 		BATCH_MAX_SIZE = 100; // 100 log messages (without init and close)
	// N number of hash 
	public static final int 		BATCH_HASH_NUM = 100; // 100 times use for one secret sharing key
    
	/*
	 * Crypto algorithms
	 */
	public static final int  		CRYPTO_MD5_KEY_SIZE	=	128;
	public static final String 		CRYPTO_HMAC_MD5_ALGORITHM = "HmacMD5";
	public static final String 		CRYPTO_HASH_MD5_ALGORITHM =	"MD5";
	public static final String 		CRYPTO_AES_ALGORITHM = "AES";
	
	/*
	 * LogEntry constants
	 */
	public static final String 		LOG_ENTRY_PIPE	= "|";
	public static final String 		LOG_ENTRY_INIT	= "log-initialization";
	public static final String 		LOG_ENTRY_CLOSE	= "log-close";
    
}

