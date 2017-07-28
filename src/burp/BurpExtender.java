package burp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;



public class BurpExtender implements IBurpExtender, IHttpListener, ITab, IContextMenuFactory
{
    public IBurpExtenderCallbacks callbacks;
    private IExtensionHelpers helpers;
    
    private PrintWriter stdout;//�������ﶨ�����������registerExtenderCallbacks������ʵ������������ں����о�ֻ�Ǿֲ���������������ʵ��������ΪҪ�õ�����������
    private String ExtenderName = "Domain Hunter v0.3 by bit4";
    private String github = "https://github.com/bit4woo/domain_hunter";
    private Set subdomainofset = new HashSet();
    private Set domainlikeset = new HashSet();
    private Set subdomainofset_spider_all = new HashSet();
    private Set domainlikeset_spider_all = new HashSet();
    private Set url_spidered_set = new HashSet();
	private JPanel contentPane;
	private JTextField textFieldSubdomains;
	private JTextField textFieldDomainsLike;
	private JLabel lblSubDomainsOf;
	private JButton btnSearch;
	private JPanel panel_2;
	private JLabel lblNewLabel_2;
	private JSplitPane splitPane;
	private Component verticalStrut;
	private JTextArea textArea;
	private JTextArea textArea_1;
	private JButton btnNewButton;
    
    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks)
    {
    	stdout = new PrintWriter(callbacks.getStdout(), true);
    	stdout.println(ExtenderName);
    	stdout.println(github);
        this.callbacks = callbacks;
        helpers = callbacks.getHelpers();
        callbacks.setExtensionName(ExtenderName); //�������
        callbacks.registerHttpListener(this); //���û��ע�ᣬ�����processHttpMessage�����ǲ�����Ч�ġ������������Ӧ���Ĳ�������Ӧ���Ǳ�Ҫ��
        callbacks.registerContextMenuFactory(this);
        addMenuTab();
        
    }

	@Override
	public void processHttpMessage(int toolFlag, boolean messageIsRequest, IHttpRequestResponse messageInfo) {
		// TODO Auto-generated method stub
		
	}
	public Map search(String subdomainof, String domainlike){
			subdomainofset.clear();
			domainlikeset.clear();
		   IHttpRequestResponse[] response = callbacks.getSiteMap("http");
		    //stdout.println(response[1]);
		    for (IHttpRequestResponse x:response){
		    	IRequestInfo  analyzeRequest = helpers.analyzeRequest(x); //ǰ��Ĳ��������Ѿ��޸���������������������ĸ���ǰ������Ҫ���»�ȡ��
				List<String> headers = analyzeRequest.getHeaders();
				for (String header:headers){
					String key = header.split(" ")[0];
					String value = header.split(" ")[1];
					if (subdomainof.equals("")){
						
					}
					else if (key.equals("Host:")&&value.endsWith("."+subdomainof)){
						subdomainofset.add(value);
						//stdout.println(subdomainofset);
					}
					
					
					else if (domainlike.equals("")){
						
					}
					else if (key.equals("Host:")&&value.contains(domainlike)){
						domainlikeset.add(value);
						//stdout.println(domainlikeset);
					}
				}
		    }
		    
		    Map result = new HashMap();
		    result.put("subdomainofset", subdomainofset);
		    result.put("domainlikeset", domainlikeset);
		    return result;
		    
	}
 
	public Map spiderall (String subdomainof, String domainlike) {
		url_spidered_set.clear();
	    
	    int i = 0;
	    int url_number = 0;
	    int item_len = 0;
	    while(i<=2) {
	    	IHttpRequestResponse[] items = callbacks.getSiteMap("http");
	    	int len = items.length;
	    	//items.subList(item_len,len);
	    	//IHttpRequestResponse[] subitems = Arrays.copyOfRange(items, item_len, len-1);
	    	
	    	/*
	    	try {//��֤�Ƿ���Ҫ��ͷ��ʼ�������������Ҫ
	    		stdout.println("10");
		    	stdout.println(helpers.analyzeRequest(items[10]).getUrl());
		    	stdout.println("56");
		    	stdout.println(helpers.analyzeRequest(items[56]).getUrl());
		    	stdout.println("100");
		    	stdout.println(helpers.analyzeRequest(items[100]).getUrl());
	    	}
	    	catch (Exception e){
	    		
	    	}*/
	    	
		    for (IHttpRequestResponse x:items){// �µ�ѭ�����񲻱ش�ͷ��ʼ������
		    	IRequestInfo  analyzeRequest = helpers.analyzeRequest(x); //ǰ��Ĳ��������Ѿ��޸���������������������ĸ���ǰ������Ҫ���»�ȡ��
				URL url = analyzeRequest.getUrl();
				String Host = url.getHost();
				if (Host.endsWith("."+subdomainof)&& !url_spidered_set.contains(url)) {
					url_spidered_set.add(url);
					callbacks.includeInScope(url);//if not, will always show confirm message box.
					callbacks.sendToSpider(url);
				}
				try {
					Thread.sleep(5*60000);//��λ���룬60000����=һ����
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
		    
		    int size = url_spidered_set.size();
		    if(size==url_number) {
		    	break; //break while loop,when no new url to spider.
		    }
		    url_number = size;
		    }
	    return search(subdomainof,domainlike);
	    //search(subdomainof,domainlike);
	    }
	
	
	public String set2string(Set set){
	    Iterator iter = set.iterator();
	    String result = "";
	    while(iter.hasNext())
	    {
	        //System.out.println(iter.next());  	
	    	result +=iter.next();
	    	result +="\n";
	    }
	    return result;
	}
		
	public void CGUI() {
			
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			contentPane.setLayout(new BorderLayout(0, 0));
	
			
			JPanel panel = new JPanel();
			FlowLayout flowLayout_1 = (FlowLayout) panel.getLayout();
			flowLayout_1.setAlignment(FlowLayout.LEFT);
			contentPane.add(panel, BorderLayout.NORTH);
			
			lblSubDomainsOf = new JLabel("SubDomains of  ");
			panel.add(lblSubDomainsOf);
			
			textFieldSubdomains = new JTextField();
			textFieldSubdomains.addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(FocusEvent arg0) {
					String domain = textFieldSubdomains.getText();
					textFieldDomainsLike.setText(domain.substring(0,domain.lastIndexOf(".")));
				}
			});
			panel.add(textFieldSubdomains);
			textFieldSubdomains.setColumns(20);
			
			verticalStrut = Box.createVerticalStrut(20);
			panel.add(verticalStrut);
			
			JLabel lblDomainsLike = new JLabel("Domains like ");
			panel.add(lblDomainsLike);
			
			textFieldDomainsLike = new JTextField();
			panel.add(textFieldDomainsLike);
			textFieldDomainsLike.setColumns(20);
			
			btnSearch = new JButton("search");
			btnSearch.setToolTipText("Do a single search from site map");
			btnSearch.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					String subdomain = textFieldSubdomains.getText();
					String domainlike = textFieldDomainsLike.getText();
					//stdout.println(subdomain);
					//stdout.println(domainlike);
					search(subdomain,domainlike);
					textArea.setText(set2string(subdomainofset));
					textArea_1.setText(set2string(domainlikeset));
				}
			});
			panel.add(btnSearch);
			
			btnNewButton = new JButton("Spider all");
			btnNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					
				    SwingWorker<Map, Map> worker = new SwingWorker<Map, Map>() {
				    	//������һ������ʵ����һ���ֱ࣬��ʵ��ԭʼ�࣬û�б������������ţ�
				    	//֮ǰ���뷨���ȵ���ʵ��һ��worker�࣬�������洦�����֣��Ͷ���һ��ʵ�֣�Ȼ����������ã��������û���һ�������⡣
				    	//https://stackoverflow.com/questions/19708646/how-to-update-swing-ui-while-actionlistener-is-in-progress
				        @Override
				        protected Map doInBackground() throws Exception {                
							String subdomain = textFieldSubdomains.getText();
							String domainlike = textFieldDomainsLike.getText();
							//stdout.println(subdomain);
							//stdout.println(domainlike);
							btnNewButton.setEnabled(false);
							return spiderall(subdomain,domainlike);
						
				        }
				        @Override
				        protected void done() {
				            try {
					        	Map result = get();
					        	subdomainofset = (Set) result.get("subdomainofset"); //֮ǰ��set�����object
					        	domainlikeset = (Set) result.get("domainlikeset");
								textArea.setText(set2string(subdomainofset));
								textArea_1.setText(set2string(domainlikeset));
								btnNewButton.setEnabled(true);
				            } catch (Exception e) {
				                e.printStackTrace();
				            }
				        }
				    };      
				    worker.execute();
				}
			});
			btnNewButton.setToolTipText("Crawl recursively,This may take 10min!!!");
			panel.add(btnNewButton);
			
			splitPane = new JSplitPane();
			splitPane.setDividerLocation(0.5);
			contentPane.add(splitPane, BorderLayout.CENTER);
			
			textArea = new JTextArea();
			textArea.setColumns(30);
			splitPane.setLeftComponent(textArea);
			
			textArea_1 = new JTextArea();
			textArea.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) {
					final JPopupMenu jp = new JPopupMenu();
			        jp.add("^_^");
			        textArea.addMouseListener(new MouseAdapter() {
			            @Override
			            public void mouseClicked(MouseEvent e) {
			                if (e.getButton() == MouseEvent.BUTTON3) {
			                    // �����˵�
			                    jp.show(textArea, e.getX(), e.getY());
			                }
			            }
			        });
				}
			});
			textArea_1.setColumns(30);
			splitPane.setRightComponent(textArea_1);
			
			panel_2 = new JPanel();
			FlowLayout flowLayout = (FlowLayout) panel_2.getLayout();
			flowLayout.setAlignment(FlowLayout.LEFT);
			contentPane.add(panel_2, BorderLayout.SOUTH);
			
			lblNewLabel_2 = new JLabel(ExtenderName+"    "+github);
			lblNewLabel_2.setFont(new Font("����", Font.BOLD, 12));
			lblNewLabel_2.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					try {
						URI uri = new URI(github);
						Desktop desktop = Desktop.getDesktop();
						if(Desktop.isDesktopSupported()&&desktop.isSupported(Desktop.Action.BROWSE)){
							desktop.browse(uri);
						}
					} catch (Exception e2) {
						// TODO: handle exception
						BurpExtender.this.callbacks.printError(e2.getMessage());
					}
					
				}
				@Override
				public void mouseEntered(MouseEvent e) {
					lblNewLabel_2.setForeground(Color.BLUE);
				}
				@Override
				public void mouseExited(MouseEvent e) {
					lblNewLabel_2.setForeground(Color.BLACK);
				}
			});
			panel_2.add(lblNewLabel_2);
	}
		
//�����Ǹ���burp����ķ��� --start
    
    public void addMenuTab()
    {
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          BurpExtender.this.CGUI();
          BurpExtender.this.callbacks.addSuiteTab(BurpExtender.this); //�����BurpExtender.thisʵ����ָITab����Ҳ����getUiComponent()�е�contentPane.���������CGUI()������ʼ����
          //������ﱨjava.lang.NullPointerException: Component cannot be null ������Ҫ�Ų�contentPane�ĳ�ʼ���Ƿ���ȷ��
        }
      });
    }
    
    
    
    //ITab����ʵ�ֵ���������
	@Override
	public String getTabCaption() {
		// TODO Auto-generated method stub
		return ("Domain Hunter");
	}
	@Override
	public Component getUiComponent() {
		// TODO Auto-generated method stub
		return this.contentPane;
	}
	//ITab����ʵ�ֵ���������

	@Override
	public List<JMenuItem> createMenuItems(IContextMenuInvocation invocation) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	//IContextMenuFactory ����ʵ�ֵķ���
	//����burp����ķ��� --end
}