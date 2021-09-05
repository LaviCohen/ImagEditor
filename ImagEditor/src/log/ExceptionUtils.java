package log;

import java.util.Calendar;

public class ExceptionUtils {
	private static final String[] CRITICALITY_LEVELS = {"Unknown", "None", "Low", "Middle", "High", "Super-High"};
	@SuppressWarnings("static-access")
	public static String exceptionToString(Exception e, Thread t, int ID) {
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
				+ "<br/>Place: " + realClass + "(" + (line < 0 ? "Unknown Source" : 
					"Method: " + method + ", line: " + line) + ")"
				+ "<br/>Time: " + c.get(c.HOUR_OF_DAY) + ":" + c.get(c.MINUTE) + ":" + c.get(c.SECOND) + " (" + c.get(c.MILLISECOND) + " ms)" 
				+ "<br/>Criticality: " + CRITICALITY_LEVELS[getCriticality(e)]
				+ "<br/>Thread: " + t.getName()
				+ "<br/>ID: " + ID
				+ "</font>"
				+ "<br/>--------------------<br/>";
		return s;
	}
	public static int getCriticality(Exception exception) {
		if (exception.getClass() == NumberFormatException.class) {
			return 1;
		}else if (exception.getClass() == NullPointerException.class) {
			return 3;
		}
		return 0;
	}
}