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

package seclog.wireformats;

import java.util.ArrayList;

import seclog.logclient.SealedLogEntry;

public class CloudTagMessage extends CloudMessageType
{
	private static final long serialVersionUID = -657290034479367566L;
	private String  tag;
	private long 	ts;
	private String	deletetag;
	private byte[]	opt;
	private ArrayList<SealedLogEntry> batch;
	private byte[]	digest;
	
	public CloudTagMessage(	int type, 
							String tag, 
							String deletetag, 
							byte[] opt, 
							ArrayList<SealedLogEntry> batch) 
	{
		super(type);
		this.tag = tag;
		this.ts = System.currentTimeMillis();
		this.deletetag = deletetag;
		this.opt = opt;
		this.batch = batch;
		this.digest = calcDigest();		
	}
	
	/*
	 * TODO:  Calculate digest from whole message
	 */
	private byte[] calcDigest()
	{
		String test = "digest!";
		return test.getBytes();
	}
	
	public String getTag()
	{
		return tag;
	}
	
	public long getTS()
	{
		return ts;
	}
	
	public String getDeleteTag()
	{
		return deletetag;
	}
	
	public byte[] getOpt()
	{
		return opt;
	}
	
	public ArrayList<SealedLogEntry> getBatch()
	{
		return batch;
	}
	
	public byte[] getDigest()
	{
		return digest;
	}
}
