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
import java.util.Random;


public class Shamir {

    public static boolean isRepeat(BigInteger x, ShamirKey[] k)
    {
        if(k.length == 0)
                return false;
        
        for(int i = 0; i < k.length; i++)
        {
                if(k[i] == null)
                        break;
                if(k[i].getX() == x)
                        return true;            
        }
        
        return false;
    }

    public static BigInteger calculatePolynomial(BigInteger s[], BigInteger x, BigInteger p)
    {
        BigInteger result = BigInteger.ZERO;
        for(int i = 0; i < s.length; i++)
                result = result.add(s[i].multiply(x.pow(i)));
        
        result = result.mod(p);
        return result;
    }       
    
    public static BigInteger[] generateParameters(int t, int numBits, byte[] SecretBytes) throws Exception
    {
        BigInteger secret = new BigInteger(SecretBytes);
        //System.out.println("Secret len: " + secret.bitLength() + "Numbits: "+ numBits);
        if(secret.bitLength() >= numBits)
                throw new Exception("numBits is too small");
                                        
        BigInteger s[] = new BigInteger[t];
        s[0] = secret;
        //System.out.println("s(0) = " + secret + " (secret)" );
        
        for(int i = 1; i < t; i++)
        {
                s[i] = new BigInteger(numBits, new Random());
                //System.out.println("s("+i+") = " +s[i]);
        }
        
        return s;
    }
    
    /*
     * function generates keys
     */
    public static ShamirKey[] generateKeys(int n, int t, int numBits, BigInteger[] s)throws Exception
    {
        ShamirKey[] keys = new ShamirKey[n];
        if(s[0].bitLength() >= numBits)
                throw new Exception("numBits is too small");                    
        if(t > n)
                throw new Exception("number of need shares greater than number of shares");

        BigInteger prime = BigInteger.probablePrime(numBits, new Random());

        BigInteger fx, x;
        for(int i = 1; i <= n; i++)
        {
        	do
        	{
        		x = new BigInteger(numBits, new Random());
            } while(isRepeat(x, keys));              
            fx = calculatePolynomial(s, x, prime);
            keys[i-1] = new ShamirKey();
            keys[i-1].setP(prime);
            keys[i-1].setX(x);
            keys[i-1].setF(fx);
            //System.out.println(i+"-> f("+x+") = " +keys[i-1].getF());
        }       
        return keys;
    }
    
    /*
     * solve scheme, calculate parameter 0 (secret)
     */
    public static byte[] calculateLagrange(ShamirKey[] sk)
    {
        BigInteger p = sk[0].getP();            
        BigInteger d;
        BigInteger D;           
        BigInteger c;
        BigInteger S = BigInteger.ZERO;         
        for(int i = 0; i < sk.length; i++)
        {
        	d = BigInteger.ONE;
            D = BigInteger.ONE;             
            for(int j = 0; j < sk.length; j++)
            {
            	if(j==i)
            		continue;                                       
                d = d.multiply(sk[j].getX());
                D = D.multiply(sk[j].getX().subtract(sk[i].getX()));
            }
            c = d.multiply(D.modInverse(p)).mod(p);
            S = S.add(c.multiply(sk[i].getF())).mod(p);
        }
        return S.toByteArray();
    }
}
