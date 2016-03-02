package ru.ifmo.feature_utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.StringTokenizer;

import ru.ifmo.feature_utilities.importance.LoggerFactory;
import ru.ifmo.utilities.FileUtilities;

public class OptimizationResultsIntoTables {
	private static String workingFolder = "";

	public static void main(String[] args) throws IOException, ParseException {
		System.setProperty("line.separator", "\n");
		NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);

		args = new String[] { "start", "e:\\24.09.09\\feature_utilities\\datasets\\new\\all\\results1\\" };

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
		File fullTable = new File(workingFolder + "\\final_table");
		fullTable.createNewFile();
		writeString(fullTable, "dataset fc sp su vdm equal top_opt steps\n");

		for (File folder : f.listFiles()) {
			if (!folder.isDirectory()){
				continue;
			}
			File results = new File(folder.getAbsolutePath() + "\\results");
			BufferedReader br = new BufferedReader(new FileReader(results));
			String outString = folder.getName();
			br.readLine();
			StringTokenizer st = new StringTokenizer(br.readLine());

			String auc = getMethodsAUC(st);
			double bestAuc = format.parse(auc).doubleValue();
			outString += " " + auc;

			while (true) {
				if (!br.ready()) {
					break;
				}
				st = new StringTokenizer(br.readLine());
				if (!st.hasMoreTokens()) {
					break;
				}
				auc = getMethodsAUC(st);
				double currentAuc = format.parse(auc).doubleValue();
				if (currentAuc > bestAuc) {
					bestAuc = currentAuc;
				}
			}

			st = new StringTokenizer(br.readLine());
			auc = getMethodsAUC(st);
			outString += " " + auc;
			double currentAuc = format.parse(auc).doubleValue();
			if (currentAuc > bestAuc) {
				bestAuc = currentAuc;
			}

			while (true) {
				if (!br.ready()) {
					break;
				}
				st = new StringTokenizer(br.readLine());
				if (!st.hasMoreTokens()) {
					break;
				}
				auc = getMethodsAUC(st);
				currentAuc = format.parse(auc).doubleValue();
				if (currentAuc > bestAuc) {
					bestAuc = currentAuc;
				}
			}

			st = new StringTokenizer(br.readLine());
			auc = getMethodsAUC(st);
			outString += " " + auc;
			currentAuc = format.parse(auc).doubleValue();
			if (currentAuc > bestAuc) {
				bestAuc = currentAuc;
			}

			while (true) {
				if (!br.ready()) {
					break;
				}
				st = new StringTokenizer(br.readLine());
				if (!st.hasMoreTokens()) {
					break;
				}
				auc = getMethodsAUC(st);
				currentAuc = format.parse(auc).doubleValue();
				if (currentAuc > bestAuc) {
					bestAuc = currentAuc;
				}
			}

			st = new StringTokenizer(br.readLine());
			auc = getMethodsAUC(st);
			outString += " " + auc;
			currentAuc = format.parse(auc).doubleValue();
			if (currentAuc > bestAuc) {
				bestAuc = currentAuc;
			}

			while (true) {
				if (!br.ready()) {
					break;
				}
				st = new StringTokenizer(br.readLine());
				if (!st.hasMoreTokens()) {
					break;
				}
				auc = getMethodsAUC(st);
				currentAuc = format.parse(auc).doubleValue();
				if (currentAuc > bestAuc) {
					bestAuc = currentAuc;
				}
			}

			st = new StringTokenizer(br.readLine());
			auc = getMethodsAUC(st);
			outString += " " + auc;
			currentAuc = format.parse(auc).doubleValue();
			if (currentAuc > bestAuc) {
				bestAuc = currentAuc;
			}

			while (true) {
				if (!br.ready()) {
					break;
				}
				st = new StringTokenizer(br.readLine());
				if (!st.hasMoreTokens()) {
					break;
				}
				auc = getMethodsAUC(st);
				currentAuc = format.parse(auc).doubleValue();
				if (currentAuc > bestAuc) {
					bestAuc = currentAuc;
				}
			}
			br.close();

			outString += " " + String.format("%.3f", bestAuc) + " " + (FileUtilities.getNumOfStringsInFile(results) - 6)+"\n";

			writeString(fullTable, outString);
		}

	}

	private static String getMethodsAUC(StringTokenizer st) {
		st.nextToken();
		st.nextToken();
		st.nextToken();
		st.nextToken();
		return st.nextToken();
	}

	private static void writeString(File file, String s) throws IOException {
		FileWriter fileWritter = new FileWriter(file.getAbsolutePath(), true);
		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		bufferWritter.write(s);
		bufferWritter.close();
	}

}
