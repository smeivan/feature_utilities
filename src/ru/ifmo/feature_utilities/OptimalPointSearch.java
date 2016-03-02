package ru.ifmo.feature_utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import ru.ifmo.feature_utilities.importance.LoggerFactory;
import ru.ifmo.utilities.BackupUtilities;
import ru.ifmo.utilities.FileUtilities;

public class OptimalPointSearch {
	private static String workingFolder = "";

	private static double[] getNext(double[] current) {
		boolean smthChanged = false;
		for (int i = 0; i < current.length; i++) {
			if (current[i] == 1) {
				continue;
			}
			if (current[i] >= 0.29) {
				current[i] = -0.3;
				continue;
			}
			current[i] += 0.1;
			if (current[i] >= 0.29) {
				for (int j = 0; j < i; j++) {
					if (current[j] == 1) {
						continue;
					}
					current[j]=-0.3;
				}
			}

			smthChanged = true;
			break;
		}

		if (!smthChanged) {
			if (current[current.length - 1] == 1) {
				current[current.length - 1] = -1;
			} else {
				int j = 1;
				for (int i = 0; i < current.length; i++) {
					if (current[i] == 1) {
						j = i + 1;
					}
					if (i == j) {
						current[i] = 1;
					} else {
						current[i] = -0.3;
					}
				}
			}
		}
		return current;
	}

	public static void main(String[] args) throws IOException {
		System.setProperty("line.separator", "\n");

		args = new String[] { "start", "e:\\24.09.09\\feature_utilities\\datasets\\Kovanikov\\all\\test_data\\" };

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
			double[] currentStep = new double[] { 1, -0.3, -0.3, -0.3 };

			while (currentStep[3] != -1) {
				if (!folder.isDirectory()) {
					continue;
				}
				try {
					File resultsFile = new File(folder.getAbsolutePath() + "//results");

					if (!resultsFile.exists()) {
						resultsFile.createNewFile();
						FileWriter fileWritter = new FileWriter(resultsFile.getAbsolutePath(), true);
						BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
						bufferWritter.write("fc sp su vdm AUC\n");
						bufferWritter.close();
					} else {
						int lines = FileUtilities.getNumOfStringsInFile(resultsFile);
						if ((currentStep[0] == -1) && (currentStep[1] <= -0.29) && (currentStep[2] <= -0.29) && (currentStep[3] <= -0.29)) {
							for (int i = 2; i < lines; i++) {
								currentStep = getNext(currentStep);
							}
						} else {
							currentStep = getNext(currentStep);
						}
					}

					EvaluateOptimisationPoint.main(new String[] { "start", folder.getAbsolutePath(), String.valueOf(currentStep[0]), String.valueOf(currentStep[1]), String.valueOf(currentStep[2]), String.valueOf(currentStep[3]) });

					File subFolder = new File(folder.getAbsolutePath() + "//7_AUC_scores_WPCA_SVM//");
					double auc = 0;
					int experiments = 0;
					for (File experiment : subFolder.listFiles()) {
						if (!experiment.isDirectory()) {
							continue;
						}
						experiments++;
						BufferedReader br = new BufferedReader(new FileReader(experiment.listFiles()[0].getAbsolutePath() + "//rank"));
						auc += Double.parseDouble(br.readLine());
						br.close();
					}
					auc /= experiments;

					FileWriter fileWritter = new FileWriter(resultsFile.getAbsolutePath(), true);
					BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
					bufferWritter.write(String.valueOf(currentStep[0]) + " " + String.valueOf(currentStep[1]) + " " + String.valueOf(currentStep[2]) + " " + String.valueOf(currentStep[3]) + " " + auc + "\n");
					bufferWritter.close();

					BackupUtilities.deleteAllOddFolders(3, folder.getAbsolutePath() + "\\");

				} catch (Exception e) {
					LoggerFactory.getInstance().println("\n" + folder + " is incorrect folder");
				}
			}
		}

	}

}
