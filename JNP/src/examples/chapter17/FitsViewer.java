package examples.chapter17;
import java.awt.*;
import javax.swing.*;
import java.awt.image.*;
import java.net.*;
import java.io.*;

public class FitsViewer extends JFrame {

  private URL url;
  private Image theImage;

  public FitsViewer(URL u) {  
    super(u.getFile());
    this.url = u;
  }
  
  public void loadImage() throws IOException {
     
    Object content = this.url.getContent();
    ImageProducer producer;
    try {
      producer = (ImageProducer) content;
    }
    catch (ClassCastException e) {
      throw new IOException("Unexpected type " + content.getClass());        
    }
    if (producer == null) theImage = null;
    else {
      theImage = this.createImage(producer);
      int width = theImage.getWidth(this);
      int height = theImage.getHeight(this);
      if (width > 0 && height > 0) this.setSize(width, height);
    }
    
  }
  
  public void paint(Graphics g) {  
    if (theImage != null) g.drawImage(theImage, 0, 0, this);
  }

  public static void main(String[] args) {
  
    URLConnection.setContentHandlerFactory(new FitsFactory());
    for (int i = 0; i < args.length; i++) {
      try {
        FitsViewer f = new FitsViewer(new URL(args[i]));
        f.setSize(252, 252);
        f.loadImage();
        f.show();
      }
      catch (MalformedURLException ex) {
        System.err.println(args[i] + " is not a URL I recognize."); 
      }
      catch (IOException ex) {
        ex.printStackTrace(); 
      }
    }
  }
}
