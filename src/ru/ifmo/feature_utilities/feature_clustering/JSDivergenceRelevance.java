package ru.ifmo.feature_utilities.feature_clustering;

import ru.ifmo.feature_utilities.system.Constants;
import ru.ifmo.utilities.MatrixUtilities;

public class JSDivergenceRelevance implements Relevance{
	
	public double calculateRelevance(int[][] features, int[] classes,
			int firstFeature, int secondFeature) {
		double[][] probabilityDistributions = MatrixUtilities.getProbabilityDistributions(
				features, firstFeature, secondFeature);
		return JSDivergence(probabilityDistributions[0],
				probabilityDistributions[1]);
	}

	private double JSDivergence(double[] firstDistr, double[] secondDistr) {
		double[] avvDistr = new double[firstDistr.length];
		for (int i = 0; i < avvDistr.length; i++) {
			avvDistr[i] = (firstDistr[i] + secondDistr[i]) / 2;
		}
		return (klDivergence(firstDistr, avvDistr) + klDivergence(secondDistr,
				avvDistr)) / 2;
	}

	private double klDivergence(double[] firstDistr, double[] secondDistr) {
		double divergence = 0;
		for (int i = 0; i < firstDistr.length; i++) {
			if (!Double.isNaN(firstDistr[i] / secondDistr[i])
					&& firstDistr[i] / secondDistr[i] > Constants.EPS) {
				divergence += Math.log(firstDistr[i] / secondDistr[i])
						* firstDistr[i];
			}
		}
		return divergence / Math.log(2);
	}
}
