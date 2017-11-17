package examples.chapter08;

public abstract class CookieHandler {

  public CookieHandler()

  public abstract Map<String,List<String>> get(
   URI uri, Map<String,List<String>> requestHeaders) 
   throws IOException
  public abstract void put(
   URI uri, Map<String,List<String>> responseHeaders) 
   throws IOException

  public static CookieHandler getDefault()
  public static void           setDefault(CookieHandler handler)

}
