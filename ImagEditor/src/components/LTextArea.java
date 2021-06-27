package components;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextArea;

public class LTextArea extends JTextArea {
	private static final long serialVersionUID = 1L;
	private String hiddenText;
	private Color hiddenTextColor = Color.GRAY;
	private Color normalTextColor = Color.BLACK;
	public LTextArea(String hiddenText) {
		this.hiddenText = hiddenText;
		setForeground(hiddenTextColor);
		setText(hiddenText);
		this.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if (getText().equals(hiddenText)) {
					setText("");
					setForeground(normalTextColor);
				}
			}
			@Override
			public void focusLost(FocusEvent e) {
				if (getText().isEmpty()) {
					setForeground(hiddenTextColor);
					setText(hiddenText);
				}
			}
		});
	}
	public String getHiddenText() {
		return hiddenText;
	}
	public void setHiddenText(String hiddenText) {
		this.hiddenText = hiddenText;
	}
	public Color getHiddenTextColor() {
		return hiddenTextColor;
	}
	public void setHiddenTextColor(Color hiddenTextColor) {
		this.hiddenTextColor = hiddenTextColor;
	}
	public Color getNormalTextColor() {
		return normalTextColor;
	}
	public void setNormalTextColor(Color normalTextColor) {
		this.normalTextColor = normalTextColor;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
