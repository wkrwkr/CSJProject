import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;

public class GUI extends JFrame implements ActionListener{

   private JPanel contentPane;
   JButton btnLoad, btnSend;
   private JFileChooser fChooser;
   private ArrayList<Worker> list;
   private JTextArea textarea;
   private JScrollPane scrollbar;

   
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
                    if(fChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
                            // showopendialog 열기 창을 열고 확인 버튼을 눌렀는지 확인
                            System.out.println("열기 경로 : " + fChooser.getSelectedFile().toString());
                       
                    }
            }
            else if(arg0.getSource() == btnSend) {
               //list.get(0).sendMessage();
               Worker.get(0).sendFile(fChooser.getSelectedFile().getPath());
            }
    }
   
   private void println(String output)
   {
      textarea.append(output+"\n");
      textarea.setCaretPosition(textarea.getDocument().getLength());
   }
}
