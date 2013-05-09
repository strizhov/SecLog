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

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.KeyAgreement;
import javax.crypto.spec.DHParameterSpec;

public class DH {
	
	private static final String DIFFIE_HELLMAN_ALGORIHM = "DH";
	
	public DH() {}
	
	public static KeyPair genKeyPair(BigInteger p, BigInteger g) throws Exception 
	{
		KeyPairGenerator kpg = KeyPairGenerator.getInstance(DIFFIE_HELLMAN_ALGORIHM);
		DHParameterSpec dhSpec = new DHParameterSpec(p, g);
		SecureRandom sr = new SecureRandom();
		kpg.initialize(dhSpec, sr);			
		KeyPair kp = kpg.generateKeyPair();	
		return kp;
	}
	
	public static byte[] genSharedSecret(byte[] PrivKeyEnc, byte[] RemotePubKeyEnc) throws Exception
	{
		// recreate private key from byte array
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(PrivKeyEnc);
		KeyFactory KeyFacPriv = KeyFactory.getInstance(DIFFIE_HELLMAN_ALGORIHM);
		PrivateKey PrivKey = KeyFacPriv.generatePrivate(spec);
	
		// recreate public key from byte array
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(RemotePubKeyEnc);
		KeyFactory KeyFacPub = KeyFactory.getInstance(DIFFIE_HELLMAN_ALGORIHM);
        PublicKey RemotePubKey = KeyFacPub.generatePublic(x509KeySpec);

		// create shared key
		KeyAgreement KeyAgree = KeyAgreement.getInstance(DIFFIE_HELLMAN_ALGORIHM);
		KeyAgree.init(PrivKey);
        KeyAgree.doPhase(RemotePubKey, true);
        
        byte[] sharedsecret = KeyAgree.generateSecret();
        return sharedsecret;
	}

}

