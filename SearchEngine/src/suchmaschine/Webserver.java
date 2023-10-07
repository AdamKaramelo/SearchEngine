package suchmaschine;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.stream.IntStream;

/**
 * This class connects a client with a server which is reachable through a web-browser under
 * the address <code>http://127.0.0.1:8003</code> where 8003 is the specified port.
 * The webpage has html configuration specified in <code>search.html</code>, which is parsed from
 * {@link TemplateProcessor} class.
 * The webpage can be queried in order to get the Documents listed descending, from most relevant. 
 *
 */
public class Webserver {
	private static TemplateProcessor tp;
	private static LinkedDocumentCollection ldc;

	public static void main(String[] args) throws IOException {
		tp = new TemplateProcessor("search.html");

		LinkedDocumentCollection temp = new LinkedDocumentCollection();
		temp.appendDocument(new LinkedDocument("A.txt", "", "", null, null, "ich bin A und verlinke link:Pinguine.txt niemanden, außer link:B.txt link:C.txt und link:D.txt", "A.txt"));
		ldc = temp.crawl();

		@SuppressWarnings("resource")
		ServerSocket serverSocket = new ServerSocket(8003);
		IntStream.iterate(0, i -> i + 1).forEach(i -> {
			Socket client;
			try {
				client = serverSocket.accept();
				WebserverThread wsThread = new WebserverThread(tp, ldc, client);
				System.out.println("Starting " + i);
				wsThread.start();
				System.out.println("Started " + i);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

}
