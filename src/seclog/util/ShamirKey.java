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

import java.io.Serializable;
import java.math.BigInteger;
/**
 * <p> Contains a Shamir's key <br>
 * f = poly(x) mod p </p> 
 */
public class ShamirKey implements Serializable{

	 private static final long serialVersionUID = 1L;
	 
	 private BigInteger p;
     private BigInteger f;
     private BigInteger x;
	
     /**
      * Set p value
      * @param p Prime
     */
     public void setP(BigInteger p){this.p = p;}

     /**
      * Set f value
      * @param f Polynomial result
     */
     public void setF(BigInteger f){this.f = f;}

     /**
      * Set x value
      * @param x Public part
     */
     public void setX(BigInteger x){this.x = x;}     

     /**
      * Set p value
      * @return p
     */      
     public BigInteger getP(){return p;}

     /**
      * Set f value
      * @return f
     */      
     public BigInteger getF(){return f;}     

     /**
      * Set x value
      * @return x
     */      
     public BigInteger getX(){return x;}

}
