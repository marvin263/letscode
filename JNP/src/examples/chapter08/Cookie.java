package examples.chapter08;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Cookie {
  
  private String  version = "0";
  private String  name;
  private String  value;
  private URI   uri;
  private String  domain;
  private Date  expires;
  private String  path;
  private boolean secure = false;
  
  private static DateFormat expiresFormat 
    = new SimpleDateFormat("E, dd-MMM-yyyy k:m:s 'GMT'");
  
  // prevent instantiation
  private Cookie() {}
  
  public static Cookie bake(String header, URI uri)
    throws CookieException {
    
    try {
      String[] attributes = header.split(";");
      String nameValue = attributes[0];
      Cookie cookie = new Cookie();
      cookie.uri = uri;
      cookie.name = nameValue.substring(0, nameValue.indexOf('='));
      cookie.value = nameValue.substring(nameValue.indexOf('=')+1);
      cookie.path = "/";
      cookie.domain = uri.getHost();
      
      if (attributes[attributes.length-1].trim().equals("secure")) {
        cookie.secure = true;
      }
      
      for (int i=1; i < attributes.length; i++) {
        nameValue = attributes[i].trim();
        int equals = nameValue.indexOf('=');
        if (equals == -1) continue;
        String attributeName = nameValue.substring(0, equals);
        String attributeValue = nameValue.substring(equals+1); 
        if (attributeName.equalsIgnoreCase("domain")) {
          String uriDomain = uri.getHost();
          if (uriDomain.equals(attributeValue)) {
            cookie.domain = attributeValue;
          }
          else {
            if (!attributeValue.startsWith(".")) {
              attributeValue = "." + attributeValue;
            }
            uriDomain = uriDomain.substring(uriDomain.indexOf('.'));
            if (!uriDomain.equals(attributeValue)) {
              throw new CookieException(
               "Server tried to set cookie in another domain");
            }
            cookie.domain = attributeValue;
          }
        }
        else if (attributeName.equalsIgnoreCase("path")) {
          cookie.path = attributeValue;
        }
        else if (attributeName.equalsIgnoreCase("expires")) {
          cookie.expires = expiresFormat.parse(attributeValue);
        }
        else if (attributeName.equalsIgnoreCase("Version")) {
          if (!"1".equals(attributeValue)) {
            throw new CookieException("Unexpected version " + attributeValue);
          }
          cookie.version = attributeValue;
        }
      }
      
      return cookie;
    }
    catch (Exception ex) { 
      // ParseException, StringIndexOutOfBoundsException etc.
      throw new CookieException(ex);
    }
    
  }
  
  public boolean isExpired() {
    if (expires == null) return false;
    Date now = new Date();
    return now.after(expires);
  }

  public String getName() {
    return name;
  }

  public boolean isSecure() {
    return secure;
  }

  public URI getURI() {
    return uri;
  }
   
  public String getVersion() {
    return version;
  }
  
  // should this cookie be sent when retrieving the specified URI?
  public boolean matches(URI u) {
    
    if (isExpired()) return false;
    
    String path = u.getPath();
    if (path == null) path = "/";
    
    if (path.startsWith(this.path)) return true;
    
    return false;
  }
  
  public String toExternalForm() {
    StringBuffer result = new StringBuffer(name);
    result.append("=");
    result.append(value);
    if ("1".equals(version)) {
       result.append(" Version=1");   
    }
    return result.toString();
  }

}
