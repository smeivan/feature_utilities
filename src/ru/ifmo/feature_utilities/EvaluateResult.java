package ru.ifmo.feature_utilities;	

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class EvaluateResult {
	public static void main(String[] args) throws IOException {
		args = new String[] { "e:\\24.09.09\\feature_utilities\\datasets\\Kovanikov\\our_format\\",  "e:\\24.09.09\\feature_utilities\\datasets\\Kovanikov\\results"};

		PrintWriter pw = new PrintWriter (args[1]);
		
		File workingFolder = new File(args[0]);

		for (File dataset : workingFolder.listFiles()) {
			if (!dataset.isDirectory()) {
				continue;
			}
			for (File algorithm : dataset.listFiles()) {
				if (!algorithm.isDirectory()) {
					continue;
				}
				if (algorithm.getName().charAt(0) != '7') {
					continue;
				}
				double min = Double.MAX_VALUE;
				double max = Double.MIN_VALUE;
				double average = 0;

				for (File experiment : algorithm.listFiles()) {
					if (!experiment.isDirectory()) {
						continue;
					}
					File subFolder = experiment.listFiles()[0];
					BufferedReader br = new BufferedReader(new FileReader(subFolder.getAbsolutePath() + "\\rank"));
					double rank = Double.parseDouble(br.readLine());
					br.close();
					if (rank < min) {
						min = rank;
					}
					if (rank > max) {
						max = rank;
					}
					average += rank;
				}
				average /= (algorithm.listFiles().length - 1);

				System.err.println("For dataset " + dataset.getName() + " and algorithm " + algorithm.getName().replace("7_AUC_scores_", "") + " min=" + min + " max=" + max + " av=" + average);
				pw.println(dataset.getName() + " " + algorithm.getName().replace("7_AUC_scores_", "") + " " + min + " " + max + " " + average);
			}
		}
		pw.close();
	}

}
