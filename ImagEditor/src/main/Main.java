package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import components.Board;
import components.LMenu;
import components.LSlider;
import components.ShapeList;
import install.Install;
import install.Resources;
import languages.Translator;
import log.Logger;
import log.ExceptionUtils;
import shapes.Picture;
import shapes.Rectangle;
import shapes.Shape;
import shapes.Text;
import webServices.Account;
import webServices.Website;


/**
 * This class contains the main method and set up the whole program.
 * In addition, this class keeps all the static important variables, for example:
 * frame, board, website, account etc.
 * */
public class Main {
	/**
	 * Holds the number of the version with minor version (after the decimal point)
	 * */
	public static final double version = 2.0;
	/**
	 * The frame of the app.
	 * */
	public static JFrame f;
	/**
	 * Represent the graphic board, which use as preview to the image which edited.
	 * */
	public static Board board;
	/**
	 * Represents the website of the product for using its services (as accounts, for example).
	 * @see webServices.Website
	 * */
	public static Website website = new Website("http://localhost/imagEditor/");
	/**
	 * The default account, which uses in the case of none account logged-in.
	 * */
	public static final Account LOCAL_ACCOUNT = new Account("local account", "", "none", false);
	/**
	 * Current logged-in account.
	 * As default, LOCAL_ACCOUNT.
	 * */
	public static Account myAccount = LOCAL_ACCOUNT;
	/**
	 * The side bar which holds the shapeList and some action buttons.
	 * */
	private static JPanel shapeListPanel;
	/**
	 * GUI list of all the shapes are currently exist. 
	 * */
	private static ShapeList shapeList;
	/**
	 * The label which holds the size of the paper (width x height).
	 * */
	private static JLabel sizeLabel;
	/**
	 * The slider which uses to set the zoom of the paper.
	 * */
	private static LSlider zoomSlider;
	/**
	 * The scrollable wrapper of the paper.
	 * */
	private static JScrollPane boardScrollPane;
	/**
	 * ActionListener for all of the menu actions.
	 * */
	private static ActionListener menuListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			System.out.println("Menu Event [" + command + "]");
			Actions.action(Translator.get(command));
		}
	};
	private static long initTime;
	public static void main(String[] args) {
		System.out.println("Start-Up");
		long millis = System.currentTimeMillis();
		if (Install.getFile("Data\\Logs\\live log.txt").exists()) {
			long pause = System.currentTimeMillis();
			if(JOptionPane.YES_OPTION == 
					JOptionPane.showConfirmDialog(null, "<html>Last time, the app crashed.<br/>"
					+ "would you like to send us auto report about it?</html>")){
				website.sendReport("Auto Reporter", "Crash Report", 
						Install.getText("Data\\Logs\\live log.txt"));
			}
			millis += System.currentTimeMillis() - pause;
		}
		Logger.initializeLogger();
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				System.out.println("Error has been reported - ID " + ++Logger.errorCount);
				Logger.getErrorLogger().println(ExceptionUtils.exceptionToString((Exception)e, t, Logger.getErrorCount()));;
				if (Logger.printInConsole) {
					e.printStackTrace(Logger.err);
				}
				int criticality = ExceptionUtils.getCriticality((Exception)e);
				if(criticality == 2) {
					int ans = JOptionPane.showOptionDialog(f, "An error has occurred", "Warning", 0, JOptionPane.WARNING_MESSAGE
							, null, new String[] {"Open Log", "Cancel"}, 0);
					if (ans == 0) {
						System.out.println("Opening error log from error message");
						Actions.action("Log");
					}
				}
				if(criticality > 2) {
					int ans = JOptionPane.showOptionDialog(f, "An error has occurred", "ERROR", 0, JOptionPane.ERROR_MESSAGE
							, null, new String[] {"Open Log", "Cancel"}, 0);
					if (ans == 0) {
						System.out.println("Opening error log from error message");
						Actions.action("Log");
					}
				}
			}
		});
		if(!Install.isInstalled()) {
			int answer = JOptionPane.showConfirmDialog(f, "Do you want to install ImageEditor v" + version + "?");
			switch (answer) {
				case JOptionPane.YES_OPTION:
					if(Install.install()) {
						Logger.initializeLiveLogger();
						JOptionPane.showMessageDialog(f, "Install done successfully!");
					}else {
						JOptionPane.showMessageDialog(f, "Error: install failed", "Install Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					break;
				default:
					System.exit(0);
					return;
			}
		}
		Install.initLanguage();
		f = new JFrame(Translator.get("ImagEditor v") + version);
		zoomSlider = new LSlider(Translator.get("Zoom") + ":", 10, 200, 100);
		shapeListPanel = new JPanel(new BorderLayout());
		Resources.init();
		initJMenuBar();
		board = new Board(Color.WHITE, 1000, 600);
		board.repaint();
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		f.setIconImage(Resources.logo.getImage());
		f.setLayout(new BorderLayout());
		boardScrollPane = new JScrollPane(board, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		f.add(boardScrollPane, BorderLayout.CENTER);
		initControlBar();
		updateShapeList();
		initShapeListPanel();
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		f.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {}
			@Override
			public void windowIconified(WindowEvent e) {}
			@Override
			public void windowDeiconified(WindowEvent e) {}
			@Override
			public void windowDeactivated(WindowEvent e) {}
			@Override
			public void windowClosing(WindowEvent e) {}
			@Override
			public void windowClosed(WindowEvent e) {
				System.out.println("Post-closing work started");
				if (Logger.saveLogFiles) {
					System.out.println("Saving log file");
					File f = Install.getFile(
							"Data\\Logs\\Log saved at " + System.currentTimeMillis() + ".txt");
					try {
						f.createNewFile();
						Logger.disableTimeStamp();
						System.out.println("Run statistics:\nErrors: " + Logger.getErrorCount() + "\nInit Time: " + initTime + " ms");
						Logger.exportTo(f);
						System.out.println("Log file saved successfully");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				Logger.stop();
				System.gc();
				new File(Install.path + "\\Data\\Logs\\live log.txt").deleteOnExit();
			}
			@Override
			public void windowActivated(WindowEvent e) {}
		});
		Install.init();
		f.applyComponentOrientation(Translator.getComponentOrientation());
		boardScrollPane.applyComponentOrientation(ComponentOrientation.UNKNOWN);
		f.setVisible(true);
		initTime = (System.currentTimeMillis() - millis);
		System.out.println("Init took " + initTime + " milli-seconds");
	}
	public static void initControlBar() {
		JPanel controlBar = new JPanel(new BorderLayout());
		sizeLabel = new JLabel(board.paper.getWidth() + "x" + board.paper.getHeight());
		controlBar.add(getSizeLabel(), Translator.getAfterTextBorder());
		zoomSlider.slider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				board.repaint();
			}
		});
		controlBar.add(zoomSlider, Translator.getBeforeTextBorder());
		f.add(controlBar, BorderLayout.SOUTH);
	}
	public static void initShapeListPanel() {
		shapeListPanel.add(new JLabel("<html><font size=30>" + 
				Translator.get("Layers") + "</font></html>"), BorderLayout.NORTH);
		JPanel actionsPanel = new JPanel(new GridLayout(2, 2));
		JButton edit = new JButton(Resources.editIcon);
		edit.setToolTipText(Translator.get("Edit selected shape"));
		edit.setBackground(Color.WHITE);
		edit.setFocusPainted(false);
		actionsPanel.add(edit);
		edit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (getShapeList().getSelectedShape() != null) {
					getShapeList().getSelectedShape().edit();
				}
			}
		});
		JButton remove = new JButton(Resources.removeIcon);
		remove.setToolTipText(Translator.get("Remove selected shape"));
		remove.setBackground(Color.WHITE);
		remove.setFocusPainted(false);
		actionsPanel.add(remove);
		remove.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (getShapeList().getSelectedShape() != null) {
					board.getShapesList().remove(getShapeList().getSelectedShape());
					board.repaint();
					updateShapeList();
				}
			}
		});
		shapeListPanel.add(actionsPanel, BorderLayout.SOUTH);
		JButton uplayer = new JButton(Resources.up_layerIcon);
		uplayer.setToolTipText(Translator.get("Move selected shape 1 layer up"));
		uplayer.setBackground(Color.WHITE);
		uplayer.setFocusPainted(false);
		actionsPanel.add(uplayer);
		uplayer.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (getShapeList().getSelectedShape() != null) {
					Shape s = getShapeList().getSelectedShape();
					if (board.getShapesList().getLast() == s) {
						JOptionPane.showMessageDialog(Main.f, 
								Translator.get("This is the top layer!"),
								Translator.get("Warning"), JOptionPane.WARNING_MESSAGE);
						return;
					}
					int sIndex = board.getShapesList().indexOf(s);
					int upIndex = board.getShapesList().indexOf(s) + 1;
					Shape up = board.getShapesList().get(upIndex);
					board.getShapesList().set(upIndex, s);
					board.getShapesList().set(sIndex, up);
					board.repaint();
					updateShapeList();
				}
			}
		});
		shapeListPanel.add(actionsPanel, BorderLayout.SOUTH);
		JButton downlayer = new JButton(Resources.down_layerIcon);
		downlayer.setToolTipText(Translator.get("Move selected shape 1 layer down"));
		downlayer.setBackground(Color.WHITE);
		downlayer.setFocusPainted(false);
		actionsPanel.add(downlayer);
		downlayer.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (getShapeList().getSelectedShape() != null) {
					Shape s = getShapeList().getSelectedShape();
					if (board.getShapesList().getFirst() == s) {
						JOptionPane.showMessageDialog(Main.f, 
								Translator.get("This is the down layer!"),
								Translator.get("Warning"), JOptionPane.WARNING_MESSAGE);
						return;
					}
					int sIndex = board.getShapesList().indexOf(s);
					int downIndex = board.getShapesList().indexOf(s) - 1;
					Shape down = board.getShapesList().get(downIndex);
					board.getShapesList().set(downIndex, s);
					board.getShapesList().set(sIndex, down);
					board.repaint();
					updateShapeList();
				}
			}
		});
		shapeListPanel.add(actionsPanel, BorderLayout.SOUTH);
		f.add(shapeListPanel, Translator.getBeforeTextBorder());
	}
	public static void updateShapeList() {
		System.out.println("Update shapeList");
		Shape s = null;
		if (getShapeList() != null) {
			s = getShapeList().getSelectedShape();
			shapeListPanel.remove(getShapeList());
		}
		shapeList = new ShapeList(board.getShapesList().toArray(new Shape[0]));
		shapeListPanel.add(getShapeList(), BorderLayout.CENTER);
		if (s != null) {
			getShapeList().setSelection(s);
		}
		f.revalidate();
		f.repaint();
	}
	public static void initJMenuBar() {
		LMenu lMenu = new LMenu(new String[][] 
				{
			{Translator.get("File"), Translator.get("Save") + "#s", Translator.get("Set Paper Size"),
				Translator.get("Set Language"), Translator.get("Log"), Translator.get("Send Report"),
				Translator.get("Visit Website")},
			{Translator.get("Actions"), Translator.get("Edit") + "#e", Translator.get("Set Paper Size"),
				Translator.get("Refresh") + "#r"},
			{Translator.get("Add"), Translator.get("Rectangle") + "@r", Translator.get("Text") + "@t", 
				Translator.get("Picture") + "@p"},
			{Translator.get("Account"), Translator.get("Profile")}
				}
		, menuListener);
		f.setJMenuBar(lMenu);
	}
	public static JPopupMenu getPopupMenuForShape(Shape s) {
		JPopupMenu popup = new JPopupMenu("Options");
		JMenuItem setName = new JMenuItem(Translator.get("Set Name"));
		setName.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				s.setName(JOptionPane.showInputDialog(Translator.get("Enter the new name for")
						+ " \"" + s.getName() + "\""));
				Main.updateShapeList();
			}
		});
		popup.add(setName);
		JMenuItem edit = new JMenuItem(Translator.get("Edit"));
		edit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				s.edit();
			}
		});
		popup.add(edit);
		if (s instanceof Text || s instanceof Rectangle) {
			return popup;	
		}
		popup.add(new JSeparator());
		JMenuItem editEffects = new JMenuItem("Edit Effects");
		editEffects.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				((Picture)s).editEffects();
			}
		});
		popup.add(editEffects);
		JMenuItem copy = new JMenuItem("Copy as image");
		copy.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Main.board.addShape(((Picture)s).copy());
			}
		});
		popup.add(copy);
		return popup;
	}
	public static ShapeList getShapeList() {
		return shapeList;
	}
	public static LSlider getZoomSlider() {
		return zoomSlider;
	}
	public static JLabel getSizeLabel() {
		return sizeLabel;
	}
}