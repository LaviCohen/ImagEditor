package shapes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import components.LFontChooser;
import components.LFontChooser.FontHolder;
import languages.Translator;
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
		editDialog.setLayout(new GridLayout(5, 1));
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
		textPanel.add(new JLabel("text:"), Translator.getBeforeTextBorder());
		JTextField textField = new JTextField(text);
		textPanel.add(textField);
		editDialog.add(textPanel);
		JPanel colorPanel = new JPanel(new BorderLayout());
		colorPanel.add(new JLabel("color:"), Translator.getBeforeTextBorder());
		JLabel colorLabel = new JLabel();
		colorLabel.setOpaque(true);
		colorLabel.setBackground(color);
		colorPanel.add(colorLabel);
		FontHolder fontHolder = new FontHolder(this.font);
		JButton setFont = new JButton("Set Font");
		setFont.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				fontHolder.setFont(LFontChooser.openChooseFontDialog(editDialog, "Set Font", fontHolder.getFont(), null));
			}
		});
		editDialog.add(setFont);
		JButton setColorButton = new JButton("set color");
		setColorButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				colorLabel.setBackground(JColorChooser.showDialog(editDialog, "Choose Text color", color));
			}
		});
		colorPanel.add(setColorButton, Translator.getAfterTextBorder());
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
					cur.x = x;
					cur.y = y;
					cur.text = text;
					cur.color = color;
					cur.font = fontHolder.getFont();
					editDialog.dispose();
					Main.getShapeList().updateImage(cur);
					Main.board.repaint();
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(Main.f, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		editDialog.add(done);
		editDialog.pack();
		editDialog.setSize(editDialog.getWidth() + 50, editDialog.getHeight());
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
