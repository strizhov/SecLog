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

import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import seclog.logclient.util.Constants;

public abstract class Crypto implements Constants
{	
	/*
	 * Purpose: Default constructor
	 */
	public Crypto() {}	
	
	/*
	 * Purpose: create hash of a key
	 */
	protected byte[] getHash(byte[] key) throws Exception
	{
		MessageDigest md = MessageDigest.getInstance(CRYPTO_HASH_MD5_ALGORITHM);
		md.reset();
		md.update(key);		
		return md.digest();
	}
	
	/*
	 * Purpose: generate key for hmac function
	 */
	protected byte[] getSecretKey() throws Exception
	{
		SecureRandom sr = new SecureRandom();
		KeyGenerator keyGen = KeyGenerator.getInstance(CRYPTO_HMAC_MD5_ALGORITHM); 
		// set size of key with secure random
		keyGen.init(CRYPTO_MD5_KEY_SIZE, sr); 
		SecretKey secretKey = keyGen.generateKey();
		byte[] ret = secretKey.getEncoded();
		return ret;
	}
	
	/*
	 * Purpose: encrypt string message with key, return byte array
	 */
	protected byte[] encryptLogEntry(String msg, byte[] key) throws Exception
	{ 		
		SecretKeySpec skeySpec = new SecretKeySpec(key, CRYPTO_AES_ALGORITHM);
	    Cipher cipher = Cipher.getInstance(CRYPTO_AES_ALGORITHM);
	    cipher.init(Cipher.ENCRYPT_MODE, skeySpec);	        	
	    byte[] cipherBytes = cipher.doFinal(msg.getBytes());	        		        	
	    return cipherBytes;		     
	}
	
 	/*
 	 * Purpose: computes the MAC.
 	 */
	protected String getMAC(byte[] msg, byte[] key) throws Exception
	{	
		SecretKeySpec signingKey = new SecretKeySpec(key, CRYPTO_HMAC_MD5_ALGORITHM);

		// get an hmac_sha1 Mac instance and initialize with the signing key
		Mac mac = Mac.getInstance(CRYPTO_HMAC_MD5_ALGORITHM);
		mac.init(signingKey);
			
		// compute the hmac on input data bytes
		byte[] rawmac = mac.doFinal(msg);
		// return Hex String
		return convertBytesToHex(rawmac);
	}
	
	/*
 	 * Purpose: computes the aggregated MAC.
 	 */
	protected String getAggMAC(String mac0, String mac1, byte[] skey, int iterationNum) throws Exception
	{
		// construct mac0 + mac1
		StringBuffer sb = new StringBuffer();
		sb.append(mac0);
		sb.append(mac1);
		String data = sb.toString();
		
 		SecretKeySpec signingKey = new SecretKeySpec(skey, CRYPTO_HMAC_MD5_ALGORITHM);

 		// get an hmac_sha1 Mac instance and initialize with the signing key
 		Mac mac = Mac.getInstance(CRYPTO_HMAC_MD5_ALGORITHM);
 		mac.init(signingKey);

 		// compute the hmac on input data bytes
		byte[] aggmac = mac.doFinal(data.getBytes());
		//System.out.println("AGGMAC, first hash:" + HexByte.convertBytesToHex(aggmac));

		int i = 0;
		for (i = 0; i < iterationNum; i++) 
		{
			mac.reset();
			aggmac = mac.doFinal(aggmac);
			//System.out.println("AGGMAC, "+i+"-th hash: " + HexByte.convertBytesToHex(aggmac));
		}
		//System.out.println("AGGMAC, hashed "+(i+1)+" times!");

		return convertBytesToHex(aggmac);
	}
	
	/*
	 * Purpose: This method converts a set of bytes into a Hexadecimal representation.
	 */
	 public static String convertBytesToHex(byte[] buf) {
		StringBuffer strBuf = new StringBuffer();

		for (int i = 0; i < buf.length; i++) {
			int byteValue = (int) buf[i] & 0xff;
			if (byteValue <= 15) {
				strBuf.append("0");
			}
			strBuf.append(Integer.toString(byteValue, 16));
		}
		return strBuf.toString();
	}

	/*
	 * Purpose: This method converts a specified hexadecimal String into a set of bytes.
	 */
	public static byte[] convertHexToBytes(String hexString) {
		int size = hexString.length();
		byte[] buf = new byte[size / 2];

		int j = 0;
		for (int i = 0; i < size; i++) {
			String a = hexString.substring(i, i + 2);
			int valA = Integer.parseInt(a, 16);

			i++;

			buf[j] = (byte) valA;
			j++;
		}

		return buf;
	}
}

