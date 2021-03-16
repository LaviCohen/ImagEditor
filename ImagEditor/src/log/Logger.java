package log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Calendar;

import install.Install;

public class Logger {
	public static boolean printInConsole = true;
	public static boolean addTimeStamp = true;
	public static boolean saveLogFiles = true;
	public static PrintStream console = System.out;
	public static PrintStream err = System.err;
	private static PrintStream logger;
	private static PrintStream errorLogger;
	private static PrintStream liveLogger;
	private static StringBuffer log = new StringBuffer();
	private static StringBuffer errorLog = new StringBuffer();
	public static int errorCount = 0;
	
	public static void initializeLogger() {
		logger = new PrintStream(new OutputStream() {
			
			@Override
			public void write(int b) throws IOException {
				liveLogger.append((char)b);
				if (printInConsole) {
					console.append((char)b);
				}
				log.append((char)b);
			}
		}) {
			@Override
			public void println(String x) {
				super.println(x + (addTimeStamp?" in (" + System.currentTimeMillis() + ")":""));
			}
		};
		errorLogger = new PrintStream(new OutputStream() {
			
			@Override
			public void write(int b) throws IOException {
				liveLogger.append((char)b);
				errorLog.append((char)b);
			}
		});
		if (Install.isInstalled()) {
			initializeLiveLogger();
		}else {
			liveLogger = new PrintStream(new OutputStream() {
				
				@Override
				public void write(int b) throws IOException {
					// TODO Auto-generated method stub
					
				}
			});
		}
		System.setOut(logger);
		System.setErr(errorLogger);
	}
	public static void initializeLiveLogger() {
		try {
			liveLogger = new PrintStream(Install.getFile("Data\\Logs\\live log.txt"));
		} catch (FileNotFoundException e) {
			
		}
	}
	public static void exportTo(File f) {
		PrintStream ps = null;
		try {
			ps = new PrintStream(f);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ps.println("Error Log:");
		ps.println(getErrorLog());
		ps.println("Standart Log:");
		ps.println(getLog());
	}
	public static String getLog() {
		return log.toString();
	}
	public static String getErrorLog() {
		return errorLog.toString();
	}
	public static void reportInLog(Exception e, Thread t) {
		System.err.println(exceptionToString(e, t, errorCount));
		if (printInConsole) {
			e.printStackTrace();
		}
	}
	@SuppressWarnings("static-access")
	public static String exceptionToString(Exception e, Thread t, int ID) {
		System.out.println("report");
		Calendar c = Calendar.getInstance();
		String realClass = null;
		String method = null;
		int line = -1;
		StackTraceElement[] elements = e.getStackTrace();
		for (int i = 0; i < elements.length; i++) {
			if (elements[i].getLineNumber() > 0) {
				realClass = elements[i].getClassName();
				line = elements[i].getLineNumber();
				method = elements[i].getMethodName();
				break;
			}
		}
		String s = "<font color = red>" + e.toString() 
				+ "<br/>Thread: " + t.getName()
				+ "<br/>Place: " + realClass + "(" + (line < 0 ? "Unknown Source" : 
					"Method: " + method + ", line: " + line) + ")"
				+ "<br/>Time: " + c.get(c.HOUR_OF_DAY) + ":" + c.get(c.MINUTE) + ":" + c.get(c.SECOND) + "." + c.get(c.MILLISECOND)
				+ "<br/>ID: " + ID
				+ "</font>"
				+ "<br/>--------------------<br/>";
		return s;
	}
}