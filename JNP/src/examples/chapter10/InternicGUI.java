package examples.chapter10;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class InternicGUI extends JFrame {

  private JTextField searchString = new JTextField(30);
  private JTextArea detailView = new JTextArea(10, 72);
  private DefaultListModel namesModel = new DefaultListModel();
  private JList names = new JList(namesModel);
  private JButton findButton = new JButton("Find");;
  private ButtonGroup searchIn = new ButtonGroup();
  private ButtonGroup searchFor = new ButtonGroup();
  private JCheckBox exactMatch = new JCheckBox("Exact Match", true);
  private Whois server;
  
  public InternicGUI(Whois whois) {
  
    super("Internic Whois");
    this.server = whois;    
    Container pane = this.getContentPane();
    detailView.setEditable(false);
    
    // the whois server assumes a monospaced font, 72 columns across
    Font f = new Font("Monospaced", Font.PLAIN, 10);
    detailView.setFont(f);
    names.setFont(f);
    
    JScrollPane jsp1 = new JScrollPane(detailView);  

    JPanel CenterPanel = new JPanel();
    CenterPanel.setLayout(new GridLayout(2, 1, 10, 10));
    names.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    JScrollPane jsp = new JScrollPane(names);
    CenterPanel.add(jsp);
    CenterPanel.add(jsp1);
    pane.add("Center", CenterPanel);   
    
    // You don't want the buttons in the south and north
    // to fill the entire sections so add Panels there
    // and use FlowLayouts in the Panel
    JPanel NorthPanel = new JPanel();
    JPanel NorthPanelTop = new JPanel();
    NorthPanelTop.setLayout(new FlowLayout(FlowLayout.LEFT));
    NorthPanelTop.add(new JLabel("Whois: "));
    NorthPanelTop.add("North", searchString);
    NorthPanelTop.add(exactMatch);
    NorthPanelTop.add(findButton);
    NorthPanel.setLayout(new BorderLayout(2,1));
    NorthPanel.add("North", NorthPanelTop);
    JPanel NorthPanelBottom = new JPanel();
    NorthPanelBottom.setLayout(new GridLayout(1,2,5,5));
    NorthPanelBottom.add(initRecordType());
    NorthPanelBottom.add(initSearchFields());
    NorthPanel.add("Center", NorthPanelBottom);
    JPanel SouthPanel = new JPanel();

    pane.add("North", NorthPanel);
      
    ActionListener al = new LookupNames();  
    findButton.addActionListener(al);
    searchString.addActionListener(al);
    
    names.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent evt) {
        int index = names.locationToIndex(evt.getPoint());
        try {
          String details = server.getFullRecord(
           getHandle(namesModel.elementAt(index).toString()));
          detailView.setText(details); 
        }
        catch (Exception ex) {}     
      } });
    
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

  private static String getHandle(String s) {
  
    int begin = s.indexOf("(") + 1;
    int end = s.indexOf(")", begin);
    return s.substring(begin,end);
  
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
        java.util.List result = server.lookUpNames(searchString.getText(), 
         category, group, exactMatch.isSelected());
        Iterator records = result.iterator();
        namesModel.clear();
        while (records.hasNext()) {
          String s = (String) records.next();
          if (s.indexOf('\t') >= 0) { // throw away NSI legal gibberish
            // tab stops are every 8 spaces
            StringBuffer temp = new StringBuffer();
            int numChars = 0;
            for (int i = 0; i < s.length(); i++) {
              if (s.charAt(i) == '\t') {
                int numSpaces = 8 - numChars%8;
                for (int j = 0; j < numSpaces; j++) {
                  temp.append(' ');
                }
                numChars += numSpaces;
              }   
              else {
                temp.append(s.charAt(i));
                numChars++;
              }    
            }
            namesModel.addElement(temp.toString());
          }   
        }
      }
      catch (IOException e) { 
      }
    }
    
  }

  public static void main(String[] args) {
  
    try {
      Whois server = new Whois();
      InternicGUI a = new InternicGUI(server);
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
      System.err.println("Usage: java InternicGUI");         
      return;        
    }

  }

}
