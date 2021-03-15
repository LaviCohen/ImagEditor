package languages;

import java.io.File;

import install.DataFile;

public class Language {
	public String name;
	public DataFile dataFile;
	
	public Language(String name, File f) {
		this.name = name;
		this.dataFile = new DataFile(f);
	}
	public String translate(String s) {
		return this.dataFile.getOrDefault(s, s);
	}
}
