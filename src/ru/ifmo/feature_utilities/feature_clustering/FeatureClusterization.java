package ru.ifmo.feature_utilities.feature_clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class FeatureClusterization {
	private final int RANDOM_INIT = 666;
	public double RELEVANCE_TRESHOLD = 0.013;// TODO auto select

	private Map<Integer, Integer> bestFeatureInCluster = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> clustersByFeatures = new HashMap<Integer, Integer>();
	private List<List<Integer>> clusters = new ArrayList<List<Integer>>();
	private Random random = new Random(RANDOM_INIT);

	public void clusterization(int[][] features, int[] classes) {
		clusterization(features, classes, null);
	}

	public void clusterization(int[][] features, int[] classes,
			int[] featureRank) {
		clusters.clear();
		bestFeatureInCluster.clear();
		int[] featureOrder = featureRank;
		if (featureOrder == null) {
			featureOrder = new int[features[0].length];
			for (int featureNum = 0; featureNum < features[0].length; featureNum++) {
				featureOrder[featureNum] = featureNum;
			}

		}

		for (int featureNum = 0; featureNum < featureOrder.length; featureNum++) {
			findClusterForFeature(features, classes, featureOrder[featureNum],
					featureRank);/*
			if (featureNum % 100 == 0) {
				System.err.println("Clusters = " + clusters.size()
						+ "; Features pased = " + featureNum);
			}*/
		}
	}

	private void findClusterForFeature(int[][] features, int[] classes,
			int featureNum, int[] featureRank) {
		double[] relevance = calculateRelevanceToAllAdded(features, classes,
				featureNum, featureRank);
		int closeClusters = 0;
		for (int clusterNum = 0; clusterNum < relevance.length; clusterNum++) {
			if (relevance[clusterNum] < RELEVANCE_TRESHOLD) {								
				closeClusters++;
				addToCluster(clusterNum, featureNum);// can add to many
				// classes		
				break;//no
			}
		}
		if (closeClusters == 0) {
			addToCluster(relevance.length, featureNum);
			bestFeatureInCluster.put(relevance.length, featureNum);
		}
	}

	public List<List<Integer>> getClusters() {
		return clusters;
	}
	
	public List<Integer> getBestFeaturesFromClusters(){
		List<Integer> features = new ArrayList<Integer>();
		for (Integer cluster:bestFeatureInCluster.keySet()){
			features.add(bestFeatureInCluster.get(cluster));
		}
		return features;
	}

	private void addToCluster(int clusterNum, int feature) {
		if (clusters.size() <= clusterNum) {
			clusters.add(new ArrayList<Integer>());
		}
		clusters.get(clusterNum).add(feature);
		clustersByFeatures.put(feature, clusterNum);
	}

	private double[] calculateRelevanceToAllAdded(int[][] features,
			int[] classes, int featureNum, int[] featureRank) {
		Relevance rel = new JSDivergenceRelevance();

		double[] relevance = new double[clusters.size()];
		for (int clusterNum = 0; clusterNum < relevance.length; clusterNum++) {
			if (featureRank == null) {
				relevance[clusterNum] = rel.calculateRelevance(features,
						classes, featureNum,
						getRandomFeatureFromCluster(clusterNum)); //TODO think
			} else {
				relevance[clusterNum] = rel.calculateRelevance(features,
						classes, featureNum,
						getBestFeatureFromCluster(clusterNum));
			}
		}
		return relevance;
	}

	private int getRandomFeatureFromCluster(int numOfCluster) {
		return clusters.get(numOfCluster).get(
				random.nextInt(clusters.get(numOfCluster).size()));
	}

	private int getBestFeatureFromCluster(int numOfCluster) {
		return bestFeatureInCluster.get(numOfCluster);
	}
	
	public Map<Integer,Integer> getClustersByFeaturesMap() {
		return clustersByFeatures;
	}

}
