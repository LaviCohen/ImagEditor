package components;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import languages.Translator;

public class LSlider extends JPanel{

	private static final long serialVersionUID = 1L;
	public JSlider slider;
	public JTextField field;
	public JLabel subject;
	public LSlider(String subject, int minValue, int maxValue, int defultValue) {
		super(new BorderLayout());
		this.subject = new JLabel(subject);
		this.add(this.subject, Translator.getBeforeTextBorder());
		this.slider = new JSlider(minValue, maxValue, defultValue);
		this.slider.setComponentOrientation(Translator.getComponentOrientation());
		this.add(slider);
		this.field = new JTextField(defultValue + "");
		this.add(field, Translator.getAfterTextBorder());
		this.slider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				field.setText(slider.getValue() + "");
			}
		});
		field.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				slider.setValue(Integer.valueOf(field.getText()));
				
			}
		});
	}
	public int getValue() {
		return slider.getValue();
	}
}