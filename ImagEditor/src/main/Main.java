package main;

import java.awt.BorderLayout;
import java.awt.Color;
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
import shapes.Picture;
import shapes.Rectangle;
import shapes.Shape;
import shapes.Text;

public class Main {
	public static final double version = 2.0;
	public static JFrame f;
	public static Board board;
	public static JPanel shapeListPanel;
	public static ShapeList shapeList;
	public static JLabel sizeLabel;
	public static LSlider zoomSlider;
	public static JScrollPane boardScrollPane;
	public static ActionListener menuListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			System.out.println("Menu Event [" + command + "]");
			Actions.action(command);
		}
	};
	public static void main(String[] args) {
		Logger.initializeLogger();
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				System.out.println("hi");
				Logger.errorCount++;
				Logger.reportInLog((Exception) e, t);
				if (Logger.printInConsole) {
					e.printStackTrace();
				}
			}
		});
		if(!Install.isInstalled()) {
			int answer = JOptionPane.showConfirmDialog(f, "Do you want to install PicturEditor v" + version + "?");
			switch (answer) {
				case JOptionPane.YES_OPTION:
					if(Install.install()) {
						Logger.initializeLiveLogger();
						JOptionPane.showMessageDialog(f, "Install done successfuly!");
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
				if (Logger.saveLogFiles) {
					File f = Install.getFile(
							"Data\\Logs\\Log saved at " + System.currentTimeMillis() + ".txt");
					try {
						f.createNewFile();
						Logger.exportTo(f);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
			@Override
			public void windowActivated(WindowEvent e) {}
		});
		Install.init();
		f.setVisible(true);
	}
	public static void initControlBar() {
		JPanel controlBar = new JPanel(new BorderLayout());
		sizeLabel = new JLabel(board.paper.getWidth() + "x" + board.paper.getHeight());
		controlBar.add(sizeLabel, BorderLayout.EAST);
		zoomSlider.slider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				board.repaint();
			}
		});
		controlBar.add(zoomSlider, BorderLayout.WEST);
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
				if (shapeList.getSelectedShape() != null) {
					shapeList.getSelectedShape().edit();
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
				if (shapeList.getSelectedShape() != null) {
					board.getShapesList().remove(shapeList.getSelectedShape());
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
				if (shapeList.getSelectedShape() != null) {
					Shape s = shapeList.getSelectedShape();
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
				if (shapeList.getSelectedShape() != null) {
					Shape s = shapeList.getSelectedShape();
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
		f.add(shapeListPanel, BorderLayout.EAST);
	}
	public static void updateShapeList() {
		System.out.println("Update shapeList");
		Shape s = null;
		if (shapeList != null) {
			s = shapeList.getSelectedShape();
			shapeListPanel.remove(shapeList);
		}
		shapeList = new ShapeList(board.getShapesList().toArray(new Shape[0]));
		shapeListPanel.add(shapeList, BorderLayout.CENTER);
		if (s != null) {
			shapeList.setSelection(s);
		}
		f.revalidate();
		f.repaint();
	}
	public static void initJMenuBar() {
		LMenu lMenu = new LMenu(new String[][] 
				{
			{"File", "Save#s", "Set Paper Size"},
			{"Actions", "Edit#e", "Set Paper Size", "Refresh#r"},
			{"Add", "Rectangle@r", "Text@t", "Picture@p"},
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
				Main.board.addShape(new Picture(0, 0, true, "Copy of " + s.getName(), ((Picture)s).getImageToDisplay(), 
						((Picture)s).getWidth(), ((Picture)s).getHeight()));
			}
		});
		popup.add(copy);
		return popup;
	}
}