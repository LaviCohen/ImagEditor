package languages;

import java.util.HashMap;
import java.util.Scanner;

import java.io.File;
import java.io.FileNotFoundException;

public class Language {
	public String name;
	public HashMap<String, String> dictionary = new HashMap<String, String>();
	
	public Language(String name, File f) {
		this.name = name;
		try {
			Scanner scanner = new Scanner(f, "UTF-8");
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (line.charAt(0) == 65279) {
					line = line.substring(1);
				}
				if (!line.startsWith("#")) {
					addToDictionary(line);
				}
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	public void addToDictionary(String line) {
		String[] arr = line.split("=");
		dictionary.put(arr[0], arr[1]);
	}
	public String translate(String s) {
		return this.dictionary.getOrDefault(s, s);
	}
}