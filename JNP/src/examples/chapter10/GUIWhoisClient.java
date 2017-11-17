package examples.chapter10;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

public class GUIWhoisClient extends JFrame {

  public final static int port = 43;
  public final static String hostname = "whois.internic.net";

  private JTextField searchString = new JTextField(30);
  private JTextArea detailView = new JTextArea(12, 40);
  private DefaultListModel namesModel = new DefaultListModel();
  private JList names = new JList(namesModel);
  private JButton findButton = new JButton("Find");;
  private ButtonGroup searchIn = new ButtonGroup();
  private ButtonGroup searchFor = new ButtonGroup();
  private JCheckBox exactMatch = new JCheckBox("Exact Match", true);

  public GUIWhoisClient() {
  
    super("whois");
    Container pane = this.getContentPane();
    detailView.setEditable(false);
    JScrollPane jsp1 = new JScrollPane(detailView);  

    Panel CenterPanel = new Panel();
    CenterPanel.setLayout(new GridLayout(1, 2, 10, 10));
    JScrollPane jsp = new JScrollPane(names);
    CenterPanel.add(jsp);
    CenterPanel.add(jsp1);
    pane.add("Center", CenterPanel);   
    
    // You don't want the buttons in the south and north
    // to fill the entire sections so add Panels there
    // and use FlowLayouts in the Panel
    Panel NorthPanel = new Panel();
    Panel NorthPanelTop = new Panel();
    NorthPanelTop.setLayout(new FlowLayout(FlowLayout.LEFT));
    NorthPanelTop.add(new Label("Whois: "));
    NorthPanelTop.add("North", searchString);
    NorthPanelTop.add(exactMatch);
    NorthPanel.setLayout(new BorderLayout(2,1));
    NorthPanel.add("North", NorthPanelTop);
    Panel NorthPanelBottom = new Panel();
    NorthPanelBottom.setLayout(new GridLayout(1,2,5,5));
    NorthPanelBottom.add(initRecordType());
    NorthPanelBottom.add(initSearchFields());
    NorthPanel.add("Center", NorthPanelBottom);
    Panel SouthPanel = new Panel();

    SouthPanel.add("South", findButton);
    pane.add("South", SouthPanel);
    pane.add("North", NorthPanel);
      
    ActionListener al = new LookupNames();  
    findButton.addActionListener(al);
    searchString.addActionListener(al);
    
    names.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        int index = names.locationToIndex(e.getPoint());
        getFullRecord(namesModel.elementAt(index).toString());      
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

  
  class LookupNames implements ActionListener {

    public void actionPerformed(ActionEvent e) {
      lookUpNames(searchString.getText());
    }
    
  }
  
  public String makeSuffix() {
  
    String suffix = "";
    if (!exactMatch.isSelected()) suffix = ".";
    return suffix;
  
  }
  
  public String makePrefix() {
  
    String searchForLabel = searchFor.getSelection().getActionCommand();
    String searchInLabel = searchIn.getSelection().getActionCommand();
    if (searchInLabel.equals("All")) searchInLabel = ""; 
    else searchInLabel += " ";
    if (searchForLabel.equals("Any")) searchForLabel = "";  
    else searchForLabel += " ";
    String prefix = searchForLabel + searchInLabel + "$";
    return prefix;
  
  }

  public void lookUpNames(String name) {
      
    try {
      Socket theSocket = new Socket(hostname, port);
      Writer out = new OutputStreamWriter(theSocket.getOutputStream(), "ASCII");
      BufferedReader in = new BufferedReader(new 
       InputStreamReader(theSocket.getInputStream(), "ASCII"));
      String query = this.makePrefix() + searchString.getText() + this.makeSuffix() + "\r\n";
      out.write(query);
      out.flush();
      namesModel.clear();
      String s;
      while ((s = in.readLine()) != null) {
        // throw away NSI legal gibberish
        if (s.indexOf('\t') >= 0) {
          // tab stops seem to be every 8 spaces
          s = s.replace('\t', ' ');
          namesModel.addElement(s);
        }
      }
    }  
    catch (IOException e) {
      System.err.println(e);
    }

  }

  public void getFullRecord (String summary) {
  
    String handle = getHandle(summary);
    try {
      Socket theSocket = new Socket(hostname, port);
      Writer out = new OutputStreamWriter(theSocket.getOutputStream(), "ASCII");
      Reader in = new InputStreamReader(theSocket.getInputStream(), "ASCII");
      out.write("!" + handle + "\r\n");
      out.flush();
      detailView.setText("");
      int c;
      while ((c = in.read()) != -1) detailView.append(String.valueOf((char) c));
    }  
    catch (IOException e) {
      System.err.println(e);
    }
  
  }

  private static String getHandle(String s) {
  
    int begin = s.indexOf("(") + 1;
    int end = s.indexOf(")", begin);
    return s.substring(begin,end);
  
  }

  public static void main(String[] args) {
  
    GUIWhoisClient a = new GUIWhoisClient();
    a.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });
    a.pack();
    a.show();

  }

}
