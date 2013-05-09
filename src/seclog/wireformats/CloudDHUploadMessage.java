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

public class CloudDHUploadMessage extends CloudMessageType 
{
	private static final long serialVersionUID = -4195137471307097860L;
	private String 	id;
	private long 	ts;
	private byte[]	deletetag;
	private byte[]	opt;
	private byte[]	data;
	private byte[]	digest;
	
	public CloudDHUploadMessage(int type, String id, byte[] deletetag, byte[] opt, byte[] data)
	{
		super(type);
		this.id = id;
		this.ts = System.currentTimeMillis();
		this.deletetag = deletetag;
		this.opt = opt;
		this.data = data;
		this.digest = getDigest(id, ts, deletetag, opt, data);
	}
	
	/*
	 * TODO:  Calculate digest from whole message
	 */
	private byte[] getDigest(String id, 
							 long ts, 
							 byte[] deletetag, 
							 byte[] opt, 
							 byte[] data)
	{
		String test = "digest!";
		return test.getBytes();
	}
	
	public String getID()
	{
		return id;
	}
	
	public long getTS()
	{
		return ts;
	}
	
	public byte[] getDeleteTag()
	{
		return deletetag;
	}
	
	public byte[] getOpt()
	{
		return opt;
	}
	
	public byte[] getData()
	{
		return data;
	}
	
	public byte[] getDigest()
	{
		return digest;
	}
	
}
