package ru.ifmo.feature_utilities.importance;

import java.util.Arrays;

import ru.ifmo.utilities.TimerUtilities;

public class ChiSquared implements IValuer {
	private Logger localLogger;

	public ChiSquared(String params, String comments) {
		localLogger = LoggerFactory.getInstance().createLogger("ChiSquared", params, comments,true);
	}

	public double[] execute(FeatureData data) {
		localLogger.println("Starting ChiSquared coefficients calculating");
		TimerUtilities timer = new TimerUtilities();
		timer.start();
		
		int[][] features = data.getFeatureMatrix();
		int[] classes = data.getClassVector();
		double[] result = new double[features[0].length];

		int maxVector = calculatePossibleVectorValues(classes);

		int[] A = new int[maxVector];
		int[] B = new int[maxVector];
		int[] C = new int[maxVector];
		int[] D = new int[maxVector];
		double[] chi = new double[maxVector];

		int N = classes.length;

		for (int i = 0; i < features[0].length; i++) {
			Arrays.fill(A, 0);
			Arrays.fill(B, 0);
			Arrays.fill(C, 0);
			Arrays.fill(D, 0);
			Arrays.fill(chi, 0);
			for (int j = 0; j < features.length; j++) {
				if (features[j][i] == 1) {
					A[classes[j]]++;
					for (int k = 0; k < B.length; ++k) {
						B[k]++;
					}
					B[classes[j]]--;
				}
				if (features[j][i] == 0) {
					C[classes[j]]++;
					for (int k = 0; k < B.length; ++k) {
						D[k]++;
					}
					D[classes[j]]--;
				}
			}
			localLogger.printlnDebug(i + "/" + features[0].length);
			for (int k = 0; k < chi.length; k++) {
				chi[k] = N * Math.pow((A[k] * D[k] - C[k] * B[k]), 2) / ((A[k] + C[k]) * (B[k] + D[k]) * (A[k] + B[k]) * (C[k] + D[k]));
				localLogger.printDebug(" " +A[k]+" " + B[k] + " " + C[k] + " " + D[k] + " " + chi[k]);
			}
			localLogger.printlnDebug("");

			for (int k = 0; k < chi.length; k++) {
				if (Math.abs(result[i]) < Math.abs(chi[k])) {
					result[i] = chi[k];
				}
			}
		}
		localLogger.println("ChiSquared coefficients calculating finished");
		localLogger.println("Time spend:" + timer.stop() + " millis");
		localLogger.println("------------------------------------------");
		LoggerFactory.getInstance().deleteLogger(localLogger);
		return result;
	}

	private int calculatePossibleVectorValues(int[] vector) {
		int vectorValues = 0;
		for (int i = 0; i < vector.length; i++) {
			if (vector[i] > vectorValues) {
				vectorValues = vector[i];
			}
		}
		return vectorValues + 1;
	}

}
