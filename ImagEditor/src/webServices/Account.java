package webServices;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import install.Install;
import main.Main;
import shapes.Picture;

public class Account {
	
	public String userName;
	public String password;
	
	public static final String MALE = "male";
	public static final String FEMALE = "female";
	public static final String NONE_GENDER = "none";
	
	public String gender;
	public boolean isPremium;
	
	
	public static void login(String userName, String password) throws AccountUndefindException{
		String s = Main.website.getResponse("getAccount.php", "userName=" + userName + "&" + "password=" + password, "POST");
		login(s);
		if (Main.myAccount.isPremium) {
			Install.initPremiumSetting();
		}
	}
	public static void login(String userCode) throws AccountUndefindException {
		if (userCode == null || userCode.equals("") || userCode.equals("&&&&&&")) {
			throw new AccountUndefindException("Account undefind, incorrect userName or password");
		}else {
			Main.myAccount = decode(userCode);
		}
	}
	public static Account decode(String s) {
		String [] properties = s.split("&&");
		return new Account(properties[0], properties[1], properties[2], Boolean.valueOf(properties[3]));
	}
	public static String encode(Account a) {
		return a.userName + "&&" + a.password + "&&" + a.gender + "&&" + a.isPremium;
	}
	public Account(String userName, String password, String gender, boolean isPremium) {
		super();
		this.userName = userName;
		this.password = password;
		this.gender = gender;
		this.isPremium = isPremium;
	}
	@Override
	public String toString() {
		return "Account [userName=" + userName + ", password=" + password + ", gender=" + gender + ", isPremium="
				+ isPremium + "]";
	}
	public static void GUILogin() {
		JDialog d = new JDialog();
		d.setTitle("login");
		d.setLayout(new BorderLayout());
		
		JPanel dataPanel = new JPanel(new BorderLayout());
		
		JPanel userNamePanel = new JPanel(new BorderLayout());
		userNamePanel.add(new JLabel("User Name:"), BorderLayout.WEST);
		JTextField userNameField = new JTextField();
		userNamePanel.add(userNameField);
		
		dataPanel.add(userNamePanel, BorderLayout.NORTH);

		JPanel passwordPanel = new JPanel(new BorderLayout());
		passwordPanel.add(new JLabel("Password:"), BorderLayout.WEST);
		JPasswordField passwordField = new JPasswordField();
		passwordPanel.add(passwordField);
		
		dataPanel.add(passwordPanel, BorderLayout.SOUTH);
		
		JButton login = new JButton("Login");
		login.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				d.setVisible(false);
				try {
					login(userNameField.getText(), new String(passwordField.getPassword()));
				} catch (AccountUndefindException e2) {
					JOptionPane.showMessageDialog(Main.f, "logging in failed");
					d.setVisible(true);
					d.toFront();
					return;
				}
				JOptionPane.showMessageDialog(Main.f, "Hello " + Main.myAccount.userName + ", let\'s create some magic!");
				d.dispose();
			}
		});
		
		d.add(new JLabel("<html><font size=5>Login to Your Account</font></html>"), BorderLayout.NORTH);
		d.add(dataPanel);
		d.add(login, BorderLayout.SOUTH);
		d.pack();
		d.setVisible(true);
	}
	public static void showAccount() {
		Account current = Main.myAccount;
		JDialog d = new JDialog();
		d.setLayout(new BorderLayout());
		try {
			d.add(new JLabel(new ImageIcon(
					Picture.getScaledImage(ImageIO.read(Main.class.getResourceAsStream(current.gender + "Shadow.png")), 150, 150)))
					, BorderLayout.NORTH);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		JPanel personalDataPanel = new JPanel(new GridLayout(3, 1));
		
		if (current.isPremium) {
			personalDataPanel.add(new JLabel("<html><i>Premium Account</i></html>"));
		}
		personalDataPanel.add(new JLabel("UserName: " + current.userName));
		personalDataPanel.add(new JLabel("Gender: " + current.gender));
		
		
		d.add(personalDataPanel);
		
		JButton login = new JButton((current == Main.LOCAL_ACCOUNT?"login":"logout"));
		login.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (current == Main.LOCAL_ACCOUNT) {
					d.dispose();
					Account.GUILogin();
				}else {	
					d.dispose();
					logout();
					Account.showAccount();
				}
			}
		});
		d.add(login, BorderLayout.SOUTH);
		d.pack();
		d.setVisible(true);
	}
	public static void logout() {
		Main.myAccount = Main.LOCAL_ACCOUNT;
		Install.initNormalSetting();
	}
}