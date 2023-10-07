package suchmaschine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class TestItThread extends Thread{
	private SynchronizedLdcWrapper ldc;
	private Socket client;
	
	public TestItThread(SynchronizedLdcWrapper ldc, Socket client) {
		this.ldc = ldc;
		this.client = client;
	}
	
	public void run() {
		System.out.println("*** Client connected!");
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));
			try {
				communicate(in, out);
	        } catch (IOException exp) {
	        } finally {
	          out.close();
	          client.close();
	          System.out.println("Finished request");
	        }
		}catch (IOException e) {
			System.out.println(e.getStackTrace());
		}
	}
	
	private static String askString(BufferedReader in, PrintWriter out, String prompt) throws IOException {
	    out.print(prompt);
	    out.flush();
	    return in.readLine();
	  }

	  private void communicate(BufferedReader in, PrintWriter out) throws IOException {

	    String command;

	    boolean exit = false;

	    while (!exit) {
	      command = askString(in, out, "> ");
	      if(command == null)
	        break;

	      if (command == null || command.equals("exit")) {
	        /* Exit program */
	        exit = true;
	      } else if (command.startsWith("add ")) {
	        /* add a new document */
	        String titleAndText = command.substring(4);

	        /* title and text separated by : */
	        int separator = titleAndText.indexOf(':');
	        String title = titleAndText.substring(0, separator);
	        String text = titleAndText.substring(separator + 1);

	        ldc.appendDocument(new LinkedDocument(title, "", "", null, null, text, title));
	      } else if (command.startsWith("list")) {
	        /* list all document in collection */
	    	  this.ldc.forEach(doc -> {
	    		  out.println(doc.getTitle());
	    	});
	          
	        
	      } else if (command.startsWith("query ")) {
	        /* query on the documents in the collection */
	        String query = command.substring(6);
	        ldc.query(query, (doc, relevance) -> {
	        	String msg = doc.getTitle() + "; Relevanz: " + relevance;
	        	out.println(msg);
	        });  
	        
//	        out.println();
	      } else if (command.startsWith("count ")) {
	        /* print the count of a word in each document */
	        String word = command.substring(6);
	        ldc.forEach(doc -> {
	        	WordCountsArray docWordCounts = doc.getWordCounts(); 
	        	int count = docWordCounts.getCount(docWordCounts.getIndexOfWord(word));
	          /* -1 and 0 makes a difference! */
	        	if (count == -1) {
	        		out.println(doc.getTitle() + ": gar nicht.");
	        	} else {
	        	  out.println(doc.getTitle() + ": " + count + "x ");
	        	}
	        });
	        
	      } else if (command.startsWith("pageRank")) {
	    	  
	    	ldc.pageRank((doc, relevance) -> {
	    		out.println(doc.getTitle() + "; PageRank: " + relevance);
	    	});
	       
	      } else if (command.startsWith("crawl")) {
	    	  ldc.crawl();
	      }
	      
	      out.flush();
	    }
	  }

}
