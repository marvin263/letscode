package examples.chapter15

public abstract class CacheRequest {

  public abstract InputStream getBody() ;
  public abstract Map<String,List<String>> getHeaders();

}
