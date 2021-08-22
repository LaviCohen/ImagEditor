package components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import components.LSearchableComboBox.Styler;
import components.LSearchableComboBox.StylingManager;

public class LFontChooser {
	private static final Font DEFAULT_FONT = Font.decode("Arial");
	public static class FontHolder{
		Font font;
		public FontHolder(Font font) {
			this.font = font;
		}
		public Font getFont() {
			return font;
		}

		public void setFont(Font font) {
			this.font = font;
		}
	}
	private static final String DEFAULT_PREVIEW_TEXT = "<html>Your text will look like this:<br/>"
			+ "abcdefghijklmnopqrstuvwxyz1234567890/*-+!@#$%^&*()_+?\\/\'\":`~;</html>";
	public static Font openChooseFontDialog(Window owner, String title, Font baseFont, String previewText) {
		if (baseFont == null) {
			baseFont = DEFAULT_FONT;
		}
		if (owner == null) {
			owner = JOptionPane.getRootFrame();
		}
		FontHolder fontHolder = new FontHolder(baseFont);
		JDialog dialog = new JDialog(owner, title, ModalityType.APPLICATION_MODAL);
		dialog.setLayout(new BorderLayout());
		LSearchableComboBox<String> familyBox = new LSearchableComboBox<>(
				GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames(), 0, 
				new StylingManager() {
					
					@Override
					public Styler getStylerFor(Object source) {
						return new Styler(source, source + "      abcdeABCDE", source.toString(), Color.BLACK);
					}
				}
			);
		familyBox.setSelectedItem(baseFont.getFamily());
		JPanel leftPanel = new JPanel(new GridLayout(4, 0));
		JPanel familyPanel = new JPanel(new BorderLayout());
		familyPanel.add(new JLabel("Family:"), BorderLayout.WEST);
		familyPanel.add(familyBox);
		leftPanel.add(familyPanel);
		LSlider sizeSlider = new LSlider("Size:", 0, 100, baseFont.getSize());
		leftPanel.add(sizeSlider);
		JPanel stylePanel = new JPanel(new GridLayout());
		JCheckBox bold = new JCheckBox("bold", baseFont.getStyle() >= Font.BOLD);
		stylePanel.add(bold);
		JCheckBox italic = new JCheckBox("italic", baseFont.getStyle() >= Font.ITALIC);
		stylePanel.add(italic);
		leftPanel.add(stylePanel);
		JPanel buttons = new JPanel(new GridLayout());
		JButton previewButton = new JButton("Preview");
		previewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int style = (bold.isSelected() ? Font.BOLD : 0) + (italic.isSelected() ? Font.ITALIC : 0);
				fontHolder.setFont(
						new Font(familyBox.getSelectedItem(), style, sizeSlider.getValue()));
				openPreviwDialog(previewText == null ? DEFAULT_PREVIEW_TEXT : previewText, dialog, fontHolder.getFont());
			}
		});
		buttons.add(previewButton);
		JButton apply = new JButton("Apply");
		apply.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int style = (bold.isSelected() ? Font.BOLD : 0) + (italic.isSelected() ? Font.ITALIC : 0);
				fontHolder.setFont(
						new Font(familyBox.getSelectedItem(), style, sizeSlider.getValue()));
				dialog.setVisible(false);
			}
		});
		buttons.add(apply);
		leftPanel.add(buttons);
		dialog.add(leftPanel);
		dialog.pack();
		dialog.setVisible(true);
		dialog.dispose();
		return fontHolder.getFont();
	}
	private static void openPreviwDialog(String text, JDialog parent, Font font) {
		JDialog dialog = new JDialog(parent, "Preview", true);
		JLabel label = new JLabel(text);
		label.setFont(font);
		dialog.add(label);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.pack();
		dialog.setVisible(true);
	}
}