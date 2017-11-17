package examples.chapter15

public abstract class CacheResponse{

  public abstract InputStream getBody();
  public abstract Map<String,List<String>> getHeaders();

}
