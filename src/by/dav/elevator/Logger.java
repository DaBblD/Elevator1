package by.dav.elevator;

public class Logger {
	public static void addLog(String log) {
		Main.getTextArea().append(log + "\n");
		System.out.println(log);
	}
}
