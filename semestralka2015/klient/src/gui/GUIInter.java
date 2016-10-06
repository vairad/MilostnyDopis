package gui;

import net.MessageFactory;
import net.NetService;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.*;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class GUIInter {

	public static Logger logger; // inicializace probíhá v konfiguraci logovani

 private JFrame okno = new JFrame();
 
 private JMenuBar menu = new JMenuBar();
 private JMenu menuEdit, menuFile, menuHelp;
 private JMenuItem save,copy,paste,open,novy,exit,about;
 private JButton button1, button2, button3;
 private JLabel content;
 
 
 private Locale jazyk = new Locale("cs","CZ") ;
 
 private ResourceBundle textyAplikace = ResourceBundle.getBundle("Texts",jazyk);

	/** sťové komunikační rozhraní*/
 private NetService netService;
//############################################################################

 
 /**
  * Konstruktor vytvoří
  *  
  */
 public GUIInter( NetService netService){

	 this.netService = netService;

	 okno.setLocationRelativeTo(null); // okno na stred

	 okno.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // krizek nic nedela
	 okno.addWindowListener(new WindowAdapter() {
		 @Override
		 public void windowClosing(WindowEvent e) {

			 // dotaz, zda chci opravdu ukončit aplikaci
			 if (JOptionPane.showConfirmDialog(okno,
					 textyAplikace.getString("askQuit"), textyAplikace.getString("titleQuit"),
					 JOptionPane.YES_NO_OPTION,
					 JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
				 System.exit(0);
			 }
		 }
	 });

     okno.setTitle(textyAplikace.getString("windowTitle")); // nastavi titulek okna

	 okno.setPreferredSize(new Dimension(300, 200)); // nastaví výchozí rozmer
	 okno.setMinimumSize(new Dimension(300, 200)); //nastavi minimalni
	 
	 generujMenu(); //pripravi vodorovne menu
	 
	 generujObsah(); // pripravi obsah
	 
	 okno.pack(); // idealizuj rozmery okna
	 okno.setVisible(true); // zobraz
 }

	/**
	 *
	 */
public void closeApp(){
	 System.exit(0);
 }

 /**
  * Vygeneruje obsah okna prida ho do okna
  */
 private void generujObsah(){
	 
	 JPanel panel = new JPanel(new FlowLayout());
	 
	 	button1 = new JButton(new AkceSend());
	 	button1.setPreferredSize(new Dimension(200, 20));
	 panel.add(button1);

	 button2 = new JButton(new AkceSendTemplate());
	 button2.setPreferredSize(new Dimension(200, 20));
	 	panel.add(button2);

	 button3 = new JButton(new AkceSendServer());
	 button3.setPreferredSize(new Dimension(200, 20));
	 panel.add(button3);

	 	content = new JLabel(textyAplikace.getString("content"));
	 panel.add(content);
	 
	 okno.add(panel);
	 
	 
 }
 
 /**
  * Vygeneruje vodorovne menu a pridáho do okna.
  */
 private void generujMenu(){
	 
	 menu.add(menuSoubor());
	 menu.add(menuUpravy());
	 menu.add(menuNapoveda());

	 okno.setJMenuBar(menu);
 }
  
 
 //##############################################################################################
 // Tvorba menu
 //############
 
 /**
  * Vygeneruje svisle menu souboru
  * @return JMenu Soubor
  */
 private JMenu menuSoubor(){
	 menuFile = new JMenu(textyAplikace.getString("menuFile"));
	 	
	    save = new JMenuItem(textyAplikace.getString("save"));
		open = new JMenuItem(textyAplikace.getString("open"));
		novy = new JMenuItem(textyAplikace.getString("new"));
		
	 menuFile.add(save);
	 menuFile.add(open);
	 menuFile.add(novy);
	
	 menuFile.addSeparator();
	  
	 
	    exit = new JMenuItem(new AkceVypnout());
	 menuFile.add(exit);
	 
	 return menuFile;
 }

 
 /**
  * Vygeneruje svisl� menu �prav
  * @return JMenu �pravy
  */
 private JMenu menuUpravy(){
	 menuEdit = new JMenu(textyAplikace.getString("menuEdit"));
	 
	        copy = new JMenuItem(textyAplikace.getString("copy"));
			paste = new JMenuItem(textyAplikace.getString("paste"));
			
	 menuEdit.add(copy);
	 menuEdit.add(paste);
	 
	 return menuEdit;
 }
 
 
 /**
  * Vygeneruje svisl� menu n�pov�dy
  * @return JMenu N�pov�da
  */
 private JMenu menuNapoveda(){
	 menuHelp = new JMenu(textyAplikace.getString("menuHelp"));

	 	about = new JMenuItem(new AkceNapoveda());
	 menuHelp.add(about);
	 
	 return menuHelp;
 }
 
 
 //########################################################################## 
 //##########################################################################
 
 /**
  * Metoda p�epne jazyk
  * Pokud nen� nastaven na �e�tinu nastav� ji, jinak nastav� angli�tinu
  */
 private void posliZpravu(String msg){
 	 netService.addMsg(msg);
 }


 /**
  * Metoda nastaví všechny texty v aplikace dle stávajíího jazyka
  */
 private void nastavTexty(){
	textyAplikace = ResourceBundle.getBundle("Texts",jazyk);
	
	 okno.setTitle(textyAplikace.getString("windowTitle"));
	 
	 menuEdit.setText(textyAplikace.getString("menuEdit"));
	 menuFile.setText(textyAplikace.getString("menuFile"));
	 menuHelp.setText(textyAplikace.getString("menuHelp"));
	 
	 save.setText(textyAplikace.getString("save"));
	 copy.setText(textyAplikace.getString("copy"));
	 paste.setText(textyAplikace.getString("paste"));
	 open.setText(textyAplikace.getString("open"));
	 novy.setText(textyAplikace.getString("new"));
	 exit.setText(textyAplikace.getString("exit"));
	 about.setText(textyAplikace.getString("about"));
	 
	 // ImageIcon icon = new ImageIcon(getClass().getResource("/images/"+jazyk.getCountry()+".gif"));
	 //button1.setIcon(icon);
	 content.setText(textyAplikace.getString("content"));
	 
	 
 }
 
 
 
 //##############################################################################################
 // Tvorba akc�
 //############

 	/**
 	 * Akce zobrazen� o programu
 	 *
 	 */
	class AkceNapoveda extends AbstractAction {
		private static final long serialVersionUID = 11L;


		public AkceNapoveda() {

	            putValue(NAME, textyAplikace.getString("about"));
	            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
	        }
		
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			JOptionPane.showMessageDialog(okno,
					textyAplikace.getString("aboutText"),
					textyAplikace.getString("about"),
					JOptionPane.INFORMATION_MESSAGE);
			
		}  
	}
	
	
//########
	
	/**
	 * Akce vypnut� 
	 *
	 */
	
	class AkceVypnout extends AbstractAction {
		private static final long serialVersionUID = 11L;


		public AkceVypnout() {
	            putValue(NAME, textyAplikace.getString("exit"));
	            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
	        }
		@Override
		public void actionPerformed(ActionEvent arg0) {
		okno.dispose();
		}  
	}
	
	
//########


	/***
	 * Odeslání zprávy
	 *
	 */
	class AkceSend extends AbstractAction {
		private static final long serialVersionUID = 11L;


		public AkceSend() {
	          // ImageIcon icon = new ImageIcon(getClass().getResource("/images/"+jazyk.getCountry()+".gif"));
	           // putValue(SMALL_ICON, icon);
	            putValue(NAME, textyAplikace.getString("sendMessage"));
				putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
	        }
		
		
		@Override
		public void actionPerformed(ActionEvent arg0){
			String msg = JOptionPane.showInputDialog("Zadej zprávu");
			posliZpravu(msg);
		}  
	}

	//########


	/***
	 * Odeslání zprávy
	 *
	 */
	class AkceSendTemplate extends AbstractAction {
		private static final long serialVersionUID = 11L;


		public AkceSendTemplate() {
			// ImageIcon icon = new ImageIcon(getClass().getResource("/images/"+jazyk.getCountry()+".gif"));
			// putValue(SMALL_ICON, icon);
			putValue(NAME, textyAplikace.getString("sendMessageT"));
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		}


		@Override
		public void actionPerformed(ActionEvent arg0){
			String msg = "ACK#121#123456654321#KCA#";
			posliZpravu(msg);
		}
	}


	/***
	 * Odeslání zprávy
	 *
	 */
	class AkceSendServer extends AbstractAction {
		private static final long serialVersionUID = 11L;


		public AkceSendServer() {
			// ImageIcon icon = new ImageIcon(getClass().getResource("/images/"+jazyk.getCountry()+".gif"));
			// putValue(SMALL_ICON, icon);
			putValue(NAME, textyAplikace.getString("sendMessageT"));
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		}


		@Override
		public void actionPerformed(ActionEvent arg0){
			String msg = MessageFactory.serverMessage("list");
			logger.info("odesilam: "+msg);
			posliZpravu(msg);
		}
	}
	
//######################################################################x
	

	
}
