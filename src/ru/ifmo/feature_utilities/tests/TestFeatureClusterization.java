package ru.ifmo.feature_utilities.tests;

import java.io.File;
import java.io.IOException;

import ru.ifmo.feature_utilities.feature_clustering.FeatureClusterization;
import ru.ifmo.feature_utilities.importance.FeatureData;

public class TestFeatureClusterization {//may be don't need

	public static void main(String[] args) throws IOException {
		FeatureData fd = new FeatureData(
				new File("data/test_training_features_2"), new File(
						"data/test_training_classes_2"));
/*
		List<Integer> featureRank = FileUtilities.loadIntList(new File(
				"data/test_sorted_features"));// [0] = bestFNumber
		int[] featureRankArray = new int[featureRank.size() / 20];
		for (int i = 0; i < featureRankArray.length; i++) {
			featureRankArray[i] = featureRank.get(i);
		}
*/
		FeatureClusterization fc = new FeatureClusterization();

		// for (int i = 10; i < 61; i++) {
		// fc.RELEVANCE_TRESHOLD = i*0.01;
		fc.clusterization(fd.getFeatureMatrix(), fd.getClassVector(),
				null/*featureRankArray*/);
		System.err.println("***********************");
		System.err.println("RELEVANCE_TRESHOLD = " + fc.RELEVANCE_TRESHOLD);
		//System.err.println("Total features = " + featureRankArray.length);
		System.err.println("Number of classes " + fc.getClusters().size());
		System.err.println("***********************");
		// }
/*
		for (int i = 0; i < fc.getClusters().size(); i++) {
			System.err.println(i + " cluster size = "
					+ fc.getClusters().get(i).size());
		}
*/
	}
}
