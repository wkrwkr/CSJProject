import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;

public class GUI extends JFrame implements ActionListener{

	private static final String url = "http://localhost:4567"; //test url
	
	
	private JPanel contentPane;
	JButton btnLoad, btnSend;
    private JFileChooser fChooser;
    private JTextArea textarea;
    //private JTextArea textArea;
    private JScrollPane scrollbar;
    private HttpClient httpclnt; 

 
    public GUI() {
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	setBounds(100, 100, 450, 300);
    	contentPane = new JPanel();
    	contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    	contentPane.setLayout(new BorderLayout(0, 0));
    	setContentPane(contentPane);
    	contentPane.setLayout(null);
      
    	fChooser = new JFileChooser();
      
    	btnLoad = new JButton("Load");
      
    	btnLoad.setBounds(105, 228, 97, 23);
    	contentPane.add(btnLoad);
    	btnLoad.addActionListener(this);
      
    	btnSend = new JButton("Send");
      
    	btnSend.addActionListener(this);
    	btnSend.setBounds(214, 228, 97, 23);
    	contentPane.add(btnSend);
      
    	textarea = new JTextArea();
    	scrollbar = new JScrollPane(textarea);
    	scrollbar.setBounds(12, 10, 410, 206);
    	contentPane.add(scrollbar);  
    }
   
    @Override
    public void actionPerformed(ActionEvent arg0) {
            // TODO Auto-generated method stub
            if(arg0.getSource() == btnLoad){
            	httpclnt = HttpClients.createDefault();
                    		
                HttpPost post = new HttpPost(url+"/list");
                post.addHeader("idx", "all");
                	
                try {
                	HttpResponse httpres = httpclnt.execute(post);
                	System.out.println(EntityUtils.toString(httpres.getEntity(), "UTF-8"));
                    } catch (ClientProtocolException e) {
                    	// TODO Auto-generated catch block
                    	e.printStackTrace();
                    } catch (IOException e) {
                    	// TODO Auto-generated catch block
                    	e.printStackTrace();
                }
            }
            else if(arg0.getSource() == btnSend) {
               //list.get(0).sendMessage();
            }
    }
   
   	private void println(String output)
   	{
   		textarea.append(output+"\n");
   		textarea.setCaretPosition(textarea.getDocument().getLength());
   	}
}
