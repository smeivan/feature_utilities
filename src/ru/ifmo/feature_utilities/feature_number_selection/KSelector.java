package ru.ifmo.feature_utilities.feature_number_selection;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import ru.ifmo.feature_utilities.importance.LoggerFactory;
import ru.ifmo.utilities.FileUtilities;

public class KSelector {//don't work but may be useful
	
	public int selectNumberOfFeatures(Map<String, Double> features) {
		/*
		double[] normalizedScores = normalizeFeaturesScores(features);
		//System.err.println("Normalize feature scores done.");
		double entropy = calculateEntropy(normalizedScores);
		//System.err.println("Calc entropy scores done. " + entropy);
		double[] CE = calculateCE(normalizedScores, entropy);
		//System.err.println("Calc CE scores done. " + CE[0]);
		double CEAvverage = calculateAvverage(CE);
		//System.err.println("Calc CE avv done." + CEAvverage);
		double CEStandartDeviation = calculateStandartDeviation(CE, CEAvverage);
		//System.err.println("Calc CE dev done. " + CEStandartDeviation);
		int numOfF = calculateNumberOfFeatures(CE, CEAvverage, CEStandartDeviation);
		LoggerFactory.getInstance().println("Num of f = " + numOfF);
		return numOfF;
		*/
		double[] normalizedScores = normalizeFeaturesScores(features);
		double avverage = calculateAvverage(normalizedScores);
		double deviation = calculateStandartDeviation(normalizedScores, avverage);
		int numOfF = calculateNumberOfFeatures(normalizedScores, avverage, deviation);
		//System.err.println("Total features " + numOfF);
		return numOfF;
		
	}
	
	/**
	 * Auto select number of features
	 * 
	 * @param folder
	 * @return
	 * @throws IOException
	 */
	public int selectNumOfFeatures(File folder) throws IOException {
		// int mean = 0;

		int max = Integer.MIN_VALUE;

		// int totalCount = 0;
		for (File file : folder.listFiles()) {
			if (file.getName().contains("sorted")) {
				int numOfF = selectNumberOfFeatures(FileUtilities
						.loadStrToDoubleMap(file));
				// mean += numOfF;
				// totalCount++;
				if (numOfF > max) {
					max = numOfF;
				}
			}
		}
		LoggerFactory.getInstance().println("Max n of f = " + max);
		return max;// mean / totalCount;
	}

	private int calculateNumberOfFeatures(double[] CE, double CEAvverage,
			double CEStandartDeviation) {
		int totalGoodFeatures = 0;
		//int totalNormFeatures = 0;
		double threshold = CEAvverage + CEStandartDeviation;
		//double thresholdNorm = CEAvverage - CEStandartDeviation;

		for (double ce : CE) {
			totalGoodFeatures += ce > threshold ? 1 : 0;
			//totalNormFeatures += ce > thresholdNorm ? 1 : 0;
			//totalNormFeatures -= ce > threshold ? 1 : 0;
		}
		//System.err.println("Norm features num = " + totalNormFeatures);
		return totalGoodFeatures;
	}

	private double calculateStandartDeviation(double[] CE, double CEAvverage) {
		double standartDeviation = 0;
		for (double ce : CE) {
			standartDeviation += (ce - CEAvverage) * (ce - CEAvverage);
		}
		return Math.sqrt(standartDeviation / CE.length);
	}

	private double calculateAvverage(double[] CE) {
		double avverage = 0;
		for (double ce : CE) {
			avverage += ce;
		}
		return avverage / CE.length;
	}

	@SuppressWarnings("unused")
	private double[] calculateCE(double[] normalizedScores, double entropy) {
		double[] CE = new double[normalizedScores.length];
		for (int i = 0; i < CE.length; i++) {
			CE[i] = entropy - recalculateEntropy(normalizedScores, entropy, i);
		}
		return CE;
	}

	private double recalculateEntropy(double[] normalizedScores,
			double entropy, int i) {
		if (!((Double) (normalizedScores[i] * Math.log(normalizedScores[i])))
				.isNaN()) {
			return (entropy * (-Math.log(normalizedScores.length)) - normalizedScores[i]
					* Math.log(normalizedScores[i]))// may by / Math.log(2)
					* (-1 / Math.log(normalizedScores.length - 1));// w/o
																	// normalization?
		} else {
			return (entropy * (-Math.log(normalizedScores.length)) + normalizedScores[i] / Math.log(2))
					* (-1 / Math.log(normalizedScores.length - 1));
		}
	}

	@SuppressWarnings("unused")
	private double calculateEntropy(double[] normalizedScores) {
		double entropy = 0;
		for (double score : normalizedScores) {
			if (!((Double) (score * Math.log(score) / Math.log(2))).isNaN()) {
				entropy += score * Math.log(score) / Math.log(2);
			} else {
				entropy += -score / Math.log(2);
			}
		}

		return entropy * (-1 / Math.log(normalizedScores.length));
	}

	private double[] normalizeFeaturesScores(Map<String, Double> features) {
		double[] normalizedScores = new double[features.size()];
		double sum = 0;
		for (Entry<String, Double> feature : features.entrySet()) {
			sum += Math.abs(feature.getValue());
		}
		//System.err.println("Sum = " + sum);
		int iterator = 0;
		for (Entry<String, Double> feature : features.entrySet()) {
			normalizedScores[iterator] = Math.abs(feature.getValue()) / sum;
			iterator++;
		}

		return normalizedScores;
	}
}
