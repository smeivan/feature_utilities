package ru.ifmo.feature_utilities;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import ru.ifmo.feature_utilities.importance.LoggerFactory;

public class GlobalStarter {
	private static String workingFolder = "";

	public static void main(String[] args) {
		System.setProperty("line.separator", "\n");

		try {
			String[] newArgs = new String[args.length - 1];
			for (int i = 1; i < args.length; i++) {
				newArgs[i - 1] = args[i];
			}

			if (args[0].equals("start")) {
				if (args.length == 2) {
					if (args[1].charAt(args[1].length() - 1) == ('/')) {
						workingFolder = args[1];
					} else {
						workingFolder = args[1] + "/";
					}
				} else {
					LoggerFactory.getInstance().println("Wrong params");
				}
			}

		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			LoggerFactory.getInstance().println(sw.toString());
		}

		File f = new File(workingFolder);

		for (File folder : f.listFiles()) {
			if (!folder.isDirectory()) {
				continue;
			}

			try {
				Starter s = new Starter();
				s.main(new String[] { "start", folder.getAbsolutePath() });
			} catch (Exception e) {
				LoggerFactory.getInstance().println("\n" + folder + " is incorrect folder");
			}
		}
	}
}
