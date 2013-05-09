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


public class Main 
{
	// ==================================================
    // class static data
    // ==================================================

    // ==================================================
    // class static methods
    // ==================================================

    // ==================================================
    // instance data
    // ==================================================

    // ==================================================
    // constructors
    // ==================================================

    // ==================================================
    // public methods
    // ==================================================

	public static void main(String[] argv) 
	{
		if (argv.length < 1 || argv.length > 4 )
		{
			System.out.println("Error: must supply at least 1 argument");
			usage();
			System.exit(1);
		}
		else
		{
			
			DHtag dht = DHtag.getInstance();			
			// init dhtag generation
			try {
				dht.initialize(argv[1], argv[2], argv[3]);
			} catch (Exception e) {
				System.out.println("Unable to read DH prime values");
				e.printStackTrace();
				System.exit(1);
			}
			
			// communicate with cloud server
			try {
				dht.start();
			} catch (Exception e) {
				System.out.println("Unable to generate DH keys");
				e.printStackTrace();
				System.exit(1);
			}

			// start receiving data from log generators
			ServerListener cl = new ServerListener(argv[0]);
			cl.launchSever();
		}	
	}
	
    // ==================================================
    // non public methods
    // ==================================================

	private static void usage()
	{
		System.out.println("Usage: listenport  cloudhost cloudport monitorhost");
	}

}

