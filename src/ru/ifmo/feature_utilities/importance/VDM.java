package ru.ifmo.feature_utilities.importance;

import java.util.Arrays;

import ru.ifmo.utilities.TimerUtilities;

public class VDM implements IValuer {
	private Logger localLogger;
	private int[] classTypes;
	private boolean[] isReserved;

	public VDM(String params, String comments) {
		localLogger = LoggerFactory.getInstance().createLogger("VDM", params, comments, true);
	}

	public double[] execute(FeatureData data) {
		localLogger.println("Starting VDM coefficients calculating");
		TimerUtilities timer = new TimerUtilities();
		timer.start();
		int[][] features = data.getFeatureMatrix();
		int[] classes = data.getClassVector();
		double[] result = new double[features[0].length];
		isReserved = new boolean[classes.length];

		if (haveMoreThanTwoClasses(classes)) {
			localLogger.println("VDM works only with two resulting classes!!!");
			return result;
			// throw new
			// IOException("VDM works only with two resulting classes!!!");
		}

		localLogger.printlnDebug("Calculating features weights");
		for (int i = 0; i < features[0].length; i++) {
			Arrays.fill(isReserved, false);
			double resultingSum = 0;
			for (int j = 0; j != isReserved.length; ++j) {
				if (!isReserved[j]) {
					resultingSum += calculateSubIntegralElement(features, classes, j, i);
				}
			}
			result[i] = (double) resultingSum / 2;
			localLogger.printlnDebug(i + "/" + features[0].length + " weight: " + result[i]);
		}
		localLogger.println("VDM coefficients calculating finished");
		localLogger.println("Time spend:" + timer.stop() + "millis");
		localLogger.println("------------------------------------------");
		LoggerFactory.getInstance().deleteLogger(localLogger);
		return result;
	}

	private double calculateSubIntegralElement(int[][] features, int[] classes, int featureSetIndex, int featureIndex) {
		int featureValue = features[featureSetIndex][featureIndex];
		int c1Total = 0;
		int c2Total = 0;
		for (int i = featureSetIndex; i < isReserved.length; ++i) {
			if (!isReserved[i]) {
				if (features[i][featureIndex] == featureValue) {
					if (classes[i] == classTypes[0]) {
						c1Total++;
					} else {
						c2Total++;
					}
				}
				isReserved[i] = true;
			}
		}

		double percent1 = (double) c1Total / (c1Total + c2Total);
		double percent2 = (double) c2Total / (c1Total + c2Total);
		return Math.abs(percent1 - percent2);
	}

	private boolean haveMoreThanTwoClasses(int[] vector) {
		classTypes = new int[2];
		boolean[] isDefined = new boolean[2];
		for (int i = 0; i < vector.length; ++i) {
			if (!isDefined[0]) {
				classTypes[0] = vector[i];
				isDefined[0] = true;
				continue;
			}
			if (classTypes[0] == vector[0]) {
				continue;
			}
			if (!isDefined[1]) {
				classTypes[1] = vector[i];
				isDefined[1] = true;
				continue;
			}
			if (classTypes[1] == vector[1]) {
				continue;
			}
			return true;
		}

		return false;
	}
}
