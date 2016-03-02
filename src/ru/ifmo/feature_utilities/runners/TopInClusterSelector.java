package ru.ifmo.feature_utilities.runners;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import ru.ifmo.feature_utilities.feature_clustering.FeatureClusterization;
import ru.ifmo.feature_utilities.importance.FeatureData;
import ru.ifmo.feature_utilities.importance.LoggerFactory;
import ru.ifmo.utilities.BackupUtilities;

public class TopInClusterSelector {//may be don't need

	public static void start(String[] args) throws IOException {
		LoggerFactory.getInstance().println("Creating top clusters elements list");
		String workingFolder = args[0];
		if (BackupUtilities.detectLastSucsessfulStep(workingFolder) < 3) {
			throw new IOException("Error, last sucsessfull step must be 3+");
		}

		File f = new File(workingFolder + "/2_matrixes_with_data/");
				

		for (File subFolder : f.listFiles()) {
			if (!subFolder.isDirectory()) {
				continue;
			}
			LoggerFactory.getInstance().println("Working on folder "+subFolder.getName());
			FeatureData fd = new FeatureData(new File(subFolder.getAbsolutePath() + "/_training_features"), new File(subFolder.getAbsolutePath() + "/_training_classes"));
			FeatureClusterization fc = new FeatureClusterization();
			fc.clusterization(fd.getFeatureMatrix(), fd.getClassVector());
			
			int clustersAmount = fc.getClusters().size();
			
			System.out.println(fc.getClustersByFeaturesMap().size() + " features in " + fc.getClusters().size() + " clusters");

			File sortedFolder = new File(workingFolder + "/3_evaluated_features/" + subFolder.getName() + "/");
			for (File sortedFile : sortedFolder.listFiles()) {
				if (!sortedFile.getName().contains("sorted_")) {
					continue;
				}
				int[] features = new int[clustersAmount];
				boolean[] clusterUsed = new boolean[clustersAmount];
				double[] score = new double[clustersAmount];
				BufferedReader br = new BufferedReader(new FileReader(sortedFile));
				while (br.ready()) {
					StringTokenizer st = new StringTokenizer(br.readLine());
					int featureIndex = Integer.parseInt(st.nextToken());
					int clusterIndex = fc.getClustersByFeaturesMap().get(featureIndex);
					if (!clusterUsed[clusterIndex]) {
						clusterUsed[clusterIndex] = true;
						features[clusterIndex] = featureIndex;
						score[clusterIndex] = Double.parseDouble(st.nextToken());
					}
				}
				br.close();
				PrintWriter pw = new PrintWriter(sortedFile.getAbsolutePath().replace("sorted_", "clustered_"));
				pw.println(clustersAmount);
				for (int i = 0; i < clustersAmount; i++) {
					pw.println(features[i] + " " + score[i]);
				}
				pw.close();
			}
		}
	}
}
