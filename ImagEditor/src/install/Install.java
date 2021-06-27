package install;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JLabel;
import javax.swing.JWindow;

import languages.Translator;
import log.Logger;
import main.Main;

public class Install {
	public static String path = "C:\\ImagEditor" + Main.version;
	public static DataFile default_setting;
	public static boolean install() {
		JWindow w = new JWindow();
		w.setLayout(null);
		JLabel l = new JLabel("Installing...");
		l.setBounds(220, 20, 100, 20);
		w.add(l);
		w.setSize(500, 200);
		w.setLocation(433, 284);
		w.setVisible(true);
		
		new File(path).mkdir();
		
		getFile("My Gallery").mkdir();
		getFile("Languages").mkdir();
		getFile("Data").mkdir();
		getFile("Data\\Settings").mkdir();
		try {
			getFile("Data\\Settings\\default_setting.properties").createNewFile();
			default_setting = new DataFile(
					getFile("Data\\Settings\\default_setting.properties"));
			default_setting.putWithoutSave("paper_width", "1000");
			default_setting.putWithoutSave("paper_height", "600");
			default_setting.putWithoutSave("zoom", "100");
			default_setting.putWithoutSave("save_log_files", "true");
			default_setting.putWithoutSave("language", Translator.DEFAULT_LANGUAGE);
			default_setting.save("Original Default Settings");
		} catch (IOException e) {
			e.printStackTrace();
		}
		getFile("Data\\Logs").mkdir();
		try {
			getFile("Data\\Logs\\live log.txt").createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		w.dispose();
		return true;
	}
	public static boolean isInstalled() {
		return new File(path).exists();
	}
	public static boolean unInstall() {
		removeDirectory(new File(path));
		return !(new File(path).exists());
	}
	public static String getPath(String resPath) {
		if (resPath.startsWith(path)) {
			return resPath;
		}
		return path + "\\" + resPath;
	}
	public static File getFile(String resPath) {
		return new File(getPath(resPath));
	}
	public static String getText(String resPath) {
		try {
			FileInputStream fis = new FileInputStream(getFile(resPath));
			String text = "";
			int c = 0;
			while ((c = fis.read()) != -1) {
				text += (char)c;
			}
			return text;
		} catch (IOException e) {
			return null;
		}
	}
	public static void writeToFile(File f, String text) {
		try {
			FileOutputStream fos = new FileOutputStream(f);
			DataOutputStream dos = new DataOutputStream(fos);
			dos.write(text.getBytes());
			fos.close();
			dos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void unZip(String pathToZip, String targetFolder) throws IOException {
		try (ZipFile zipFile = new ZipFile(pathToZip)) {
			  Enumeration<? extends ZipEntry> entries = zipFile.entries();
			  while (entries.hasMoreElements()) {
				  ZipEntry entry = entries.nextElement();
				  File entryDestination = new File(targetFolder,  entry.getName());
				  if(entry.getName().startsWith("Res")) {
					  if(entry.isDirectory()) {
						  entryDestination.mkdirs();
					  }else {
						  entryDestination.getParentFile().mkdirs();
						  InputStream in = zipFile.getInputStream(entry);
						  Files.copy(in, entryDestination.toPath(), StandardCopyOption.REPLACE_EXISTING);
					  }
				  }
			  }
		}
	}
	public static void copyDirectory(File sourceLocation , File targetLocation)
		    throws IOException {
		if (sourceLocation.isDirectory()) {
			if (!targetLocation.exists()) {
				targetLocation.mkdir();
			}
			String[] children = sourceLocation.list();
			for (int i=0; i<children.length; i++) {
				copyDirectory(new File(sourceLocation, children[i]), new File(targetLocation, children[i]));
			}
		} else {
			Files.copy(sourceLocation.toPath(), new FileOutputStream(targetLocation));
		}
	}
	public static void removeDirectory(File f) {
		if (f.isDirectory()) {
			String[] children = f.list();
			for (int i = 0; i < children.length; i++) {
				removeDirectory(new File(f.getAbsoluteFile() + "\\" + children[i]));
			}
			f.delete();
		}else {
			f.delete();
		}
	}
	public static void init() {
		Main.board.setPaperSize(
				Integer.parseInt(default_setting.get("paper_width")),
				Integer.parseInt(default_setting.get("paper_height")));
		Main.getZoomSlider().slider.setValue(Integer.parseInt(default_setting.get("zoom")));
		Logger.saveLogFiles = default_setting.get("save_log_files").equals("true");
	}
	public static void initLanguage() {
		default_setting = new DataFile(getFile("Data\\Settings\\default_setting.properties"));
		Translator.setLanguage(default_setting.get("language"));
	}
	public static void setDefaultSetting(String settingName, Object value) {
		Install.default_setting.put(settingName, value);
	}
	public static void initPremiumSetting() {
		
	}
	public static void initNormalSetting() {
		
	}
}