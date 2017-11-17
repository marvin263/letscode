package examples.chapter17;
import java.net.*;

public class FitsFactory implements ContentHandlerFactory {

  public ContentHandler createContentHandler(String mimeType) {
  
    if (mimeType.equalsIgnoreCase("image/x-fits")) {
      return new com.macfaq.net.www.content.image.x_fits();
    }
    return null;
  
  }
}
