package components;

import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

public class LMenu extends JMenuBar {
		private static final long serialVersionUID = 1L;
		public ActionListener AL;
		public JMenuItem[][] menuArr;
		public LMenu(String[][] menuNames, ActionListener AL) {
			int longest = 0;
			for (int i = 0; i < menuNames.length; i++) {
				if(menuNames[i].length>longest) {
					longest = menuNames[i].length;
				}
			}
			menuArr = new JMenuItem[menuNames.length][longest];
			this.AL = AL;
			translateToMenuItems(menuNames);
			addToMenuBar();
		}
		private void addToMenuBar() {
			for(int i=0;i<menuArr.length;i++) {
				if(menuArr[i][0]!=null) {
					//System.out.println("Menu " + menuArr[i][0].getText() + " added");
					this.add(menuArr[i][0]);
				}else {
					break;
				}
			}
			for(int i=0;i<menuArr.length;i++){	
				for(int j=1;j<menuArr[i].length;j++) {
					if(menuArr[i][j]!=null) {
						//System.out.println("menuItem " + menuArr[i][j].getText() + " added to Menu " + menuArr[i][0].getText());
						if(menuArr[i][j].getText().equals("---")) {
							menuArr[i][0].add(new JSeparator());
						}else {
							menuArr[i][0].add(menuArr[i][j]);
						}
					}else {
						break;
					}
				}
			}
			
		}
		private void translateToMenuItems(String[][] menuNames) {
			for(int i=0;i<menuNames.length;i++) {
				if(menuNames[i][0]!=null) {
					menuArr[i][0] = new JMenu(menuNames[i][0]);
				}
			}
			for(int i=0;i<menuNames.length;i++) {
				for(int j=1;j<menuNames[i].length;j++) {
					if(menuNames[i][j]!=null) {
							menuArr[i][j] = build(menuNames[i][j]);
					}else {
						break;
					}
				}
			}
		}
		private JMenuItem build(String menuItem) {
			JMenuItem MI;
			if(menuItem.contains("@")||menuItem.contains("#")||menuItem.contains("$")){
				int hotkeyLength = 1;
				String keyCombo = "";
				if(menuItem.contains("@")) {keyCombo += "shift ";hotkeyLength++;}
				if(menuItem.contains("#")) {keyCombo += "control ";hotkeyLength++;}
				if(menuItem.contains("$")) {keyCombo += "alt ";hotkeyLength++;}
				MI = new JMenuItem(menuItem.substring(0, menuItem.length() - hotkeyLength));
				keyCombo += (menuItem.charAt(menuItem.length()-1)+"").toUpperCase();
				KeyStroke hotkey = KeyStroke.getKeyStroke(keyCombo);
			    MI.setAccelerator(hotkey);
			}else if(menuItem.startsWith("[check-box]")){
				MI = new JCheckBoxMenuItem(menuItem.substring(11));
			}else {
				MI = new JMenuItem(menuItem);
			}
			MI.addActionListener(AL);
			return MI;
		}
	}