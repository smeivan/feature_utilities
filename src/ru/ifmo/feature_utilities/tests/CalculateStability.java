package ru.ifmo.feature_utilities.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import ru.ifmo.feature_utilities.Starter;
import ru.ifmo.utilities.FileUtilities;

public class CalculateStability {

	public static final String STABILITY_FOLDER = "999_stability";
	public static final String VDM_NAME = "vdm_BGF";
	public static final String SPEARMAN_NAME = "spearman_BGF";
	public static final String FIT_CRITERION_NAME = "fit_criterion_BGF";
	public static final String SU_NAME = "symmetrical_uncertainty_BGF";

	public static void main(String[] args) throws IOException {//TODO Ivan (mb add to Starter and move to runners)
		if (args.length != 1) {
			throw new IOException("Input working folder");
		}

		int numOfExperiments = Integer.parseInt(Starter.NUM_OF_EXPERIMENTS);

		int[][] features = new int[numOfExperiments][];

		for (int i = 0; i < numOfExperiments; i++) {
			String workingFile = args[0] + "/" + Starter.FOURTH_STEP_FOLDER + "/" + (i+1) + "/" + SPEARMAN_NAME;
			int numOfFeatures = FileUtilities.getNumOfStringsInFile(new File(workingFile));
			features[i] = new int[numOfFeatures];
			BufferedReader br = new BufferedReader(new FileReader(workingFile));
			for (int j = 0; j < numOfFeatures; j++) {
				features[i][j] = Integer.parseInt(br.readLine());
			}
			br.close();
			Arrays.sort(features[i]);
		}
		
		System.err.println("Resulting Stability = " + calcStability(features));
		
	}

	private static double calcStability(int[][] features) {
		int numOfExperiments = features.length;
		int k = 0;
		double result = 0;

		for (int i = 0; i < numOfExperiments; i++) {
			for (int j = i + 1; j < numOfExperiments; j++) {
				k++;
				int intersections = calcIntersections(features[i], features[j]);
				double s = 1 - ((double)(features[i].length + features[j].length - 2 * intersections)) / ((double)(features[i].length + features[j].length - intersections));
				System.err.println("i=" + i + " j=" + j + " s=" + s);
				result += s;
			}
		}
		result /= k;
		return result;
	}

	private static int calcIntersections(int[] is, int[] is2) {
		int i=0;
		int j=0;
		int result = 0;
		while ((i<is.length)&&(j<is2.length)){
			if (is[i]<is2[j]){
				i++;
				continue;
			}
			if (is[i]>is2[j]){
				j++;
				continue;
			}
			if (is[i]==is2[j]){
				i++;
				j++;
				result++;
				continue;
			}
		}
		
		return result;
	}
}
