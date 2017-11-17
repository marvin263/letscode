package examples.chapter08;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class CookieStore extends CookieHandler {
    
  private List<Cookie> store = new ArrayList<Cookie>();

  public Map<String,List<String>> get(URI uri, 
   Map<String,List<String>> requestHeaders) 
   throws IOException {
      
      Map<String,List<String>> result = new HashMap<String,List<String>>();
      StringBuffer cookies = new StringBuffer();
      for (Cookie cookie : store) {
        if (cookie.isExpired()) {
          store.remove(cookie);
        }
        else if (cookie.matches(uri)) {
              if (cookies.length() != 0) cookies.append(", ");
              cookies.append(cookie.toExternalForm());
        }
      }
      
      if (cookies.length() > 0) {
        List<String> temp = new ArrayList<String>(1);
        temp.add(cookies.toString());
        result.put("Cookie", temp);
      }
      
      return result;
      
  }

  public void put(URI uri, Map<String,List<String>> responseHeaders) 
   throws IOException {
      
    List<String> setCookies = responseHeaders.get("Set-Cookie");
    for (String next : setCookies) {
      try {
        Cookie cookie = Cookie.bake(next, uri);
        // Is a cookie with this name and URI already in the list? 
        // If so, we replace it
        for (Cookie existingCookie : store) {
          if (cookie.getURI().equals(existingCookie.getURI()) &&
            cookie.getName().equals(existingCookie.getName())) {
              store.remove(existingCookie);
              break;
          }
        }
        store.add(cookie);            
      }
      catch (CookieException ex) {
        // Server sent malformed header; 
        // log and ignore
        System.err.println(ex);
      }
    }

  }

}
