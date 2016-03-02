package ru.ifmo.feature_utilities.feature_clustering;

public interface Relevance {
	public double calculateRelevance(int[][] features, int[] classes,
			int firstFeature, int secondFeature);
}
