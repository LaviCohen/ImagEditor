package log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import install.Install;

public class Logger {
	public static boolean printInConsole = true;
	public static boolean saveLogFiles = true;
	public static PrintStream console = System.out;
	public static PrintStream err = System.err;
	private static boolean addTimeStamp = true;
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
				super.println(x + (isAddTimeStamp()?" (timestamp " + System.currentTimeMillis() + ")":""));
			}
		};
		errorLogger = new PrintStream(new OutputStream() {
			
			@Override
			public void write(int b) throws IOException {
				liveLogger.append((char)b);
				errorLog.append((char)b);
				if (printInConsole) {
					err.print((char)b);
				}
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
			Install.getFile("Data\\Logs\\live log.txt").createNewFile();
			liveLogger = new PrintStream(Install.getFile("Data\\Logs\\live log.txt"));
		} catch (FileNotFoundException e) {
			
		} catch (IOException e) {
			e.printStackTrace();
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
	public static void stop() {
		logger = null;
		errorLogger = null;
		liveLogger = null;
		log = null;
		errorLog = null;
		System.setOut(console);
		System.setErr(err);
		console = null;
		err = null;
	}
	public static int getErrorCount() {
		return errorCount;
	}
	public static boolean isAddTimeStamp() {
		return addTimeStamp;
	}
	public static void enableTimeStamp() {
		Logger.addTimeStamp = true;
	}
	public static void disableTimeStamp() {
		Logger.addTimeStamp = false;
	}
	public static PrintStream getErrorLogger() {
		return errorLogger;
	}
}