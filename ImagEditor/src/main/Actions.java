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

import install.Install;
import shapes.Picture;
import shapes.Rectangle;
import shapes.Text;

public class Actions {
	public static void action(String command) {
		if (command.equals("Save")) {
			save();
		}else if (command.equals("Set Paper Size")) {
			Main.board.setPaperSize(Integer.parseInt(JOptionPane.showInputDialog("Enter Width:")),
					Integer.parseInt(JOptionPane.showInputDialog("Enter Width:")));
		}else if (command.equals("Rectangle")) {
			addRectagle();
		}else if (command.equals("Text")) {
			addText();
		}else if (command.equals("Picture")) {
			addPicture();
		}else if (command.equals("Edit")) {
			edit();
		}else if (command.equals("Refresh")) {
			Main.board.repaint();
			Main.updateShapeList();
		}
	}
	public static void save() {
		JDialog saveDialog = new JDialog(Main.f);
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
						new JFileChooser(Install.getPath("Gallery")):new JFileChooser(new File(dirField.getText()));
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
				System.out.println("Exporting image to " + f);
				try {
					BufferedImage bf = new BufferedImage(Main.board.getWidth(), Main.board.getHeight(), 
							typeBox.getSelectedItem().equals(".png")?
									BufferedImage.TYPE_INT_ARGB:BufferedImage.TYPE_INT_RGB);
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
		Rectangle r = new Rectangle(0, 0, true, null, 100, 100, Color.BLUE);
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
	public static void edit() {
		if (Main.shapeList.getSelectedShape() == null) {
			return;
		}
		Main.shapeList.getSelectedShape().edit();
	}
	public static void remove() {
		if (Main.shapeList.getSelectedShape() == null) {
			return;
		}
		if (JOptionPane.showConfirmDialog(Main.f, "Are you sure?") == JOptionPane.YES_OPTION) {
			Main.board.getShapesList().remove(Main.shapeList.getSelectedShape());
			Main.board.repaint();
			Main.updateShapeList();
		}
	}
}