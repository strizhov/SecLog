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

package seclog.test;

import java.math.BigInteger;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;

public class DiffieHellman {
	
	private BigInteger p; // prime modulus
	private BigInteger g; // base generator
	private int l;        // random exponent
	
	public DiffieHellman() {}

	public static void main(String[] argv) 
	{	
		DiffieHellman dh = new DiffieHellman();
		dh.init();		
		dh.start();		
	}
	
	public BigInteger getP()
	{
		return p;
	}
	
	public BigInteger getG()
	{
		return g;		
	}
	
	public int getL()
	{
		return l;
	}
		
	private void init()
	{		 
		try 
		{
			AlgorithmParameterGenerator paramGen = AlgorithmParameterGenerator.getInstance("DH");
			paramGen.init(1024); // DH is 1024 bit

			// Generate the parameters
			AlgorithmParameters params = paramGen.generateParameters();
			DHParameterSpec dhSpec = (DHParameterSpec)params.getParameterSpec(DHParameterSpec.class);

			// set p
			setP(dhSpec.getP());
			
			// set g
			setG(dhSpec.getG());
			
			// set l
			setL(dhSpec.getL());
			
			System.out.println("P: "+getP()+" G: "+getG()+" L: "+getL());
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidParameterSpecException e) {
			e.printStackTrace();
		}
	}
	
	private void start()
	{		
		try 
		{
			System.out.println("ALICE: Generate DH keypair ...");
			
			KeyPairGenerator aliceKpairGen = KeyPairGenerator.getInstance("DH");
		
			DHParameterSpec dhSpec = new DHParameterSpec(getP(), getG(), getL());
			aliceKpairGen.initialize(dhSpec);
			
			KeyPair aliceKpair = aliceKpairGen.generateKeyPair();

			// Alice creates and initializes her DH KeyAgreement object
			System.out.println("ALICE: Initialization ...");
			KeyAgreement aliceKeyAgree = KeyAgreement.getInstance("DH");
			aliceKeyAgree.init(aliceKpair.getPrivate());
		
			// Alice encodes her public key, and sends it over to Bob.
			byte[] alicePubKeyEnc = aliceKpair.getPublic().getEncoded();
			
			// send alice public key to bob
			
			
			/*
			 * Let's turn over to Bob. Bob has received Alice's public key
			 * in encoded format.
			 * He instantiates a DH public key from the encoded key material.
			 */
			KeyFactory bobKeyFac = KeyFactory.getInstance("DH");
		
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(alicePubKeyEnc);
			PublicKey alicePubKey = bobKeyFac.generatePublic(x509KeySpec);
			
			/*
			 * Bob gets the DH parameters associated with Alice's public key. 
			 * He must use the same parameters when he generates his own key
			 * pair.
			 */
			DHParameterSpec dhParamSpec = ((DHPublicKey)alicePubKey).getParams();

			// Bob creates his own DH key pair
			System.out.println("BOB: Generate DH keypair ...");
			KeyPairGenerator bobKpairGen = KeyPairGenerator.getInstance("DH");
			bobKpairGen.initialize(dhParamSpec);
			
			KeyPair bobKpair = bobKpairGen.generateKeyPair();

			// Bob creates and initializes his DH KeyAgreement object
			System.out.println("BOB: Initialization ...");
			KeyAgreement bobKeyAgree = KeyAgreement.getInstance("DH");

			bobKeyAgree.init(bobKpair.getPrivate());			

			// Bob encodes his public key, and sends it over to Alice.
			byte[] bobPubKeyEnc = bobKpair.getPublic().getEncoded();
			
			
			
			 /*
	         * Alice uses Bob's public key for the first (and only) phase
	         * of her version of the DH
	         * protocol.
	         * Before she can do so, she has to instanticate a DH public key
	         * from Bob's encoded key material.
	         */
	        KeyFactory aliceKeyFac = KeyFactory.getInstance("DH");
	        x509KeySpec = new X509EncodedKeySpec(bobPubKeyEnc);
	        PublicKey bobPubKey = aliceKeyFac.generatePublic(x509KeySpec);
	        System.out.println("ALICE: Execute PHASE1 ...");
	        aliceKeyAgree.doPhase(bobPubKey, true);
	        
	        
	        /*
	         * Bob uses Alice's public key for the first (and only) phase
	         * of his version of the DH
	         * protocol.
	         */
	        System.out.println("BOB: Execute PHASE1 ...");
	        bobKeyAgree.doPhase(alicePubKey, true);
        
	        
	        
	        /*
	         * At this stage, both Alice and Bob have completed the DH key
	         * agreement protocol.
	         * Both generate the (same) shared secret.
	         */
	        byte[] aliceSharedSecret = aliceKeyAgree.generateSecret();

	        
	        byte[] bobSharedSecret  = bobKeyAgree.generateSecret();
	        
	        
	        if (!java.util.Arrays.equals(aliceSharedSecret, bobSharedSecret))
	        {	
	        	throw new Exception("Shared secrets differ");
	        }
			
	        System.out.println("Shared secrets are the same");
	        
	        System.out.println("AliceSharedSecret: "+toHexString(aliceSharedSecret));
	        System.out.println("BobSharedSecret: "+toHexString(bobSharedSecret));
	        
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private void setP(BigInteger p)
	{
		this.p = p;
	}
	
	private void setG(BigInteger g)
	{
		this.g = g;
	}
	
	private void setL(int l)
	{
		this.l = l;
	}

	 /*
     * Converts a byte to hex digit and writes to the supplied buffer
     */
    private void byte2hex(byte b, StringBuffer buf) {
        char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
                            '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        int high = ((b & 0xf0) >> 4);
        int low = (b & 0x0f);
        buf.append(hexChars[high]);
        buf.append(hexChars[low]);
    }

    /*
     * Converts a byte array to hex string
     */
    private String toHexString(byte[] block) {
        StringBuffer buf = new StringBuffer();

        int len = block.length;

        for (int i = 0; i < len; i++) {
             byte2hex(block[i], buf);
             if (i < len-1) {
                 buf.append(":");
             }
        } 
        return buf.toString();
    }

}
