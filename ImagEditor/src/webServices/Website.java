package webServices;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

import javax.swing.JOptionPane;

import log.Logger;
import main.Main;

/**
 * Represents the website of the product to use its web services
 * */
public class Website {
	public String webAddress;
	public Website(String webAdress) {
		this.webAddress = webAdress;
	}
	public void openInBrowser() {
		openInBrowser("chrome");
	}
	public void openInBrowser(String browser) {
		try {
			Runtime.getRuntime().exec("cmd /c start " + browser + " " + webAddress);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String sendReport(String name, String title, String report) {
		if (!checkInternetConnection()) {
			return "There is no internet connection, please try again later";
		}
		if (!checkWebsite()) {
			return "Web services are unavaliable right now, please try again later";
		}
		String urlParams = "name=" + name.replaceAll(" ", "+") + "&title=" + title.replaceAll(" ", "+") + 
		"&report=" + report.replaceAll(" ", "+");
		return getGetResponse(webAddress + "/report.php?" + urlParams);
	}
	public void sendAutoReport(Exception e, Thread t) {
		if (!checkInternetConnection() || !checkWebsite()) {
			return;
		}
        @SuppressWarnings("unused")
		String urlParams = "exception=" + e.toString().replaceAll(" ", "+") + 
        		"&description=" + Logger.exceptionToString(e, t, Logger.getErrorCount()).replace(" ", "+") + 
		"&place=" + getErrorPlace(e).replaceAll(" ", "+");
		System.out.println("reported");
	}
	public void checkUpdate() {
		if (!checkInternetConnection()) {
			System.out.println("no internet connection");
			JOptionPane.showMessageDialog(Main.f, "<html>Your\'e have not an internet connection.<br/>"
					+ "Please connect for full support and features.</html>", "No Internet Connection", JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (!checkWebsite()) {
			System.out.println("website error");
			JOptionPane.showMessageDialog(Main.f, "<html>ImagEditor web support isn't avaliable now.<br/>"
					+ "Please try again later.</html>", "Web Support Problem", JOptionPane.WARNING_MESSAGE);
			return;
		}
		URL url;
		try {
			url = new URL(webAddress + "lastest-version.txt");
			InputStream is = url.openStream();
			Scanner reader = new Scanner(is);
			double version = reader.nextDouble();
			if (version <= Main.version) {
				reader.close();
				is.close();
				return;
			}
			String s = "";
			if (reader.hasNextLine()) {
				reader.nextLine();
			}
			while(reader.hasNext()) {
				s += reader.nextLine();
				if (reader.hasNext()) {
					s += "\n";
				}
			}
			int answer = JOptionPane.showConfirmDialog(Main.f, "<html>There are a new version."
					+ "<br/>Do you want to take a look?"
					+ "<br/>new features:<ol><li>" + s.replaceAll("\n", "</li><li>") + "</li></ol>"
					+ "</html>");
			if (answer == JOptionPane.YES_OPTION) {
				openInBrowser();
			}
			reader.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public boolean checkInternetConnection(){
		try {
			new URL("https://www.google.com").openStream();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	public boolean checkWebsite(){
	    try {
			new URL(webAddress).openStream();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	public long download(String url, File target) {
		if (!url.startsWith(webAddress)) {
			url = webAddress + url;
		}
		long downloadSize = 0;
		try {
			downloadSize = Files.copy(new URL(url).openStream(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return downloadSize;
	}
	public String getResponse(String url, String params, String method) {
		if (!url.startsWith(webAddress)) {
			url = webAddress + url;
		}
		if (method.toUpperCase().equals("POST")) {
			return getPostResponse(url, params);
		}else {
			return getGetResponse(url + "?" + params);
		}
	}
	private String getGetResponse(String url) {
		System.out.println(url);
		URL adress = null;
		try {
			adress = new URL(URLDecoder.decode(url, "UTF-32").replaceAll(" ", "+")
					.replaceAll("\t", "").replaceAll("\n", ""));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(adress);
		Scanner reader = null;
		try {
			reader = new Scanner(adress.openStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String s = "";
		while(reader.hasNext()) {
			s += reader.nextLine();
			if (reader.hasNext()) {
				s += "\n";
			}
		}
		return s;
	}
	private String getPostResponse(String address, String params) {
		try {
		    URL url = new URL(address);
			// Convert string to byte array, as it should be sent
		    byte[] postDataBytes = params.toString().getBytes("UTF-8");

		    // Connect, easy
		    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		    // Tell server that this is POST and in which format is the data
		    conn.setRequestMethod("POST");
		    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		    conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
		    conn.setDoOutput(true);
		    conn.getOutputStream().write(postDataBytes);

		    // This gets the output from your server
		    Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
		    String response = "";
		    for (int c; (c = in.read()) >= 0;) {
		        response += (char)c;
		    }
		    return response;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public String getErrorPlace(Exception e) {
		String place = "";
		StackTraceElement[] stackTraceElements = e.getStackTrace();
		for (int i = 0; i < stackTraceElements.length; i++) {
			if (stackTraceElements[i].getLineNumber() == -1) {
				place = place + stackTraceElements[i].getClassName() + "(Unknown Source)<br/>";	
			}else {
				place = place + stackTraceElements[i].getClassName() + "(line:" + stackTraceElements[i].getLineNumber() + ")<br/>";	
			}
		}
		return place;
	}
}