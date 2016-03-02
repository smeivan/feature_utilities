package ru.ifmo.feature_utilities.importance;

import java.util.HashMap;
import java.util.Map;

import ru.ifmo.utilities.TimerUtilities;

public class SymmetricUncertainty implements IValuer {

	private Logger localLogger;

	public SymmetricUncertainty(String params, String comments) {
		localLogger = LoggerFactory.getInstance().createLogger(
				"SymmetricUncertainty", params, comments, true);
	}

	@Override
	public double[] execute(FeatureData data) {
		localLogger.println("------------------------------------------");
		localLogger.println("Starting Symmetric Uncertainty calculating");
		TimerUtilities timer = new TimerUtilities();
		timer.start();
		double[] relevance = new double[data.getFeatureMatrix()[0].length];
		Map<Integer, Double> pY = getClassProb(data.getClassVector());
		double HY = calculateEntropy(pY);

		for (int i = 0; i < relevance.length; i++) {
			Map<Integer, Double> pX = getFeatureProb(data.getFeatureMatrix(), i);
			double HX = calculateEntropy(pX);
			
			Map<Integer, Map<Integer, Double>> pXlY = getFeatureConditionalProb(
					data.getFeatureMatrix(), data.getClassVector(), i);
			double HXlY = calculateConditionalEntropy(pY, pXlY);
			
			relevance[i] = calculateSU(HX, HY, HXlY);
		}
		localLogger.println("Symmetric Uncertainty calculating finished");
		localLogger.println("Time spend:" + timer.stop() + "millis");
		localLogger.println("------------------------------------------");
		LoggerFactory.getInstance().deleteLogger(localLogger);
		return relevance;
	}

	private double calculateSU(double HX, double HY, double HXlY) {
		return 2 * (HX - HXlY) / (HX + HY);
	}

	private double calculateEntropy(Map<Integer, Double> pX) {
		double result = 0;
		for (Integer x : pX.keySet()) {
			result -= pX.get(x) * Math.log(pX.get(x)) / Math.log(2);
		}
		return result;
	}

	private double calculateConditionalEntropy(Map<Integer, Double> pY,
			 Map<Integer, Map<Integer, Double>> pXlY) {
		double result = 0;

		for (Integer x : pXlY.keySet()) {
			Map<Integer, Double> ySet = pXlY.get(x);			
			for (Integer y : ySet.keySet()) {
				result += pXlY.get(x).get(y)
						* Math.log(pY.get(y)/pXlY.get(x).get(y))
						/ Math.log(2); // TODO check with int 128++
			}
		}
		return result;
	}

	private Map<Integer, Double> getFeatureProb(int[][] matrix, int index) {// freq
																			// by
																			// value
		Map<Integer, Double> prob = new HashMap<Integer, Double>();
		for (int i = 0; i < matrix.length; i++) {
			if (!prob.containsKey(matrix[i][index])) {
				prob.put(matrix[i][index], 0d);
			}
			prob.put(matrix[i][index], prob.get(matrix[i][index]) + 1);
		}
		int total = matrix.length;
		for (Integer key : prob.keySet()) {
			prob.put(key, prob.get(key) / total);
		}
		return prob;
	}

	private Map<Integer, Map<Integer, Double>> getFeatureConditionalProb(
			int[][] matrix, int[] vector, int index) {// freq by value x by
														// value y
		Map<Integer, Map<Integer, Double>> prob = new HashMap<Integer, Map<Integer, Double>>();
		for (int i = 0; i < matrix.length; i++) {
			if (!prob.containsKey(matrix[i][index])) {
				prob.put(matrix[i][index], new HashMap<Integer, Double>());
			}
			Map<Integer, Double> curXMap = prob.get(matrix[i][index]);
			if (!curXMap.containsKey(vector[i])) {
				curXMap.put(vector[i], 0d);
			}
			curXMap.put(vector[i], curXMap.get(vector[i]) + 1);
		}
		int total = vector.length;
		for (Integer key : prob.keySet()) {
			Map<Integer, Double> curXMap = prob.get(key);
			for (Integer keyY : curXMap.keySet()) {
				curXMap.put(keyY, curXMap.get(keyY) / total);
			}
		}
		return prob;
	}

	private Map<Integer, Double> getClassProb(int[] vector) { // bad code
		Map<Integer, Double> prob = new HashMap<Integer, Double>();
		for (int i = 0; i < vector.length; i++) {
			if (!prob.containsKey(vector[i])) {
				prob.put(vector[i], 0d);
			}
			prob.put(vector[i], prob.get(vector[i]) + 1);
		}
		int total = vector.length;
		for (Integer key : prob.keySet()) {
			prob.put(key, prob.get(key) / total);
		}
		return prob;
	}

}