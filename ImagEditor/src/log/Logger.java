package log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class Logger {
	public static boolean printInConsole = true;
	public static PrintStream console = System.out;
	public static PrintStream err = System.err;
	private static PrintStream logger;
	private static PrintStream errorLogger;
	private static StringBuffer log = new StringBuffer();
	private static StringBuffer errorLog = new StringBuffer();
	
	public static void initializeLogger() {
		logger = new PrintStream(new OutputStream() {
			
			@Override
			public void write(int b) throws IOException {
				if (printInConsole) {
					console.append((char)b);
				}
				log.append((char)b);
			}
		});
		errorLogger = new PrintStream(new OutputStream() {
			
			@Override
			public void write(int b) throws IOException {
				if (printInConsole) {
					err.append((char)b);
				}
				errorLog.append((char)b);
			}
		});
		System.setOut(logger);
		System.setErr(errorLogger);
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
}