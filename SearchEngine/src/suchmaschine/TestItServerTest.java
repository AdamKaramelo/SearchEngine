package suchmaschine;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.Test;

public class TestItServerTest {
  private static void testPrompt(BufferedReader in) throws IOException {
    char[] buf = new char[2];
    in.read(buf);
    assertArrayEquals(new char[] { '>', ' ' }, buf);
  }
  
  private static void skipNewline(BufferedReader in) throws IOException {
    in.mark(100);
    char c = (char)in.read();
    if(c == '\n')
      return;
    in.reset();
  }

  public static void testClient() throws UnknownHostException, IOException {
    Socket socket = new Socket("127.0.0.1", 8003);

    try {
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

      testPrompt(in);
      out.println("add B.txt:link:A.txt link:E.txt");
      testPrompt(in);
      out.println("add A.txt:blablabla link:B.txt tralalalal link:C.txt tetsetse ende");
      testPrompt(in);
      out.println("add C.txt:dies ist die datei c die auf datei d verlinkt link:D.txt");
      testPrompt(in);
      out.println("add D.txt:es ist so ein schoener tag.. verlink einfach mal auf datei c link:C.txt");
      testPrompt(in);
      out.println("add E.txt:pinguine link:Pinguine.txt verlinken ueblich nur auf auf link:C.txt");
      testPrompt(in);
      out.println("crawl");
      testPrompt(in);
      
      out.println("query einmal");
      assertEquals("1. D.txt; Relevanz: 0.28615880434508845", in.readLine().trim());
      assertEquals("2. C.txt; Relevanz: 0.14554138926995028", in.readLine().trim());
      assertEquals("3. A.txt; Relevanz: 0.03854293763345878", in.readLine().trim());
      assertEquals("4. B.txt; Relevanz: 0.024952177065648565", in.readLine().trim());
      assertEquals("5. Tierchen.txt; Relevanz: 0.02278451036368182", in.readLine().trim());
      assertEquals("6. E.txt; Relevanz: 0.01917610382432923", in.readLine().trim());
      assertEquals("7. Pinguine.txt; Relevanz: 0.01672127269676851", in.readLine().trim());
      skipNewline(in);
      testPrompt(in);

      out.println("query datei");
      assertEquals("1. C.txt; Relevanz: 0.31378237144529836", in.readLine().trim());
      assertEquals("2. D.txt; Relevanz: 0.24228781996357535", in.readLine().trim());
      assertEquals("3. A.txt; Relevanz: 0.038542937633458785", in.readLine().trim());
      assertEquals("4. B.txt; Relevanz: 0.02495217706564857", in.readLine().trim());
      assertEquals("5. Tierchen.txt; Relevanz: 0.022784510363681825", in.readLine().trim());
      assertEquals("6. E.txt; Relevanz: 0.01917610382432923", in.readLine().trim());
      assertEquals("7. Pinguine.txt; Relevanz: 0.01672127269676851", in.readLine().trim());
      skipNewline(in);
      testPrompt(in);
      
      out.println("list");

      assertEquals("C.txt", in.readLine().trim());
      assertEquals("D.txt", in.readLine().trim());
      assertEquals("A.txt", in.readLine().trim());
      assertEquals("B.txt", in.readLine().trim());
      assertEquals("Tierchen.txt", in.readLine().trim());
      assertEquals("E.txt", in.readLine().trim());
      assertEquals("Pinguine.txt", in.readLine().trim());
      skipNewline(in);
      testPrompt(in);
      
      out.println("count ein");
      assertEquals("C.txt: 0x", in.readLine().trim());
      assertEquals("D.txt: 1x", in.readLine().trim());
      assertEquals("A.txt: 0x", in.readLine().trim());
      assertEquals("B.txt: 0x", in.readLine().trim());
      assertEquals("Tierchen.txt: 0x", in.readLine().trim());
      assertEquals("E.txt: 0x", in.readLine().trim());
      assertEquals("Pinguine.txt: 0x", in.readLine().trim());
      skipNewline(in);
      testPrompt(in);

      out.println("pageRank");
      assertEquals("C.txt; PageRank: 0.3638534731748756", in.readLine().trim());
      assertEquals("D.txt; PageRank: 0.3307040228654089", in.readLine().trim());
      assertEquals("A.txt; PageRank: 0.09635734408364695", in.readLine().trim());
      assertEquals("B.txt; PageRank: 0.06238044266412142", in.readLine().trim());
      assertEquals("Tierchen.txt; PageRank: 0.05696127590920456", in.readLine().trim());
      assertEquals("E.txt; PageRank: 0.047940259560823074", in.readLine().trim());
      assertEquals("Pinguine.txt; PageRank: 0.041803181741921276", in.readLine().trim());
      skipNewline(in);
      testPrompt(in);

    } finally {
      socket.close();
    }
  }

  @Test(timeout = 3000)
  public void testSingle() throws UnknownHostException, IOException {
    /*
     * 
     * Working directory für den Server: suchmaschine_testdocs 
     */
    testClient();
  }
  
  @Test(timeout = 10000)
  public void testMulti() throws UnknownHostException, IOException {
    /*
     * 
     * Working directory für den Server: suchmaschine_testdocs 
     */
    testClient();
    testClient();
    testClient();
    testClient();
  }

}
