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

import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;

import seclog.util.Networking;
import seclog.util.Shamir;
import seclog.util.ShamirKey;
import seclog.wireformats.ShamirKeyMessage;
import seclog.wireformats.ShamirMessageType;


public class KeyCrypto extends Crypto{
	
	private static KeyCrypto instance = new KeyCrypto(); // private instance of KeyCrypto class
	
	// Three keys
	private byte[] Akey;
	private byte[] Xkey;
	private byte[] Kkey;
	
	private KeyCrypto() {}
	
	public static KeyCrypto getInstance()
	{
		return instance;		
	}
	
	/* 
	 * Purpose: initialize keys
	 */
	protected void initialize() 
	{		
		try {			
			// generate A key
			setAkey(getSecretKey());
			// generate X key
			setXkey(getSecretKey());
			// generate K key
			setKkey(getSecretKey());			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Purpose: recalculate keys, create hash of each key
	 */
	protected void reComputeKeys() throws Exception 
	{
		setAkey(getHash(getAkey()));
		setXkey(getHash(getXkey()));
		setKkey(getHash(getKkey()));		
	}
		
	/*
	 * Purpose: send keys to remote hosts
	 */
	public void sendKeys()
	{
		// check if number of hosts if bigger than the number of shares to solve secret
		int numofhosts = RemoteHostConfig.getInstance().getVectorSize();
		if (SHAMIR_SHARES_MIN_NUM > numofhosts)
		{
			try {
				throw new Exception("Number of hosts should be bigger than number of secret shares");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
	
		// A key	
		ShamirKey[] Ask = getShamirShares(getAkey(), numofhosts);
		sendKey(Ask, ShamirMessageType.A_KEY);
		
		// X key
		ShamirKey[] Xsk = getShamirShares(getXkey(), numofhosts);
		sendKey(Xsk, ShamirMessageType.X_KEY);
		
		// K key
		ShamirKey[] Ksk = getShamirShares(getKkey(), numofhosts);
		sendKey(Ksk, ShamirMessageType.K_KEY);
	}
	
	/*
	 * Function generates Shamir's key shares and return an array
	 */
	private ShamirKey[] getShamirShares(byte[] secret, int numofhosts)
	{		
		ShamirKey[] sk = null;
		BigInteger[] s = null;
			
		try {
			s = Shamir.generateParameters(SHAMIR_SHARES_MIN_NUM, SHAMIR_CIPHER_SIZE, secret);
			sk = Shamir.generateKeys(numofhosts, SHAMIR_SHARES_MIN_NUM, SHAMIR_CIPHER_SIZE, s);
		} catch (Exception e) {
			System.out.println("Unable to calcualte Shamirs keys: " + e.getMessage());
			e.printStackTrace();
		}
		return sk;
	}
	
	/*
	 * Purpose: send keys
	 */
	private void sendKey(ShamirKey[] sk, int type)
	{
		// socket for connection
		Socket s = null;
		ObjectOutputStream oos = null;
		// go over each host and send key
		for (int i=0; i< RemoteHostConfig.getInstance().getVectorSize(); i++)
		{
			s = null;
			oos = null;
			String hostname = RemoteHostConfig.getInstance().getRemoteHost(i).getHostname();
			int port = RemoteHostConfig.getInstance().getRemoteHost(i).getPort();
						
			// open socket
			try {
				s = Networking.openSocket(hostname, port);
				oos = new ObjectOutputStream(s.getOutputStream());
				// create message
				ShamirKeyMessage akm = new ShamirKeyMessage(sk[i], type);
				System.out.println("Sending "+type+" type, key: "+sk[i].getF()+
						" to: "+hostname+" port: " +port);
				oos.writeObject(akm);
				oos.flush();		
			} catch (Exception e) {	
				System.out.println("Unable to connect to host: "+hostname+" port: "+port);
				e.printStackTrace();
			}
		}
	}
	
	protected void setAkey(byte[] key)
	{
		this.Akey = key;
	}
	
	protected void setXkey(byte[] key)
	{
		this.Xkey = key;
	}
	
	protected void setKkey(byte[] key)
	{
		this.Kkey = key;
	}
	
	protected byte[] getAkey()
	{
		return Akey;
	}
		
	protected byte[] getXkey()
	{
		return Xkey;
	}

	protected byte[] getKkey()
	{
		return Kkey;
	}
}
