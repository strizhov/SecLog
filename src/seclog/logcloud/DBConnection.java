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
 *      Authors: Kirill Belyaev
 *      Date: May 9, 2012
 */

package seclog.logcloud;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import seclog.logcloud.util.CloudConstants;
import seclog.wireformats.CloudTagMessage;

public class DBConnection implements CloudConstants
{
	private Connection conn;

	/*
	 * Purpose: default constructor
	 */
	public DBConnection() { }
		
	/*
	 * Purpose: insert log batch to the cloud
	 */
	public void storeLog(CloudTagMessage ctm)
	{
	    // set connection
	    try 
	    {
	    	// register JDBC
			Class.forName(DB_JDBC_DRIVER);
			
			setConnection(DriverManager.getConnection(DB_SERVER, DB_USER, DB_PASSWD));
		
			// convert object to byte array
			byte[] data = toByteArray(ctm);
        		
			// prepare sql statement
			PreparedStatement ps = getConnection().prepareStatement(DB_LOG_INSERT);
			ps.setObject(1, ctm.getTag());
			ps.setObject(2, ctm.getDeleteTag());
			ps.setObject(3, data);
			ps.executeUpdate();
			
			// close connection
			getConnection().close();
	    	    
			System.out.println("Batch is sent to DB, tag: "+ctm.getTag());
	    } catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} 
		
	}
	
	private Connection getConnection()
	{
		return conn;
	}
	
	private void setConnection(Connection conn)
	{
		this.conn = conn;
	}

	/*
	 * Purpose: convert object to a byte array
	 */
	public byte[] toByteArray (Object obj)
	{
		byte[] bytes = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(bos); 
			oos.writeObject(obj);
			oos.flush(); 
			oos.close(); 
			bos.close();
			bytes = bos.toByteArray ();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return bytes;
	}
    
	/*
	 * Purpose: convert byte array to object
	 */
	public Object toObject (byte[] bytes)
	{
		Object obj = null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream (bytes);
			ObjectInputStream ois = new ObjectInputStream (bis);
			obj = ois.readObject();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return obj;
	}


}//end of connection
