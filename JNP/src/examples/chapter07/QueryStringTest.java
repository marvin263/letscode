package examples.chapter07;
import com.macfaq.net.*;


public class QueryStringTest {

  public static void main(String[] args) {
    QueryString qs = new QueryString("pg", "q");
    qs.add("kl", "XX");
    qs.add("stype", "stext");
    qs.add("q", "+\"Java Network Programming\"");
    String url = "http://www.altavista.com/cgi-bin/query?" + qs;
    System.out.println(url);
  }

}