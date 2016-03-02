package ru.ifmo.feature_utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Locale;
import java.util.StringTokenizer;

import ru.ifmo.feature_utilities.Starter;
import ru.ifmo.utilities.BackupUtilities;
import ru.ifmo.utilities.FileUtilities;

public class OptimizationStabilityCalculation {
	public static void main(String[] args) throws IOException, ParseException {
		args = new String[] { "e:\\24.09.09\\feature_utilities\\datasets\\new\\all\\results1\\" };
		NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
		
		if (args.length != 1) {
			throw new IOException("Input working folder");
		}

		File workingFolder = new File(args[0]);
		File outFile = new File(workingFolder + "\\descend_stability");
		outFile.createNewFile();

		FileUtilities.writeString(outFile, "dataset fc sp su vdm equal top_opt\n");

		for (File dataset : workingFolder.listFiles()) {
			if (!dataset.isDirectory()) {
				continue;
			}

			String outString = dataset.getName();

			EvaluateOptimisationPoint.main(new String[] { "start", dataset.getAbsolutePath(), String.valueOf(1), String.valueOf(0), String.valueOf(0), String.valueOf(0) });
			outString += " " + String.format("%.3f", calcDatasetStability(dataset.getAbsolutePath()));
			BackupUtilities.deleteAllOddFolders(3, dataset.getAbsolutePath() + "\\");

			EvaluateOptimisationPoint.main(new String[] { "start", dataset.getAbsolutePath(), String.valueOf(0), String.valueOf(1), String.valueOf(0), String.valueOf(0) });
			outString += " " + String.format("%.3f", calcDatasetStability(dataset.getAbsolutePath()));
			BackupUtilities.deleteAllOddFolders(3, dataset.getAbsolutePath() + "\\");

			EvaluateOptimisationPoint.main(new String[] { "start", dataset.getAbsolutePath(), String.valueOf(0), String.valueOf(0), String.valueOf(1), String.valueOf(0) });
			outString += " " + String.format("%.3f", calcDatasetStability(dataset.getAbsolutePath()));
			BackupUtilities.deleteAllOddFolders(3, dataset.getAbsolutePath() + "\\");

			EvaluateOptimisationPoint.main(new String[] { "start", dataset.getAbsolutePath(), String.valueOf(0), String.valueOf(0), String.valueOf(0), String.valueOf(1) });
			outString += " " + String.format("%.3f", calcDatasetStability(dataset.getAbsolutePath()));
			BackupUtilities.deleteAllOddFolders(3, dataset.getAbsolutePath() + "\\");

			EvaluateOptimisationPoint.main(new String[] { "start", dataset.getAbsolutePath(), String.valueOf(1), String.valueOf(1), String.valueOf(1), String.valueOf(1) });
			outString += " " + String.format("%.3f", calcDatasetStability(dataset.getAbsolutePath()));
			BackupUtilities.deleteAllOddFolders(3, dataset.getAbsolutePath() + "\\");

			BufferedReader br = new BufferedReader(new FileReader(dataset.getAbsolutePath() + "\\results"));
			double[] topPoint = new double[4];
			double topAUC = 0;
			StringTokenizer st = new StringTokenizer(br.readLine());
			while (br.ready()){
				st = new StringTokenizer(br.readLine());
				if (!st.hasMoreTokens()){
					continue;
				}
				
				double[] currentPoint = new double[4];
				currentPoint[0] = format.parse(st.nextToken()).doubleValue();
				currentPoint[1] = format.parse(st.nextToken()).doubleValue();
				currentPoint[2] = format.parse(st.nextToken()).doubleValue();
				currentPoint[3] = format.parse(st.nextToken()).doubleValue();
				double currentAUC = format.parse(st.nextToken()).doubleValue();
				if (currentAUC>topAUC){
					topAUC=currentAUC;
					topPoint = currentPoint;
				}
			}
			
			EvaluateOptimisationPoint.main(new String[] { "start", dataset.getAbsolutePath(), String.valueOf(topPoint[0]), String.valueOf(topPoint[1]), String.valueOf(topPoint[2]), String.valueOf(topPoint[3]) });
			outString += " " + String.format("%.3f", calcDatasetStability(dataset.getAbsolutePath())) + "\n";
			BackupUtilities.deleteAllOddFolders(3, dataset.getAbsolutePath() + "\\");
			
			br.close();
			FileUtilities.writeString(outFile, outString);
		}

	}

	private static double calcDatasetStability(String datasetFolder) throws IOException {
		int numOfExperiments = 3;

		int[][] features = new int[numOfExperiments][];

		for (int i = 0; i < numOfExperiments; i++) {
			File workingFolder = new File(datasetFolder + "/" + Starter.FOURTH_STEP_FOLDER + "/" + (i + 1) + "\\");
			File workingFile = workingFolder.listFiles()[0];
			int numOfFeatures = FileUtilities.getNumOfStringsInFile(workingFile);
			features[i] = new int[numOfFeatures];
			BufferedReader br = new BufferedReader(new FileReader(workingFile));
			for (int j = 0; j < numOfFeatures; j++) {
				features[i][j] = Integer.parseInt(br.readLine());
			}
			br.close();
			Arrays.sort(features[i]);
		}

		return calcStability(features);
	}

	private static double calcStability(int[][] features) {
		int numOfExperiments = features.length;
		int k = 0;
		double result = 0;

		for (int i = 0; i < numOfExperiments; i++) {
			for (int j = i + 1; j < numOfExperiments; j++) {
				k++;
				int intersections = calcIntersections(features[i], features[j]);
				double s = 1 - ((double) (features[i].length + features[j].length - 2 * intersections)) / ((double) (features[i].length + features[j].length - intersections));
				System.err.println("i=" + i + " j=" + j + " s=" + s);
				result += s;
			}
		}
		result /= k;
		return result;
	}

	private static int calcIntersections(int[] is, int[] is2) {
		int i = 0;
		int j = 0;
		int result = 0;
		while ((i < is.length) && (j < is2.length)) {
			if (is[i] < is2[j]) {
				i++;
				continue;
			}
			if (is[i] > is2[j]) {
				j++;
				continue;
			}
			if (is[i] == is2[j]) {
				i++;
				j++;
				result++;
				continue;
			}
		}

		return result;
	}
}
