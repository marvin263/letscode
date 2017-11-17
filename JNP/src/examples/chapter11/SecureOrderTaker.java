package examples.chapter11;
import java.net.*;
import java.io.*;
import java.util.*;
import java.security.*;
import javax.net.ssl.*;
import javax.net.*;
 
public class SecureOrderTaker {
 
  public final static int DEFAULT_PORT = 7000;
  public final static String algorithm = "SSL";

  public static void main(String[] args) {

    int port = DEFAULT_PORT;     
    if (args.length > 0) {
      try {
        port = Integer.parseInt(args[0]);
        if (port < 0 || port >= 65536) {
          System.out.println("Port must between 0 and 65535");
          return;      
        }
      }   
      catch (NumberFormatException ex) {}  
    }     

    try {
      
      SSLContext context = SSLContext.getInstance(algorithm);
      
      // The reference implementation only supports X.509 keys
      KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
      
      // Sun's default kind of key store
      KeyStore ks = KeyStore.getInstance("JKS");

      // For security, every key store is encrypted with a 
      // pass phrase that must be provided before we can load 
      // it from disk. The pass phrase is stored as a char[] array
      // so it can be wiped from memory quickly rather than
      // waiting for a garbage collector. Of course using a string
      // literal here completely defeats that purpose.
      char[] password = "2andnotafnord".toCharArray();
      ks.load(new FileInputStream("jnp3e.keys"), password);
      kmf.init(ks, password);
      
      //  
      context.init(kmf.getKeyManagers(), null, null);

      SSLServerSocketFactory factory 
       = context.getServerSocketFactory();
     
      SSLServerSocket server 
       = (SSLServerSocket) factory.createServerSocket(port);
     
      String[] supported = server.getSupportedCipherSuites();
      String[] anonCipherSuitesSupported = new String[supported.length];      
      int numAnonCipherSuitesSupported = 0;
      for (int i = 0; i < supported.length; i++) {
        if (supported[i].indexOf("_anon_") > 0) {
          anonCipherSuitesSupported[numAnonCipherSuitesSupported++] = supported[i];
        }
      }  
      
      String[] oldEnabled = server.getEnabledCipherSuites();
      String[] newEnabled = new String[oldEnabled.length 
       + numAnonCipherSuitesSupported];
      System.arraycopy(oldEnabled, 0, newEnabled, 0, oldEnabled.length);
      System.arraycopy(anonCipherSuitesSupported, 0, newEnabled, 
       oldEnabled.length, numAnonCipherSuitesSupported);
      
      server.setEnabledCipherSuites(newEnabled);     
      // Now all the set up is complete and we can focus 
      // on the actual communication. 
      try {
        while (true) {
          // This socket will be secure,
          // but there's no indication of that in the code!
          Socket theConnection = server.accept();
          InputStream in = theConnection.getInputStream();
          int c;
          while ((c = in.read()) != -1) {
            System.out.write(c);
          } 
          theConnection.close();
        }  // end while
      } // end try
      catch (IOException ex) {
        System.err.println(ex);
      } // end catch
      
   }  // end try
   catch (IOException ex) {
     ex.printStackTrace();
   } // end catch
   catch (KeyManagementException ex) {
     ex.printStackTrace();
   } // end catch
   catch (KeyStoreException ex) {
     ex.printStackTrace();
   } // end catch
   catch (NoSuchAlgorithmException ex) {
     ex.printStackTrace();
   } // end catch
   catch (java.security.cert.CertificateException ex) {
     ex.printStackTrace();
   } // end catch
   catch (UnrecoverableKeyException ex) {
     ex.printStackTrace();
   } // end catch

  } // end main

} // end server
