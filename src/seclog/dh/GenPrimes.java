/* 
 * 	Copyright (c) 2012 Colorado State University
 * 
 *	Permission is hereby granted, free of charge, to any person
 *	obtaining a copy of this software and associated documentation
 *	files (the "Software"), to deal in the Software without
 *	restriction, including without limitation the rights to use,
 *	copy, modify, merge, publish, distribute, sublicense, and/or
 *	sell copies of the Software, and to permit persons to whom
 *	the Software is furnished to do so, subject to the following
 *	conditions:
 *
 *	The above copyright notice and this permission notice shall be
 *	included in all copies or substantial portions of the Software.
 *
 *	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *	EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *	OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *	NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *	HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *	WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *	FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *	OTHER DEALINGS IN THE SOFTWARE.
 * 
 * 	Authors: Mikhail Strizhov
 *	Date: May 9, 2012
 */


package seclog.dh;

import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.DHParameterSpec;

import seclog.dh.util.GenPrimesConstants;
import seclog.util.FileIO;


public class GenPrimes implements GenPrimesConstants {

	public GenPrimes() {}
	
	public static void main(String[] args) {
		GenPrimes gp = new GenPrimes();
		gp.start();
	}
	
	private void start()
	{
		try 
		{
			AlgorithmParameterGenerator paramGen = AlgorithmParameterGenerator.getInstance("DH");
			paramGen.init(DH_SIZE); 

			// Generate the parameters
			AlgorithmParameters params = paramGen.generateParameters();
			DHParameterSpec dhSpec = (DHParameterSpec)params.getParameterSpec(DHParameterSpec.class);

			// save P prime
			FileIO.saveByteArrayToFile(PFILENAME, dhSpec.getP().toByteArray());
		
			// set g
			FileIO.saveByteArrayToFile(GFILENAME, dhSpec.getG().toByteArray());
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidParameterSpecException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
