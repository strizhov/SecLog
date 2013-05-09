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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import seclog.logclient.util.Constants;
import seclog.util.Networking;
import seclog.util.TorNetworking;
import seclog.wireformats.CloudTagMessage;
import seclog.wireformats.util.TagConstants;

public class BatchCrypto extends Crypto implements Constants, TagConstants
{		
	long time1 = 0;
	long time2 = 0;
	
	// Previous MAC
	private String prevmac; // store previous MAC
	
	// Instance of class
	private static BatchCrypto instance = new BatchCrypto();
	
	// Batch
	private ArrayList<SealedLogEntry> batch = new ArrayList <SealedLogEntry>(BATCH_MAX_SIZE);
	
	/*
	 * Purpose: default constructor
	 */
	private BatchCrypto() {}
	
	/*
	 * Purpose: get instance of class
	 */
	public static BatchCrypto getInstance()
	{
		return instance;
	}	
	
	/*
	 * Purpose: process incoming log message. 
	 * This method is synchronized since multiple threads will call it
	 */
	public synchronized void processLogMessage(String logMessage)
	{
		
		try 
		{
			
			if (getBatchSize() == 0)
			{
				System.out.println("Starting collecting log entries...");
				System.out.println("Batch size: "+BATCH_MAX_SIZE);
				// 	create init log message and add it to batch
				SealedLogEntry sl0 = createSL0(BATCH_MAX_SIZE);
				addBatch(sl0);
            	
				time1 = System.currentTimeMillis();
				// add 1st log message to the batch
				SealedLogEntry sl = createSL(logMessage, BATCH_MAX_SIZE - getBatchSize());
				addBatch(sl);
				
			}  // subtract 1 because of init log message in array
			else if ( (getBatchSize() - 1) < BATCH_MAX_SIZE)
			{
				System.out.println("Adding another one...");
				SealedLogEntry sl = createSL(logMessage, BATCH_MAX_SIZE - getBatchSize());
				addBatch(sl);
			
				// Check if we fill up batch array
				// Ignore first init log message
				if ( (getBatchSize() - 1) == BATCH_MAX_SIZE)
				{
					time2 = System.currentTimeMillis();
					
					System.out.println("Batch size: "+BATCH_MAX_SIZE
							           +" Overhead: "+ (time2-time1));
					SealedLogEntry slc = createSLC();
					addBatch(slc);
					

					String outstr = System.currentTimeMillis()+"\n";
					BufferedWriter out = new BufferedWriter(new FileWriter("LogClient-log.txt", true));
					out.write(outstr);
					out.close();
					
					// Send the batch
					sendBatch();
					
					// Print everything that we have in batch
					//for (int i=0; i<getBatchSize(); i++)
					//{
					//	System.out.println(i+"-th log entry, " 
					//			+ "msg: " +getBatch().get(i).getLog() 
					//			+" mac: " +getBatch().get(i).getMAC()
					//			+" aggmac: " +getBatch().get(i).getAggMAC());
					//}
					
					//	empty batch
					clearBatch();
					
					// re-generate A, X, K keys for a new batch
					KeyCrypto.getInstance().initialize();
					// send keys
					//KeyCrypto.getInstance().sendKeys();

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Purpose: create init log object
	 * Input: number of entries in a whole batch
	 */
	private SealedLogEntry createSL0(int size) throws Exception
	{	
		// generate log init message
		String l0 = LogEntry.makeLog(size);
	
		// encrypt log:  use K key
		byte[] enc_l0 = encryptLogEntry(l0, KeyCrypto.getInstance().getKkey()); 		

		// calculate mac0
		String mac0 = getMAC(enc_l0, KeyCrypto.getInstance().getAkey());
		
		// create sealed log entry
		SealedLogEntry sl0 = new SealedLogEntry(	convertBytesToHex(enc_l0), 
													mac0);
		
		// recompute A, X, K keys
		KeyCrypto.getInstance().reComputeKeys();
		
		// save previous mac
		setPrevMac(mac0);
		
		return sl0;
	}
	
	/*
	 * Purpose: create N-th log object
	 * Input: log string, int how many times hash aggmac
	 */
	private SealedLogEntry createSL(String log, int hashtimes) throws Exception
	{

		// make log string: concatenate log with previoud MAC
		String l = LogEntry.makeLog(log, getPrevMac()); 
		
		// encrypt log string with key K		
		byte[] enc_l = encryptLogEntry(l, KeyCrypto.getInstance().getKkey());
		
		//get MAC out of encrypted M1 with A1
		String mac = getMAC(enc_l, KeyCrypto.getInstance().getAkey());
		
		//create aggregated MAC out of previous MAC and the present MAC with X1
		//String aggmac = getAggMAC(	getPrevMac(), 
		//							mac, 
		//							KeyCrypto.getInstance().getXkey(),
		//							hashtimes);
	
		//update previous MAC
		setPrevMac(mac);
		
		//create batch entry as a combination of encrypted M1 and MAC and AGGMAC
		SealedLogEntry sl = new SealedLogEntry(		convertBytesToHex(enc_l),
													mac); 

		// recompute A, X, K keys
		KeyCrypto.getInstance().reComputeKeys();
		
		return sl;
	}
	
	/*
	 * Purpose: create log close message entry
	 */	
	private SealedLogEntry createSLC() throws Exception
	{		
		// make close log message
		String lc = LogEntry.makeLog(getPrevMac());
		
		// encrypt close log message with K key
		byte[] enc_lc = encryptLogEntry(lc, KeyCrypto.getInstance().getKkey()); 
		
		// calculate MAC with A key
		String mac = getMAC(enc_lc, KeyCrypto.getInstance().getAkey());
		
		// calculate AggMAC
		String aggmac = getAggMAC(	getPrevMac(), 
									mac, 
									KeyCrypto.getInstance().getXkey(),
									0); // zero here because we dont want hash it multiple times
		
		SealedLogEntry slc = new SealedLogEntry(	convertBytesToHex(enc_lc), 
													mac, 
													aggmac);
			
		return slc;
	}	
	
	/*
	 * Purpose: send batch to the cloud using TOR 
	 */
	private void sendBatch() throws Exception
	{
		// open connection to cloud server
		Socket s = Networking.openSocket(DHtag.getInstance().getCloudHostname(), 
										 DHtag.getInstance().getCloudPort());
		
		
		// TOR!!!
		//Socket s = TorNetworking.TorSocket(DHtag.getInstance().getCloudHostname(), 
		//					DHtag.getInstance().getCloudPort());

		ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
		
		// get hash
		byte[] taghash = DHtag.getInstance().getSharedSecret();
		
		// create new cloud tag message
		CloudTagMessage ctm = new CloudTagMessage(	LOG_UPLOAD,
													convertBytesToHex(taghash),
													null,
													null,
													getBatch());
		
		
		oos.writeObject(ctm);
		oos.flush();
		
	}
	
	/*
	 * Purpose: set previous MAC
	 */
	private void setPrevMac(String mac)
	{
		this.prevmac = mac;
	}
	
	/*
	 * Purpose: get previous MAC
	 */
	private String getPrevMac()
	{
		return prevmac;
	}
	
	/*
	 * Purpose: return Batch array
	 */
	private ArrayList<SealedLogEntry> getBatch()
	{
		return batch;
	}
	
	/*
	 * Purpose: add log entry object to the batch
	 */
	private void addBatch(SealedLogEntry obj)
	{
		batch.add(obj);
	}
	
	/*
	 * Purpose: return batch size
	 */
	private int getBatchSize()
	{
		return batch.size();
	}
	
	/*
	 * Purpose: clean up batch
	 */
	private void clearBatch()
	{
		batch.clear();
	}
}
