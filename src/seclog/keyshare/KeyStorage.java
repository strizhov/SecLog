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

package seclog.keyshare;

import seclog.util.ShamirKey;
import seclog.wireformats.ShamirKeyMessage;
import seclog.wireformats.ShamirMessageType;

public class KeyStorage {
	
	private static KeyStorage instance = new KeyStorage();
	private ShamirKey Akey;
	private ShamirKey Xkey;
	private ShamirKey Kkey;
	
	private KeyStorage() {}
	
	public static KeyStorage getInstance() {
	      return instance;
	}
	
	public ShamirKey getAkey()
	{
		return Akey;
	}
	
	public ShamirKey getXKey()
	{
		return Xkey;
	}
	
	public ShamirKey getKkey()
	{
		return Kkey;
	}
	
	public void addKey(ShamirMessageType mt)
	{
		ShamirKeyMessage akm = (ShamirKeyMessage)mt;
		switch(akm.getKeyType())
		{
			case ShamirMessageType.A_KEY:
				System.out.println("I received share of A key: "+akm.getValue().getF());
				setAkey(akm.getValue());
				break;
			case ShamirMessageType.X_KEY:
				System.out.println("I received share of X key: "+akm.getValue().getF());
				setXkey(akm.getValue());
				break;
			case ShamirMessageType.K_KEY:
				System.out.println("I received share of K key: "+akm.getValue().getF());
				setKkey(akm.getValue());
				break;
			default:
				System.out.println("Received unknown key type");
				break;
		}
	}	
	
	public void getKey(ShamirMessageType mt)
	{
		
	}

	private void setAkey(ShamirKey key)
	{
		this.Akey = key;
	}
	
	private void setXkey(ShamirKey key)
	{
		this.Xkey = key;
	}
	private void setKkey(ShamirKey key)
	{
		this.Kkey = key;
	}
	
}
