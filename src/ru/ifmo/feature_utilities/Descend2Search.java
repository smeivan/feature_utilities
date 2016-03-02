
package ru.ifmo.feature_utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;

import ru.ifmo.feature_utilities.importance.LoggerFactory;
import ru.ifmo.utilities.BackupUtilities;

public class Descend2Search {
	private static String workingFolder = "";
	private static HashSet<double[]> visitedPoints = new HashSet<double[]>();

	public static void main(String[] args) throws IOException {
		System.setProperty("line.separator", "\n");

		args = new String[] { "start", "e:\\24.09.09\\feature_utilities\\datasets\\new\\all\\test_data3\\" };
		
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
			if (completedData(folder)) {
				continue;
			}
			
			File specialFolder = new File (folder+"/99_resulting_features/");
			specialFolder.mkdirs();
			
			prepareFiles(folder);
			makeDescend(folder, new double[] { 1, 0, 0, 0 });
			writeString(folder, "\n");
			makeDescend(folder, new double[] { 0, 1, 0, 0 });
			writeString(folder, "\n");
			makeDescend(folder, new double[] { 0, 0, 1, 0 });
			writeString(folder, "\n");
			makeDescend(folder, new double[] { 0, 0, 0, 1 });
			writeString(folder, "\n");
			makeDescend(folder, new double[] { 1, 1, 1, 1 });

//			double baseScore = getAUCScore(folder, new double[]{1.300, 0.600, 0.000, 0.000}); //0,798 -> 0,948 = 0.931                            
//			double baseScore2 = getAUCScore(folder, new double[]{-0.300, 1.000, 0.300, 0.300});//0,919 -> 0,970 = 0.951
//			double baseScore3 = getAUCScore(folder, new double[]{0.000, 0.000, 0.700, 0.300});//0,868 -> 0.953 = 0.961
//			double baseScore4 = getAUCScore(folder, new double[]{0.000, 0.300, 0.000, 0.700});//0,873 -> 0.930 = 0.887
//			double baseScore5 = getAUCScore(folder, new double[]{1.300, 1.000, 1.300, 1.300});//0,866 -> 0,973 = 0.941

//			double baseScore = getAUCScore(folder, new double[]{1.300, 0.300, -0.300, 0.300}); //0,798 -> 0,948 = 0.931                            
//			double baseScore2 = getAUCScore(folder, new double[]{0.300, 1.000, 0.300, 0.000});//0,919 -> 0,970 = 0.951
//			double baseScore3 = getAUCScore(folder, new double[]{0.000, 0.300, 0.700, 0.000});//0,868 -> 0.953 = 0.961
//			double baseScore4 = getAUCScore(folder, new double[]{0.600, 0.000, 0.000, 1.300});//0,873 -> 0.930 = 0.887
//			double baseScore5 = getAUCScore(folder, new double[]{1.000, 1.000, 1.000, 1.000});//0,866 -> 0,973 = 0.941
	
			
//			System.err.println(baseScore);
//			System.err.println(baseScore2);
//			System.err.println(baseScore3);
//			System.err.println(baseScore4);
//			System.err.println(baseScore5);
			
			
			visitedPoints.clear();
		}
	}

	private static void makeDescend(File folder, double[] point) throws IOException {
		double baseScore = getAUCScore(folder, point);
		writeResults(folder, baseScore, point);
		visitedPoints.add(point.clone());

		boolean smthChanged = true;

		while (smthChanged) {
			smthChanged = false;
			for (int i = 0; i < point.length; i++) {
				point[i] += 0.3;
				if (!alreadyVisited(point)) {
					double score = getAUCScore(folder, point);
					writeResults(folder, score, point);
					visitedPoints.add(point.clone());
					if (score > baseScore) {
						baseScore = score;
						smthChanged = true;
						break;
					}
				}
				point[i] -= 0.6;
				if (!alreadyVisited(point)) {
					double score = getAUCScore(folder, point);
					writeResults(folder, score, point);
					visitedPoints.add(point.clone());
					if (score > baseScore) {
						baseScore = score;
						smthChanged = true;
						break;
					}
				}
				point[i] += 0.3;
			}
		}

	}

	private static void prepareFiles(File folder) throws IOException {
		BackupUtilities.deleteAllOddFolders(3, folder.getAbsolutePath() + "\\");
		
		File resultsFile = new File(folder.getAbsolutePath() + "//results");
		resultsFile.createNewFile();
		FileWriter fileWritter = new FileWriter(resultsFile.getAbsolutePath(), true);
		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		bufferWritter.write("fc sp su vdm AUC\n");
		bufferWritter.close();
	}

	private static void writeString(File folder, String s) throws IOException {
		File resultsFile = new File(folder.getAbsolutePath() + "//results");
		FileWriter fileWritter = new FileWriter(resultsFile.getAbsolutePath(), true);
		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		bufferWritter.write(s);
		bufferWritter.close();
	}

	private static void writeResults(File folder, double auc, double[] point) throws IOException {
		File resultsFile = new File(folder.getAbsolutePath() + "//results");
		FileWriter fileWritter = new FileWriter(resultsFile.getAbsolutePath(), true);
		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		bufferWritter.write(String.format("%.3f", point[0]) + " " + String.format("%.3f", point[1]) + " " + String.format("%.3f", point[2]) + " " + String.format("%.3f", point[3]) + " " + String.format("%.3f", auc) + "\n");
		bufferWritter.close();
	}

	private static boolean alreadyVisited(double[] point) {
		for (double[] visited : visitedPoints) {
			boolean differs = false;
			for (int i = 0; i < visited.length; i++) {
				if (Math.abs(point[i] - visited[i]) > 0.001) {
					differs = true;
					break;
				}
			}
			if (!differs) {
				return true;
			}
		}
		return false;
	}

	private static double getAUCScore(File folder, double[] point) throws IOException {
		EvaluateOptimisationPoint.main(new String[] { "start", folder.getAbsolutePath(), String.valueOf(point[0]), String.valueOf(point[1]), String.valueOf(point[2]), String.valueOf(point[3]) });
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
		
		BackupUtilities.storePoint(folder.getAbsolutePath(), point, auc);
		BackupUtilities.deleteAllOddFolders(3, folder.getAbsolutePath() + "\\");
		return auc;
	}

	private static boolean completedData(File folder) {
		for (File f : folder.listFiles()) {
			if (f.isDirectory()) {
				continue;
			}
			if (f.getName().equals("ended")) {
				return true;
			}
		}
		return false;
	}

}
