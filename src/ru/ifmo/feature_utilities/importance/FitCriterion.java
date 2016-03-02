package ru.ifmo.feature_utilities.importance;

import java.util.Arrays;

import ru.ifmo.utilities.TimerUtilities;

public class FitCriterion implements IValuer {
	private Logger localLogger;
	private double[][] featuresMeans;
	private double[][] featuresVariances;

	public FitCriterion(String params, String comments) {
		localLogger = LoggerFactory.getInstance().createLogger("FitCriterion", params, comments, true);
	}

	public double[] execute(FeatureData data) {
		localLogger.println("Starting FitCriterion coefficients calculating");
		TimerUtilities timer = new TimerUtilities();
		timer.start();
		
		int[][] features = data.getFeatureMatrix();
		int[] classes = data.getClassVector();

		int vectorValues = calculatePossibleVectorValues(classes);

		featuresMeans = new double[features[0].length][vectorValues];
		featuresVariances = new double[features[0].length][vectorValues];
		double[] result = new double[features[0].length];

		calculateFeaturesMeans(features, classes);
		calculateFeaturesVariances(features, classes);

		for (int i = 0; i < features[0].length; i++) {
			int count = 0;
			for (int j = 0; j < features.length; j++) {
				if (FCP(features[j][i],i)==classes[j]){
					count++;
				}
			}
			result[i] = (double)count/features.length;
			localLogger.printlnDebug(i+"/"+features[0].length+" weight: " + result[i]);
		}

		localLogger.println("FitCriterion coefficients calculating finished");
		localLogger.println("Time spend:" + timer.stop() + "millis");
		localLogger.println("------------------------------------------");
		LoggerFactory.getInstance().deleteLogger(localLogger);
		return result;
	}

	private int FCP(int value, int featureIndex) {
		int minIndex = 0;
		double min = Double.POSITIVE_INFINITY;
		for (int i = 0; i < featuresMeans[0].length; i++) {
			double newValue = (double) (Math.abs(value - featuresMeans[featureIndex][i]) / featuresVariances[featureIndex][i]);
			if (newValue < min) {
				minIndex = i;
				min = newValue;
			}
		}
		return minIndex;
	}

	private void calculateFeaturesVariances(int[][] features, int[] classes) {
		int[] counters = new int[featuresMeans[0].length];
		for (int i = 0; i < features[0].length; i++) {
			Arrays.fill(counters, 0);
			for (int j = 0; j < features.length; j++) {
				featuresVariances[i][classes[j]] += Math.pow(features[j][i] - featuresMeans[i][classes[j]], 2);
				counters[classes[j]]++;
			}
			for (int j = 0; j < featuresMeans[0].length; j++) {
				featuresVariances[i][j] = (double) featuresVariances[i][j] / counters[j];
			}
		}
	}

	private void calculateFeaturesMeans(int[][] features, int[] classes) {
		int[] counters = new int[featuresMeans[0].length];
		for (int i = 0; i < features[0].length; i++) {
			Arrays.fill(counters, 0);
			for (int j = 0; j < features.length; j++) {
				featuresMeans[i][classes[j]] += features[j][i];
				counters[classes[j]]++;
			}
			for (int j = 0; j < featuresMeans[0].length; j++) {
				featuresMeans[i][j] = (double) featuresMeans[i][j] / counters[j];
			}
		}
	}

	private int calculatePossibleVectorValues(int[] vector) {
		int vectorValues = 0;
		for (int i = 0; i < vector.length; i++) {
			if (vector[i] > vectorValues) {
				vectorValues = vector[i];
			}
		}
		return vectorValues+1;
	}
}