package examples.chapter15;
import java.net.*;
import java.io.*;
import com.macfaq.net.*;

public class FormPoster {

  private URL url;
  // from Chapter 7, Example 7-9
  private QueryString query = new QueryString(); 
   
  public FormPoster (URL url) {
    if (!url.getProtocol().toLowerCase().startsWith("http")) {
      throw new IllegalArgumentException(
       "Posting only works for http URLs");   
    }    
    this.url = url;
  }
  
  public void add(String name, String value) {
    query.add(name, value);    
  }
  
  public URL getURL() {
    return this.url; 
  }

  public InputStream post() throws IOException {

    // open the connection and prepare it to POST
    URLConnection uc = url.openConnection();
    uc.setDoOutput(true);
    OutputStreamWriter out 
     = new OutputStreamWriter(uc.getOutputStream(), "ASCII");

    // The POST line, the Content-type header,
    // and the Content-length headers are sent by the URLConnection.
    // We just need to send the data
    out.write(query.toString());
    out.write("\r\n");
    out.flush();
    out.close();


    // Return the response
     return uc.getInputStream();

  }

  public static void main(String args[]) {

    URL url;

    if (args.length > 0) {
      try {
        url = new URL(args[0]);
      }
      catch (MalformedURLException ex) {
        System.err.println("Usage: java FormPoster url");
        return;
      }
    }
    else {
      try {
        url = new URL(
          "http://www.cafeaulait.org/books/jnp3/postquery.phtml");
      }
      catch (MalformedURLException ex) { // shouldn't happen
        System.err.println(ex);
        return; 
      }
    }

    FormPoster poster = new FormPoster(url);
    poster.add("name", "Elliotte Rusty Harold");
    poster.add("email", "elharo@metalab.unc.edu");
    
    try {
      InputStream in = poster.post();
    
      // Read the response
      InputStreamReader r = new InputStreamReader(in);
      int c;
      while((c = r.read()) != -1) {
        System.out.print((char) c);
      }
      System.out.println();
      in.close();
    }
    catch (IOException ex) {
      System.err.println(ex);   
    }

  }

}
