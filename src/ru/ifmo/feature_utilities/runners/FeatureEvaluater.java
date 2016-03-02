package ru.ifmo.feature_utilities.runners;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import ru.ifmo.feature_utilities.importance.FeatureData;
import ru.ifmo.feature_utilities.importance.FeaturePrinterSorter;
import ru.ifmo.feature_utilities.importance.FitCriterion;
import ru.ifmo.feature_utilities.importance.IValuer;
import ru.ifmo.feature_utilities.importance.SpearmanCoefficient;
import ru.ifmo.feature_utilities.importance.SymmetricUncertainty;
import ru.ifmo.feature_utilities.importance.VDM;

public class FeatureEvaluater {
	private static String OUT_FOLDER_PATH = "feature_score_res/";

	private final static String FC_NAME = "fit_criterion";
	private final static String SPEARMAN_NAME = "spearman";
	private final static String SU_NAME = "symmetrical_uncertainty";
	private final static String VDM_NAME = "vdm";	//

	private final static String SORTED_PREFIX = "sorted_";	

	private static void init() {
		File outFolder = new File(OUT_FOLDER_PATH);
		if (!outFolder.exists() || !outFolder.isDirectory()) {
			outFolder.mkdirs();
		}
	}

	private static void printScore(double[] score, File out) throws IOException {
		PrintWriter writer = new PrintWriter(new FileWriter(out));
		System.err.println(score.length + " features");
		for (int i = 0; i < score.length; i++) {
			writer.println(score[i]);
		}
		writer.close();
	}

	private static void calculateScore(IValuer valuer, FeatureData data,
			String outPath) throws IOException {
		printScore(valuer.execute(data), new File(outPath));
		System.out.println(outPath.substring(OUT_FOLDER_PATH.length())
				+ " done");
	}

	public static void start(String[] args) throws IOException {
		if (args.length != 2 && args.length != 3) {
			System.err
					.println("Wrong num of params: feature_matrix, feature_vector, (out_folder)");
			return;
		}
		if (args.length == 3) {
			OUT_FOLDER_PATH = args[2] + "/";
		}

		init();

		FeatureData data = new FeatureData(new File(args[0]), new File(args[1]));

		calculateScore(new FitCriterion("", ""), data, OUT_FOLDER_PATH
				+ FC_NAME);
		calculateScore(new SpearmanCoefficient("", ""), data, OUT_FOLDER_PATH
				+ SPEARMAN_NAME);
		calculateScore(new SymmetricUncertainty("", ""), data, OUT_FOLDER_PATH
				+ SU_NAME);
		calculateScore(new VDM("", ""), data, OUT_FOLDER_PATH + VDM_NAME);
		

		FeaturePrinterSorter.execute(new File(OUT_FOLDER_PATH + FC_NAME),
				new File(OUT_FOLDER_PATH + SORTED_PREFIX + FC_NAME), false);
		FeaturePrinterSorter.execute(new File(OUT_FOLDER_PATH + SPEARMAN_NAME),
				new File(OUT_FOLDER_PATH + SORTED_PREFIX + SPEARMAN_NAME),
				false);

		FeaturePrinterSorter.execute(new File(OUT_FOLDER_PATH + SU_NAME),
				new File(OUT_FOLDER_PATH + SORTED_PREFIX + SU_NAME), false);
		FeaturePrinterSorter.execute(new File(OUT_FOLDER_PATH + VDM_NAME),
				new File(OUT_FOLDER_PATH + SORTED_PREFIX + VDM_NAME), false);
		

	}
}
