package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import shapes.Picture;
import shapes.Rectagle;
import shapes.Shape;
import shapes.Text;

public class Actions {
	public static void action(String command) {
		if (command.equals("Save")) {
			save();
		}else if (command.equals("Set Paper Size")) {
			Main.board.setPaperSize(Integer.parseInt(JOptionPane.showInputDialog("Enter Width:")),
					Integer.parseInt(JOptionPane.showInputDialog("Enter Width:")));
		}else if (command.equals("Rectagle")) {
			addRectagle();
		}else if (command.equals("Text")) {
			addText();
		}else if (command.equals("Picture")) {
			addPicture();
		}else if (command.equals("edit layers")) {
			editLayers();
		}
	}
	public static void save() {
		JDialog saveDialog = new JDialog();
		saveDialog.setTitle("Save");
		saveDialog.setLayout(new GridLayout(3, 1));
		JPanel dirPanel = new JPanel(new BorderLayout());
		dirPanel.add(new JLabel("Directory:"), BorderLayout.WEST);
		JTextField dirField = new JTextField();
		dirField.setEditable(false);
		dirPanel.add(dirField);
		saveDialog.add(dirPanel);
		JButton browse = new JButton("Browse");
		browse.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = dirField.getText().equals("")?
						new JFileChooser():new JFileChooser(new File(dirField.getText()));
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc.showOpenDialog(saveDialog);
				File f = fc.getSelectedFile();
				dirField.setText(f.getAbsolutePath());
			}
		});
		dirPanel.add(browse, BorderLayout.EAST);
		JPanel namePanel = new JPanel(new BorderLayout());
		namePanel.add(new JLabel("Name"), BorderLayout.WEST);
		JTextField nameField = new JTextField("picture");
		namePanel.add(nameField);
		JComboBox<String> typeBox = new JComboBox<String>(new String[] {".png", ".jpg"});
		namePanel.add(typeBox, BorderLayout.EAST);
		saveDialog.add(namePanel);
		JButton save = new JButton("Save");
		save.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				File f = new File(
						dirField.getText() + "\\" + nameField.getText() + typeBox.getSelectedItem());
				System.out.println(f);
				try {
					System.out.println(Main.board.getWidth() + ", " + Main.board.getHeight());
					BufferedImage bf = new BufferedImage(Main.board.getWidth(), Main.board.getHeight(), 
							BufferedImage.TYPE_INT_RGB);
					System.out.println(Main.board.getDisplay() + "\n" + bf);
					Main.board.paintShapes(bf.getGraphics());
					JDialog d = new JDialog();
					d.add(new JLabel(new ImageIcon(bf)));
					d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					d.pack();
					d.setVisible(true);
					ImageIO.write(bf.getSubimage(0, 0, bf.getWidth(), bf.getHeight()), 
							typeBox.getSelectedItem().toString().substring(1), f);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				saveDialog.dispose();
			}
		});
		saveDialog.add(save);
		saveDialog.pack();
		saveDialog.setVisible(true);
	}
	public static void addRectagle() {
		Rectagle r = new Rectagle(0, 0, true, null, 100, 100, Color.BLUE);
		Main.board.addShape(r);
		r.edit();
	}
	public static void addText() {
		Text t = new Text(
				0, 0, true, null, Color.BLACK, new Font("Arial", Font.PLAIN, 20), "text");
		Main.board.addShape(t);
		t.edit();
	}
	public static void addPicture() {
		Picture p = new Picture(0, 0, true, null,
				new BufferedImage(150, 50, BufferedImage.TYPE_INT_RGB), 100, 100);
		Main.board.addShape(p);
		p.edit();
	}
	public static void editLayers() {
		JDialog layersDialog = new JDialog();
		layersDialog.setLayout(new BorderLayout());
		JComboBox<Shape> comboBox = new JComboBox<Shape>(Main.board.getShapesList().toArray(new Shape[0]));
		layersDialog.add(comboBox);
		JPanel options = new JPanel(new GridLayout(1, 2));
		JButton edit = new JButton("Edit");
		edit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				((Shape)comboBox.getSelectedItem()).edit();
			}
		});
		options.add(edit);
		JButton remove = new JButton("Remove");
		remove.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (JOptionPane.showConfirmDialog(layersDialog, "Are you sure?") == JOptionPane.YES_OPTION) {
					Main.board.getShapesList().remove(comboBox.getSelectedItem());
					layersDialog.dispose();
					Main.board.paintShapes();
				}
			}
		});
		options.add(remove);
		layersDialog.add(options, BorderLayout.EAST);
		layersDialog.pack();
		layersDialog.setVisible(true);
	}
}