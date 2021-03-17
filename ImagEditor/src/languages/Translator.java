package languages;

import java.io.File;

import javax.swing.JOptionPane;

import install.Install;
import main.Main;

public class Translator {
	public static Language language;
	public static final String DEFAULT_LANGUAGE = "English";
	public static String get(String s) {
		if (language == null) {
			return s;
		}
		return language.translate(s);
	}
	public static void setLanguage(String name) {
		if (name == null || name.equals(DEFAULT_LANGUAGE)) {
			setLanguage((Language)null);
		}else {
			setLanguage(new Language(name, Install.getFile("Languages\\" + name + ".lng")));
		}
	}
	public static void setLanguage(Language newLanguage) {
		Translator.language = newLanguage;
	}
	public static void showChangeLanguageDialog() {
		File f = Install.getFile("Languages");
		String[] allLanguages = f.list();
		String[] displayLanguages = new String[allLanguages.length + 1];
		displayLanguages[0] = DEFAULT_LANGUAGE;
		for (int i = 1; i < displayLanguages.length; i++) {
			displayLanguages[i] = allLanguages[i].substring(0, allLanguages[i].indexOf('.'));
		}
		String ans = JOptionPane.showInputDialog(Main.f, "Choose Language:", "Languages",
				JOptionPane.QUESTION_MESSAGE, null, displayLanguages, 
				getLanguageName()).toString();
		setLanguage(ans);
	}
	public static Object getLanguageName() {
		if (language == null) {
			return DEFAULT_LANGUAGE;
		}
		return language.name;
	}
}