package shapes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

import main.Main;

public class Text extends Shape{
	Color color;
	Font font;
	String text;
	public Text(int x, int y, boolean visible, String name, Color color, Font font, String text) {
		super(x, y, visible, name);
		this.color = color;
		this.font = font;
		this.text = text;
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(color);
		g.setFont(font);
		g.drawString(text, x, y + getHeightOnBoard());
	}
	@Override
	public void edit() {
		JDialog editDialog = new JDialog(Main.f);
		editDialog.setLayout(new GridLayout(6, 1));
		editDialog.setTitle("Edit Text");
		JPanel positionPanel = new JPanel(new GridLayout(1, 4));
		positionPanel.add(new JLabel("X:"));
		JTextField xField = new JTextField(this.x + "");
		positionPanel.add(xField);
		positionPanel.add(new JLabel("Y:"));
		JTextField yField = new JTextField(this.y + "");
		positionPanel.add(yField);
		editDialog.add(positionPanel);
		JPanel textPanel = new JPanel(new BorderLayout());
		textPanel.add(new JLabel("text:"), BorderLayout.WEST);
		JTextField textField = new JTextField(text);
		textPanel.add(textField);
		editDialog.add(textPanel);
		JPanel colorPanel = new JPanel(new BorderLayout());
		colorPanel.add(new JLabel("color:"), BorderLayout.WEST);
		JLabel colorLabel = new JLabel();
		colorLabel.setOpaque(true);
		colorLabel.setBackground(color);
		colorPanel.add(colorLabel);
		JPanel fontPanel = new JPanel(new BorderLayout());
		fontPanel.add(new JLabel("font:"), BorderLayout.WEST);
		String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		JComboBox<String> fontsBox = new JComboBox<String>(fonts);
		fontsBox.setSelectedItem(this.font.getFontName());
		fontPanel.add(fontsBox);
		editDialog.add(fontPanel);
		JPanel fontProps = new JPanel(new BorderLayout());
		fontProps.add(new JLabel("size:"), BorderLayout.WEST);
		JSlider slider = new JSlider(0, 100, this.font.getSize());
		fontProps.add(slider);
		JCheckBox isBold = new JCheckBox("Bold", this.font.isBold());
		fontProps.add(isBold, BorderLayout.EAST);
		editDialog.add(fontProps);
		JButton setColorButton = new JButton("set color");
		setColorButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				colorLabel.setBackground(JColorChooser.showDialog(editDialog, "Choose Rectagle color", color));
			}
		});
		colorPanel.add(setColorButton, BorderLayout.EAST);
		editDialog.add(colorPanel);
		JButton done = new JButton("done");
		final Text cur = this;
		done.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int x = Integer.parseInt(xField.getText());
					int y = Integer.parseInt(yField.getText());
					String text = textField.getText();
					Color color = colorLabel.getBackground();
					String fontName = (String) fontsBox.getSelectedItem();
					int size = slider.getValue();
					boolean bold = isBold.isSelected();
					cur.x = x;
					cur.y = y;
					cur.text = text;
					cur.color = color;
					cur.font = new Font(fontName, bold?Font.BOLD:Font.PLAIN, size);
					editDialog.dispose();
					Main.board.paintShapes();
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(Main.f, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		editDialog.add(done);
		editDialog.pack();
		editDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		editDialog.setVisible(true);
	}

	@SuppressWarnings("deprecation")
	@Override
	public int getWidthOnBoard() {
		return Toolkit.getDefaultToolkit().getFontMetrics(font).stringWidth(text);
	}

	@SuppressWarnings("deprecation")
	@Override
	public int getHeightOnBoard() {
		return Toolkit.getDefaultToolkit().getFontMetrics(font).getHeight();
	}

}
