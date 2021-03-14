package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import components.Board;
import components.LSlider;
import components.ShapeList;
import shapes.Picture;
import shapes.Rectagle;
import shapes.Shape;
import shapes.Text;

public class Main {
	public static final double version = 1.0;
	public static JFrame f = new JFrame("ImageEditor v" + version);
	public static Board board;
	public static JPanel shapeListPanel = new JPanel(new BorderLayout());
	public static ShapeList shapeList;
	public static JLabel sizeLabel;
	public static LSlider zoomSlider = new LSlider("Zoom:", 10, 200, 100);
	public static ActionListener menuListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			Actions.action(command);
		}
	}; 
	public static void main(String[] args) {
		initJMenuBar();
		board = new Board(Color.WHITE, 1000, 600);
		board.paintShapes();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setLayout(new BorderLayout());
		f.add(new JScrollPane(board, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
		initControlBar();
		updateShapeList();
		initShapeListPanel();
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		f.setVisible(true);
	}
	public static void initControlBar() {
		JPanel controlBar = new JPanel(new BorderLayout());
		sizeLabel = new JLabel(board.getWidth() + "x" + board.getHeight());
		controlBar.add(sizeLabel, BorderLayout.EAST);
		zoomSlider.slider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				board.paintShapes();
			}
		});
		controlBar.add(zoomSlider, BorderLayout.WEST);
		f.add(controlBar, BorderLayout.SOUTH);
	}
	public static void initShapeListPanel() {
		shapeListPanel.add(new JLabel("<html><font size=30>Layers</font></html>"), BorderLayout.NORTH);
		JPanel actionsPanel = new JPanel(new GridLayout(2, 2));
		JButton edit = new JButton("edit");
		actionsPanel.add(edit);
		edit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (shapeList.getSelectedShape() != null) {
					shapeList.getSelectedShape().edit();
				}
			}
		});
		JButton remove = new JButton("remove");
		actionsPanel.add(remove);
		remove.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (shapeList.getSelectedShape() != null) {
					board.getShapesList().remove(shapeList.getSelectedShape());
					board.paintShapes();
					updateShapeList();
				}
			}
		});
		shapeListPanel.add(actionsPanel, BorderLayout.SOUTH);
		JButton uplayer = new JButton("up layer");
		actionsPanel.add(uplayer);
		uplayer.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (shapeList.getSelectedShape() != null) {
					Shape s = shapeList.getSelectedShape();
					if (board.getShapesList().getLast() == s) {
						JOptionPane.showMessageDialog(Main.f, "This is the top layer!",
								"Warning", JOptionPane.WARNING_MESSAGE);
						return;
					}
					int sIndex = board.getShapesList().indexOf(s);
					int upIndex = board.getShapesList().indexOf(s) + 1;
					Shape up = board.getShapesList().get(upIndex);
					board.getShapesList().set(upIndex, s);
					board.getShapesList().set(sIndex, up);
					board.paintShapes();
					updateShapeList();
				}
			}
		});
		shapeListPanel.add(actionsPanel, BorderLayout.SOUTH);
		JButton downlayer = new JButton("down layer");
		actionsPanel.add(downlayer);
		downlayer.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (shapeList.getSelectedShape() != null) {
					Shape s = shapeList.getSelectedShape();
					if (board.getShapesList().getFirst() == s) {
						JOptionPane.showMessageDialog(Main.f, "This is the down layer!",
								"Warning", JOptionPane.WARNING_MESSAGE);
						return;
					}
					int sIndex = board.getShapesList().indexOf(s);
					int downIndex = board.getShapesList().indexOf(s) - 1;
					Shape down = board.getShapesList().get(downIndex);
					board.getShapesList().set(downIndex, s);
					board.getShapesList().set(sIndex, down);
					board.paintShapes();
					updateShapeList();
				}
			}
		});
		shapeListPanel.add(actionsPanel, BorderLayout.SOUTH);
		f.add(shapeListPanel, BorderLayout.EAST);
	}
	public static void updateShapeList() {
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
		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem save = new JMenuItem("Save");
		save.addActionListener(menuListener);
		file.add(save);
		JMenuItem setPaperSize = new JMenuItem("Set Paper Size");
		setPaperSize.addActionListener(menuListener);
		file.add(setPaperSize);
		menuBar.add(file);
		JMenu add = new JMenu("Add");
		menuBar.add(add);
		JMenuItem rect = new JMenuItem("Rectagle");
		rect.addActionListener(menuListener);
		add.add(rect);
		JMenuItem text = new JMenuItem("Text");
		text.addActionListener(menuListener);
		add.add(text);
		JMenuItem picture = new JMenuItem("Picture");
		picture.addActionListener(menuListener);
		add.add(picture);
		JMenu edit = new JMenu("Edit");
		menuBar.add(edit);
		JMenuItem editLayers = new JMenuItem("edit layers");
		editLayers.addActionListener(menuListener);
		edit.add(editLayers);
		f.setJMenuBar(menuBar);
	}
	public static JPopupMenu getPopupMenuForShape(Shape s) {
		JPopupMenu popup = new JPopupMenu("Options");
		JMenuItem setName = new JMenuItem("Set Name");
		setName.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				s.setName(JOptionPane.showInputDialog("Enter the new name for \"" + s.getName() + "\""));
				Main.updateShapeList();
			}
		});
		popup.add(setName);
		JMenuItem edit = new JMenuItem("Edit");
		edit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				s.edit();
			}
		});
		popup.add(edit);
		if (s instanceof Text || s instanceof Rectagle) {
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