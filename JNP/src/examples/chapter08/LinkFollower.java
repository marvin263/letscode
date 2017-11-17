package examples.chapter08;
import javax.swing.*;
import javax.swing.event.*;

public class LinkFollower implements HyperlinkListener {

  private JEditorPane pane;
  
  public LinkFollower(JEditorPane pane) {
    this.pane = pane;
  }

  public void hyperlinkUpdate(HyperlinkEvent evt) {
    
    if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
      try {
        pane.setPage(evt.getURL());        
      }
      catch (Exception ex) {
        pane.setText("<html>Could not load " + evt.getURL() + "</html>");
      } 
    }
    
  }

}
