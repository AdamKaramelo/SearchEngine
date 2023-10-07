package suchmaschine;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Here you can test the functionality of the server-client connection.
 * The communication is performed by {@link TestItThread} class, which also tests the client inputs and
 * acts accordingly. Multiple clients can be connected using multiple threads. 
 * After running the main method, connect to the specified port using a terminal:
 * <code>telnet 127.0.0.1 8001</code>.
 * You can then enter the desired commands: <code>add</code>, <code>crawl</code>, <code>query</code>, <code>pageRank</code>...
 *
 */

public class TestItServer {
	
  public static void main(String[] args) throws IOException {
	  
	SynchronizedLdcWrapper ldc = new SynchronizedLdcWrapper();
    @SuppressWarnings("resource")
    ServerSocket serverSocket = new ServerSocket(8001);
    while (true) {
      java.net.Socket client = serverSocket.accept(); //for each client make a thread
      TestItThread testThread = new TestItThread(ldc, client);
      System.out.println("Client connected!");
      testThread.start();
    }
  }

}
