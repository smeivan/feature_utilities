package ru.ifmo.feature_utilities.importance;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import ru.ifmo.utilities.FileUtilities;

public class FeaturePrinterSorter {

	public static void execute(File scoreFile, File featureMapFile, File out, final boolean up) throws IOException {
		final List<Double> score = FileUtilities.loadDoubleListLines(scoreFile);
		Map<Integer, String> featureMap = FileUtilities.loadIntToStrMap(featureMapFile);

		PrintWriter writer = new PrintWriter(new FileWriter(out));

		List<Integer> sortedIDs = new ArrayList<Integer>(featureMap.keySet());
		Collections.sort(sortedIDs, new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				if (up) {
					return Double.compare(Math.abs(score.get(o1)), Math.abs(score.get(o2)));
				} else {
					return -Double.compare(Math.abs(score.get(o1)), Math.abs(score.get(o2)));
				}
			}
		});

		for (int ind = 0; ind < sortedIDs.size(); ind++) {
			if (!Double.isNaN(score.get(sortedIDs.get(ind)))) {
				writer.println(featureMap.get(sortedIDs.get(ind)) + " " + score.get(sortedIDs.get(ind)));
			}
		}
		writer.close();
	}

	public static void execute(File scoreFile, File out, final boolean up) throws IOException {
		final List<Double> score = FileUtilities.loadDoubleListLines(scoreFile);
		PrintWriter writer = new PrintWriter(new FileWriter(out));

		List<Integer> sortedIDs = new ArrayList<Integer>();
		for (int i = 0; i < score.size(); i++) {
			sortedIDs.add(i);
		}
		Collections.sort(sortedIDs, new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				if (up) {
					return Double.compare(Math.abs(score.get(o1)), Math.abs(score.get(o2)));
				} else {
					return -Double.compare(Math.abs(score.get(o1)), Math.abs(score.get(o2)));
				}
			}
		});

		for (int ind = 0; ind < sortedIDs.size(); ind++) {
			writer.println(sortedIDs.get(ind) + " " + score.get(sortedIDs.get(ind)));
		}
		writer.close();
	}
}
