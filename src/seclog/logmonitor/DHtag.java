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

package seclog.logmonitor;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.security.KeyPair;

import seclog.dh.util.GenPrimesConstants;
import seclog.util.DH;
import seclog.util.FileIO;
import seclog.util.Networking;
import seclog.wireformats.CloudDHRequestMessage;
import seclog.wireformats.CloudDHUploadMessage;
import seclog.wireformats.CloudMessageType;
import seclog.wireformats.util.TagConstants;

public class DHtag implements GenPrimesConstants, TagConstants 
{
	private static final long WAIT_TIME = 30000; // 30 sec
	
	private String cloudhostname;
	private int    cloudport;
	private String clienthostname;
	
	// DH values
	private BigInteger p;
	private BigInteger g;
	private KeyPair    kp; // public and private keys
	private byte[]	   ss; // secret share	
	
	/*
	 * Constructor
	 */
	public DHtag(String cloudhostname, String cloudport, String clienthostname) 
	{
		this.cloudhostname = cloudhostname;
		this.cloudport = Integer.parseInt(cloudport);
		this.clienthostname = clienthostname;
	}
	
	/*
	 * Read P an G value from filesystem
	 */
	public void initialize() throws Exception
	{
		// get and set P value 
		byte[] pbyte = FileIO.readByteArrayFromFile(PFILENAME);
		BigInteger p = new BigInteger(pbyte);
		setP(p);			
			
		// get and set G value
		byte[] gbyte = FileIO.readByteArrayFromFile(GFILENAME);
		BigInteger g = new BigInteger(gbyte);
		setG(g);
	}
	
	/*
	 * Method generates key pair, sends it to the cloud
	 *  and later retrieves remote public key 
	 *  to calculate shared secret
	 */
	public void start() throws Exception
	{
		// generate key pair
		KeyPair kp = DH.genKeyPair(getP(), getG());
		// set key pair 
		setDHKeyPair(kp);
			
		// send alice public key to cloud
		sendPubKeyToCloud(kp.getPublic().getEncoded());	
		
		// ask cloud for remote public key
		getRemotePubKeyEnc();
	}
	
	/*
	 * Open socket connection to the cloud and send data with public key
	 */
	private void sendPubKeyToCloud(byte[] PubKeyEnc) 
	{
		try
		{
			Socket sock = Networking.openSocket(getCloudHostname(), getCloudPort());
			ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
			CloudDHUploadMessage ctm = new CloudDHUploadMessage(	DHTAG_UPLOAD,     // type
												Networking.getHostName(), // hostname
												null,  // deletetag
												null,  // opt
												PubKeyEnc // data
											);
			oos.writeObject(ctm);
			oos.flush();
		} catch (Exception e) {
			System.out.println("Unable to send data to the cloud");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private void getRemotePubKeyEnc()
	{
		int loop = 1;
		while (loop == 1)
		{
			try
			{
				Socket sock = Networking.openSocket(getCloudHostname(), getCloudPort());
				ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
				ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
			
				// send request packet to the cloud
				CloudDHRequestMessage crm = 
						new CloudDHRequestMessage(DHTAG_REQUEST, getClientHostname());
				oos.writeObject(crm);
				oos.flush();
			
				// receive reply
				CloudMessageType ctmt = (CloudMessageType)ois.readObject();
				switch(ctmt.getCloudTagMessageType())
				{
					case DHTAG_REPLY:
						processDHReply(ctmt);
						loop = 0;
						break;
					case DHTAG_NA:						
						break;
					default:
						System.out.println("Unknown cloud message type");
						break;
				}			
			} catch (Exception e) {
				System.out.println("Unable to connect to the cloud");
				e.printStackTrace();
			}
			
			// wait WAIT_TIME  seconds and then send request to cloud again
			try {
				Thread.sleep(WAIT_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * Extract remote public key to generate DH shared secret
	 */
	private void processDHReply(CloudMessageType ctmt)
	{
		// extract remote public key
		CloudDHUploadMessage ctm = (CloudDHUploadMessage)ctmt;
		byte[] remotePubKeyEnc = ctm.getData();
		
		// get shared secret
		try 
		{
			byte[] sharedsecret = 
					DH.genSharedSecret(getDHKeyPair().getPrivate().getEncoded(), 
									   remotePubKeyEnc
									   );
			setSharedSecret(sharedsecret);
			
			System.out.println("Shared secret in hex: " +convertBytesToHex(sharedsecret));
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/*
	 * TESTONLY!!!!!
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
	
	private String getCloudHostname()
	{
		return cloudhostname;
	}
	
	private int getCloudPort()
	{
		return cloudport;
	}
	
	private String getClientHostname()
	{
		return clienthostname;
	}
	
	private void setP(BigInteger p)
	{
		this.p = p;
	}
	
	private BigInteger getP()
	{
		return p;
	}
	
	private void setG(BigInteger g)
	{
		this.g = g;
	}
	
	private BigInteger getG()
	{
		return g;
	}
	
	private void setDHKeyPair(KeyPair kp)
	{
		this.kp = kp;
	}
	
	protected KeyPair getDHKeyPair()
	{
		return kp;
	}
	
	private void setSharedSecret(byte[] ss)
	{
		this.ss = ss;		
	}
	
	protected byte[] getSharedSecret()
	{
		return ss;
	}

}
