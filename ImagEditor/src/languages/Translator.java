package languages;

public class Translator {
	public static Language language;
	public static String get(String s) {
		if (language == null) {
			return s;
		}
		return language.translate(s);
	}
}