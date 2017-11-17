package examples.chapter17;

import java.net.*;

public class TabFactory implements ContentHandlerFactory {

  public ContentHandler createContentHandler(String mimeType)) {

    if (mimeType.equals("text/tab-separated-values") {
      return new com.macfaq.net.www.content.text.tab_separated_values();
    }
    else {
      return null; // look for the handler in the default locations
    }
  }
}
