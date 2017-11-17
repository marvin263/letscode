package examples.chapter15;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class MemoryCache extends ResponseCache {
  
  private Map<URI, SimpleCacheResponse> responses 
   = new ConcurrentHashMap<URI, SimpleCacheResponse>();
  private int maxEntries = 100;  
  
  public MemoryCache() {
    this(100);
  }
  
  public MemoryCache(int maxEntries) {
    this.maxEntries = maxEntries;
  }
  
  public CacheRequest put(URI uri, URLConnection uc)
   throws IOException {
     
     if (responses.size() >= maxEntries) return null;
     
     String cacheControl = uc.getHeaderField("Cache-Control");
     if (cacheControl != null && cacheControl.indexOf("no-cache") >= 0) {
       return null;
     }
   
     SimpleCacheRequest request = new SimpleCacheRequest();
     SimpleCacheResponse response = new SimpleCacheResponse(request, uc);
   
     responses.put(uri, response);
     return request;
    
  }

  public CacheResponse get(URI uri, String requestMethod, 
   Map<String,List<String>> requestHeaders)
   throws IOException {
     
     SimpleCacheResponse response = responses.get(uri);
     if (response != null && response.isExpired()) { // check expiration date
       responses.remove(response);
       response = null;
     }
     return response;
     
  }
  
}
