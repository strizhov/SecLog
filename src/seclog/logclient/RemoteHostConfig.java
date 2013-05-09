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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.Vector;

// class reads the configuration file that contains the list of ip;port of log generators.
// it stores host and port in array
public class RemoteHostConfig {
	
	private static RemoteHostConfig instance = new RemoteHostConfig();
	private static final String strDelims = " ";
	private String filename;
	private Vector<RemoteHost> hostvector;	

	
	private RemoteHostConfig() {}
	
	public static RemoteHostConfig getInstance()
	{		
		return instance;
	}
	
	public void initialize(String filename)
	{
		this.filename = filename;
		hostvector = new Vector<RemoteHost>();
	}
	
	public int getVectorSize()
	{
		return hostvector.size();
	}
	
	public RemoteHost getRemoteHost(int i)
	{
		return hostvector.elementAt(i);
	}
	
	private String getFilename()
	{
		return filename;
	}	
	
	private Vector<RemoteHost> getHostVector()
	{
		return hostvector;
	}
	
	public void readFile()
	{
		try
		{
			// Open the file
			FileInputStream fstream = new FileInputStream(getFilename());
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine = null;
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   
			{
				StringTokenizer st = new StringTokenizer(strLine, strDelims);
				String host = st.nextToken();
				String port = st.nextToken();
				RemoteHost rh = new RemoteHost(host, Integer.parseInt(port));
				getHostVector().addElement(rh);				
			}
			//Close the input stream
			in.close();
		} catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	 }
	
}
