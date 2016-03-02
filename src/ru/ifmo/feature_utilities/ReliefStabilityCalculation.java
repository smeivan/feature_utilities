package ru.ifmo.feature_utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import ru.ifmo.feature_utilities.Starter;
import ru.ifmo.utilities.FileUtilities;

public class ReliefStabilityCalculation {
	public static void main(String[] args) throws IOException {
		args = new String[] { "e:\\24.09.09\\feature_utilities\\datasets\\new\\all\\results_relieff\\" };

		if (args.length != 1) {
			throw new IOException("Input working folder");
		}

		File workingFolder = new File(args[0]);

		File outFile = new File(workingFolder + "\\relieff_stability");
		outFile.createNewFile();
		FileUtilities.writeString(outFile, "dataset relief1 relief3 relief5\n");

		for (File f : workingFolder.listFiles()) {
			if (!f.isDirectory()) {
				continue;
			}

			FileUtilities.writeString(outFile, f.getName() + " " + String.format("%.3f", calcDatasetStability(f.getAbsolutePath(), 1)) + " " + String.format("%.3f", calcDatasetStability(f.getAbsolutePath(), 3)) + " " + String.format("%.3f", calcDatasetStability(f.getAbsolutePath(), 5)) + "\n");

		}

	}

	private static double calcDatasetStability(String datasetFolder, int reliefType) throws IOException {
		int numOfExperiments = 3;
		String searchString = "RELIEFF" + reliefType;

		int[][] features = new int[numOfExperiments][];

		for (int i = 0; i < numOfExperiments; i++) {
			File workingFolder = new File(datasetFolder + "/" + Starter.FOURTH_STEP_FOLDER + "/" + (i + 1) + "\\");

			for (File workingFile : workingFolder.listFiles()) {

				if (!workingFile.getName().contains(searchString)) {
					continue;
				}
				int numOfFeatures = FileUtilities.getNumOfStringsInFile(workingFile);
				features[i] = new int[numOfFeatures];
				BufferedReader br = new BufferedReader(new FileReader(workingFile));
				for (int j = 0; j < numOfFeatures; j++) {
					features[i][j] = Integer.parseInt(br.readLine());
				}
				br.close();
				Arrays.sort(features[i]);
			}
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
