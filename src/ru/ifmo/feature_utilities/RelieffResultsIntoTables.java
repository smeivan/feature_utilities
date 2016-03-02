package ru.ifmo.feature_utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;

import ru.ifmo.feature_utilities.importance.LoggerFactory;

public class RelieffResultsIntoTables {
	private static String workingFolder = "";

	public static void main(String[] args) throws IOException, ParseException {
		System.setProperty("line.separator", "\n");
		args = new String[] { "start", "e:\\24.09.09\\feature_utilities\\datasets\\new\\all\\results_relieff\\" };

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
		File fullTable = new File(workingFolder + "\\relieff_table");
		fullTable.createNewFile();
		writeString(fullTable, "dataset relief1 relief3 relief5\n");

		for (File folder : f.listFiles()) {
			if (!folder.isDirectory()) {
				continue;
			}
			
			double[] aucScores = getPrecalculatedAUCScores(folder);
			writeString(fullTable, folder.getName() + " " + String.format("%.3f", aucScores[0]) + " " + String.format("%.3f", aucScores[1]) + " " + String.format("%.3f", aucScores[2]) + "\n");
		}

	}

	private static void writeString(File file, String s) throws IOException {
		FileWriter fileWritter = new FileWriter(file.getAbsolutePath(), true);
		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		bufferWritter.write(s);
		bufferWritter.close();
	}

	private static double[] getPrecalculatedAUCScores(File folder) throws IOException {
		File subFolder = new File(folder.getAbsolutePath() + "//7_AUC_scores_WPCA_SVM//");
		double auc1 = 0;
		double auc3 = 0;
		double auc5 = 0;
		int experiments = 0;
		for (File experiment : subFolder.listFiles()) {
			if (!experiment.isDirectory()) {
				continue;
			}
			experiments++;

			for (File relieffEx : experiment.listFiles()) {
				BufferedReader br = new BufferedReader(new FileReader(relieffEx.getAbsolutePath() + "//rank"));
				if (relieffEx.getName().contains("RELIEFF1")) {
					auc1 += Double.parseDouble(br.readLine());
				} else if (relieffEx.getName().contains("RELIEFF3")) {
					auc3 += Double.parseDouble(br.readLine());
				} else if (relieffEx.getName().contains("RELIEFF5")) {
					auc5 += Double.parseDouble(br.readLine());
				}
				br.close();
			}
		}
		auc1 /= experiments;
		auc3 /= experiments;
		auc5 /= experiments;
		return new double[] { auc1, auc3, auc5 };
	}

}
