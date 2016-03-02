package ru.ifmo.feature_utilities.importance;

import ru.ifmo.utilities.TimerUtilities;

public class SpearmanCoefficient implements IValuer {
	Logger localLogger;
	
	public SpearmanCoefficient(String params, String comments) {
		localLogger = LoggerFactory.getInstance().createLogger("SpearmanCoefficient", params, comments, true);
	}
	
	public double[] execute(FeatureData data) {
		localLogger.println("Starting Spearman coefficients calculating");
		TimerUtilities timer = new TimerUtilities();
		timer.start();
		int[][] features = data.getFeatureMatrix();
		int[] classes = data.getClassVector();

		int featuresnNum = features[0].length;
		double[] result = new double[featuresnNum];

		localLogger.printlnDebug("Calculating classes mean");
		double yMean = getVectorMean(classes);
		localLogger.printlnDebug("Classes mean: "+yMean);

		localLogger.printlnDebug("Calculating features weights");
		for (int i = 0; i < featuresnNum; i++) {
			double xMean = getColomnMean(features, i);
			double nominator = 0;
			double denominatorLeft = 0;
			double denominatorRight = 0;
			for (int j = 0; j < classes.length; j++) {
				nominator += (features[j][i] - xMean)*(classes[j] - yMean);
				denominatorLeft+=(features[j][i] - xMean)*(features[j][i] - xMean);
				denominatorRight+=(classes[j] - yMean)*(classes[j] - yMean);
			}
			result[i] = nominator/(Math.sqrt(denominatorLeft*denominatorRight));
			if (Double.isNaN(result[i])){
				result[i] = 0;
			}
			localLogger.printlnDebug(i+"/"+featuresnNum+" - mean: " + xMean + " weight: " + result[i]);
		}
		localLogger.println("Spearman coefficients calculating finished");
		localLogger.println("Time spend:" + timer.stop() + " millis");
		localLogger.println("------------------------------------------");
		LoggerFactory.getInstance().deleteLogger(localLogger);
		return result;
	}

	private double getVectorMean(int[] vector) {
		double result = 0;
		for (int i = 0; i < vector.length; i++) {
			result += vector[i];
		}
		result /= vector.length;
		return result;
	}

	private double getColomnMean(int[][] matrix, int colomnIndex) {
		double result = 0;
		for (int i = 0; i < matrix.length; i++) {
			result += matrix[i][colomnIndex];
		}
		result /= matrix.length;
		return result;
	}
}
