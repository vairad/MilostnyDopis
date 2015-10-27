package gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
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

 private JFrame okno = new JFrame();
 
 private JMenuBar menu = new JMenuBar();
 private JMenu menuEdit, menuFile, menuHelp;
 private JMenuItem save,copy,paste,open,novy,exit,about;
 private JButton lang;
 private JLabel content;
 
 
 private Locale jazyk = new Locale("cs","CZ") ;
 
 private ResourceBundle zpravy = ResourceBundle.getBundle("Texts",jazyk);
 
//############################################################################

 
 /**
  * Konstruktor vytvoří
  *  
  */
 public GUIInter(){
	 okno.setLocationRelativeTo(null); // okno na stred
	 okno.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // krizek ukonci program
	 okno.setTitle(zpravy.getString("windowTitle")); // nastavi titulek okna
	 okno.setPreferredSize(new Dimension(300, 200)); // nastaví výchozí rozmer
	 
	 generujMenu(); //pripravi vodorovne menu
	 
	 generujObsah(); // pripravi obsah
	 
	 okno.pack(); // idealizuj rozmery okna
	 okno.setVisible(true); // zobraz
 }
 
 
 /**
  * Vygeneruje obsah okna p�id� ho do okna
  */
 private void generujObsah(){
	 
	 JPanel panel = new JPanel(new FlowLayout());
	 
	 	lang = new JButton(new AkceSwitch());
	 	lang.setPreferredSize(new Dimension(200,20));
	 panel.add(lang);
	 	content = new JLabel(zpravy.getString("content"));
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
	 menuFile = new JMenu(zpravy.getString("menuFile"));
	 	
	    save = new JMenuItem(zpravy.getString("save"));
		open = new JMenuItem(zpravy.getString("open"));
		novy = new JMenuItem(zpravy.getString("new"));
		
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
	 menuEdit = new JMenu(zpravy.getString("menuEdit"));
	 
	        copy = new JMenuItem(zpravy.getString("copy"));
			paste = new JMenuItem(zpravy.getString("paste"));
			
	 menuEdit.add(copy);
	 menuEdit.add(paste);
	 
	 return menuEdit;
 }
 
 
 /**
  * Vygeneruje svisl� menu n�pov�dy
  * @return JMenu N�pov�da
  */
 private JMenu menuNapoveda(){
	 menuHelp = new JMenu(zpravy.getString("menuHelp"));

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
 private void prepniJazyk(){

	 if(jazyk.getCountry()!="CZ"){
		 jazyk = new Locale("cs","CZ");
	 }
	 else
		 jazyk = new Locale("en","UK");
 }
 
 /**
  * Metoda nastav� v�echny texty v aplikace dle st�vaj�c�ho jazyka
  */
 private void nastavTexty(){
	zpravy = ResourceBundle.getBundle("Texts",jazyk);
	
	 okno.setTitle(zpravy.getString("windowTitle"));
	 
	 menuEdit.setText(zpravy.getString("menuEdit"));
	 menuFile.setText(zpravy.getString("menuFile"));
	 menuHelp.setText(zpravy.getString("menuHelp"));
	 
	 save.setText(zpravy.getString("save"));
	 copy.setText(zpravy.getString("copy"));
	 paste.setText(zpravy.getString("paste"));
	 open.setText(zpravy.getString("open"));
	 novy.setText(zpravy.getString("new"));
	 exit.setText(zpravy.getString("exit"));
	 about.setText(zpravy.getString("about"));
	 
	 // ImageIcon icon = new ImageIcon(getClass().getResource("/images/"+jazyk.getCountry()+".gif"));
	 //lang.setIcon(icon);
	 content.setText(zpravy.getString("content"));
	 
	 
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

	            putValue(NAME, zpravy.getString("about"));
	            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
	        }
		
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			JOptionPane.showMessageDialog(okno, 
										zpravy.getString("aboutText"),
										zpravy.getString("about"), 
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
	            putValue(NAME, zpravy.getString("exit"));
	            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
	        }
		@Override
		public void actionPerformed(ActionEvent arg0) {
		okno.dispose();
		}  
	}
	
	
//########


	/***
	 * Akce p�epnut� jazyka
	 *
	 */
	class AkceSwitch extends AbstractAction {
		private static final long serialVersionUID = 11L;


		public AkceSwitch() {
	          // ImageIcon icon = new ImageIcon(getClass().getResource("/images/"+jazyk.getCountry()+".gif"));
	           // putValue(SMALL_ICON, icon);
	            putValue(NAME, "Akce switch language;");
				putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
	        }
		
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			 
			prepniJazyk();
			nastavTexty();
			
		}  
	}
	
//######################################################################x	
	
public static void main(String[] args)
{
	new GUIInter();
}
	

	
}
