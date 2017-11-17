package examples.chapter10;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;


public class WhoisGUI extends JFrame {

  private JTextField searchString = new JTextField(30);
  private JTextArea names = new JTextArea(15, 80);
  private JButton findButton = new JButton("Find");;
  private ButtonGroup searchIn = new ButtonGroup();
  private ButtonGroup searchFor = new ButtonGroup();
  private JCheckBox exactMatch = new JCheckBox("Exact Match", true);
  private JTextField chosenServer = new JTextField();
  private Whois server;
  
  public WhoisGUI(Whois whois) {
  
    super("Whois");
    this.server = whois;    
    Container pane = this.getContentPane();
    
    // whois.internic.net assumes a monospaced font, 72 columns across
    Font f = new Font("Monospaced", Font.PLAIN, 12);
    names.setFont(f);
    names.setEditable(false);
    
    JPanel centerPanel = new JPanel();
    centerPanel.setLayout(new GridLayout(1, 1, 10, 10));
    JScrollPane jsp = new JScrollPane(names);
    centerPanel.add(jsp);
    pane.add("Center", centerPanel);   
    
    // You don't want the buttons in the south and north
    // to fill the entire sections so add Panels there
    // and use FlowLayouts in the Panel
    JPanel northPanel = new JPanel();
    JPanel northPanelTop = new JPanel();
    northPanelTop.setLayout(new FlowLayout(FlowLayout.LEFT));
    northPanelTop.add(new JLabel("Whois: "));
    northPanelTop.add("North", searchString);
    northPanelTop.add(exactMatch);
    northPanelTop.add(findButton);
    northPanel.setLayout(new BorderLayout(2,1));
    northPanel.add("North", northPanelTop);
    JPanel northPanelBottom = new JPanel();
    northPanelBottom.setLayout(new GridLayout(1,3,5,5));
    northPanelBottom.add(initRecordType());
    northPanelBottom.add(initSearchFields());
    northPanelBottom.add(initServerChoice());
    northPanel.add("Center", northPanelBottom);

    pane.add("North", northPanel);
      
    ActionListener al = new LookupNames();  
    findButton.addActionListener(al);
    searchString.addActionListener(al);
        
  }
  
  private JPanel initRecordType() {
  
    JPanel p = new JPanel();
    p.setLayout(new GridLayout(6, 2, 5, 2));
    p.add(new JLabel("Search for:"));
    p.add(new JLabel(""));
    
    JRadioButton any = new JRadioButton("Any", true);
    any.setActionCommand("Any");
    searchFor.add(any);
    p.add(any);

    p.add(this.makeRadioButton("Network"));
    p.add(this.makeRadioButton("Person"));
    p.add(this.makeRadioButton("Host"));
    p.add(this.makeRadioButton("Domain"));
    p.add(this.makeRadioButton("Organization"));
    p.add(this.makeRadioButton("Group"));
    p.add(this.makeRadioButton("Gateway"));
    p.add(this.makeRadioButton("ASN"));

    return p;
  
  }

  private JRadioButton makeRadioButton(String label) {
    
    JRadioButton button = new JRadioButton(label, false);
    button.setActionCommand(label);
    searchFor.add(button);
    return button;
    
  }

  private JRadioButton makeSearchInRadioButton(String label) {
    
    JRadioButton button = new JRadioButton(label, false);
    button.setActionCommand(label);
    searchIn.add(button);
    return button;
    
  }

  private JPanel initSearchFields() {
  
    JPanel p = new JPanel();
    p.setLayout(new GridLayout(6, 1, 5, 2));
    p.add(new JLabel("Search In: "));

    JRadioButton all = new JRadioButton("All", true);
    all.setActionCommand("All");
    searchIn.add(all);
    p.add(all);

    p.add(this.makeSearchInRadioButton("Name"));
    p.add(this.makeSearchInRadioButton("Mailbox"));
    p.add(this.makeSearchInRadioButton("Handle"));

    return p;
  
  }
  
  private JPanel initServerChoice() {
  
    JPanel p = new JPanel();
    p.setLayout(new GridLayout(6, 1, 5, 2));
    p.add(new JLabel("Search At: "));

    chosenServer.setText(server.getHost().getHostName());
    p.add(chosenServer);
    chosenServer.addActionListener( new ActionListener() {
      public void actionPerformed(ActionEvent evt) { 
        try {
          InetAddress newHost 
           = InetAddress.getByName(chosenServer.getText());
          Whois newServer = new Whois(newHost);
          server = newServer;  
        }
        catch (Exception e) {
          // should use an error dialog here, but that
          // doesn't teach much about networking
          chosenServer.setText(server.getHost().getHostName());
        }
      }
    } );
    
    return p;
  
  }

  class LookupNames implements ActionListener {

    public void actionPerformed(ActionEvent evt) {
        
      Whois.SearchIn group = Whois.SearchIn.ALL;
      Whois.SearchFor category = Whois.SearchFor.ANY;
      
      String searchForLabel = searchFor.getSelection().getActionCommand();
      String searchInLabel = searchIn.getSelection().getActionCommand();
      if (searchInLabel.equals("Name")) group = Whois.SearchIn.NAME; 
      else if (searchInLabel.equals("Mailbox")) {
        group = Whois.SearchIn.MAILBOX;
      }
      else if (searchInLabel.equals("Handle")) {
        group = Whois.SearchIn.HANDLE;
      }

      if (searchForLabel.equals("Network")) {
        category = Whois.SearchFor.NETWORK;
      }
      else if (searchForLabel.equals("Person")) {
        category = Whois.SearchFor.PERSON;
      }
      else if (searchForLabel.equals("Host")) {
        category = Whois.SearchFor.HOST;
      }
      else if (searchForLabel.equals("Domain")) {
        category = Whois.SearchFor.DOMAIN;
      }
      else if (searchForLabel.equals("Organization")) {
        category = Whois.SearchFor.ORGANIZATION;
      }
      else if (searchForLabel.equals("Group")) {
        category = Whois.SearchFor.GROUP;
      }
      else if (searchForLabel.equals("Gateway")) {
        category = Whois.SearchFor.GATEWAY;
      }
      else if (searchForLabel.equals("ASN")) {
        category = Whois.SearchFor.ASN;
      }

      try {
        names.setText("");
        String result = server.lookUpNames(searchString.getText(), 
         category, group, exactMatch.isSelected());
        names.setText(result);
      }
      catch (IOException e) { 
        names.setText("Lookup failed due to " + e);
      }
    }
    
  }

  public static void main(String[] args) {
  
    try {
      Whois server = new Whois();
      WhoisGUI a = new WhoisGUI(server);
      a.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          System.exit(0);
        }
      });
      a.pack();
      a.show();     
    }
    catch (UnknownHostException e) {
      System.err.println("Error: Could not locate default host "
       + Whois.DEFAULT_HOST);
      System.err.println("Check to make sure you're connected to the"
       + " Internet and that DNS is funtioning");
      System.err.println("Usage: java WhoisGUI");         
      return;        
    }

  }

}