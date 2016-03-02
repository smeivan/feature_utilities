package ru.ifmo.feature_utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.StringTokenizer;

import ru.ifmo.feature_utilities.classifiers.KNN;
import ru.ifmo.feature_utilities.classifiers.RandomForest;
import ru.ifmo.feature_utilities.classifiers.SVM;
import ru.ifmo.feature_utilities.importance.LoggerFactory;
import ru.ifmo.feature_utilities.runners.ClassifierEvaluater;
import ru.ifmo.feature_utilities.runners.ExperimentsPreparer;
import ru.ifmo.feature_utilities.runners.FeatureEvaluater;
import ru.ifmo.feature_utilities.runners.NewMatrixFromFeaturesCreator;
import ru.ifmo.feature_utilities.runners.Voter;
import ru.ifmo.utilities.BackupUtilities;
import ru.ifmo.utilities.MatrixUtilities;
import ru.ifmo.utilities.TimerUtilities;

public class LinearCombinationOptimization {
	private static String workingFolder = "";

	private static String SECOND_STEP_FOLDER = "2_matrixes_with_data";
	private static String THIRD_STEP_FOLDER = "3_evaluated_features";
	private static String FOURTH_STEP_FOLDER = "4_feature_sets_WPCA";
	private static String FIFTH_STEP_FOLDER = "5_feature_matrixes_WPCA";
	private static String SIXTH_STEP_FOLDER = "6_classifiers_WPCA_SVM";
	private static String SEVENTH_STEP_FOLDER = "7_AUC_scores_WPCA_SVM";

	private static String TRAINING_FEATURES = "/_training_features";
	private static String TRAINING_CLASSES = "/_training_classes";

	private static String TEST_FEATURES = "/_test_features";
	private static String TEST_CLASSES = "/_test_classes";

	private static String WEKA_TRAINING = "/_weka_training.csv";
	private static String WEKA_TEST = "/_weka_test.csv";

	private static String CLASSIFIER = "/classifier_";

	private static void prepare(String datasetFolder) throws IOException {
		int lastSuccessfulStep = Math.min(BackupUtilities.detectLastSucsessfulStep(datasetFolder), 3);

		BackupUtilities.deleteAllOddFolders(lastSuccessfulStep, datasetFolder);

		TimerUtilities timer = new TimerUtilities();
		timer.start();
		LoggerFactory.getInstance().println("First step: " + timer.total());

		if (lastSuccessfulStep < 2) {
			ExperimentsPreparer.start(new String[] { datasetFolder + Starter.FIRST_STEP_FILE, datasetFolder + Starter.SECOND_STEP_FOLDER, Starter.NUM_OF_EXPERIMENTS });
			BackupUtilities.addEndsMark(datasetFolder + Starter.SECOND_STEP_FOLDER);
			LoggerFactory.getInstance().println("\n-----------\nFOLDER 2 COMPLETE!\n-----------\n");
		}

		if (lastSuccessfulStep < 3) {
			File f = new File(datasetFolder + SECOND_STEP_FOLDER);
			File outFolder = new File(datasetFolder + THIRD_STEP_FOLDER);
			outFolder.mkdir();
			for (File folder : f.listFiles()) {
				if (folder.isDirectory()) {
					FeatureEvaluater.start(new String[] { folder.getAbsolutePath() + TRAINING_FEATURES, folder.getAbsolutePath() + TRAINING_CLASSES, outFolder.getAbsolutePath() + "/" + folder.getName() });
				}
			}
			BackupUtilities.addEndsMark(datasetFolder + THIRD_STEP_FOLDER);
			LoggerFactory.getInstance().println("\n-----------\nFOLDER 3 COMPLETE!\n-----------\n");
		}

		LoggerFactory.getInstance().println("Third step: " + timer.total());

		LoggerFactory.getInstance().println("Second step: " + timer.total());

	}

	private static void execute(String datasetFolder) throws IOException {

		int lastSuccessfulStep = BackupUtilities.detectLastSucsessfulStep(datasetFolder);
		TimerUtilities timer = new TimerUtilities();
		timer.start();

		if (lastSuccessfulStep < 4) {
			File f = new File(datasetFolder + FOURTH_STEP_FOLDER);
			f.mkdir();
			File inFolder = new File(datasetFolder + THIRD_STEP_FOLDER);

			for (File folder : inFolder.listFiles()) {
				if (folder.isDirectory()) {
					String outFolder = f.getAbsolutePath() + "/" + folder.getName() + "/";
					Voter.start(new String[] { folder.getAbsolutePath(), "4", outFolder, Voter.LINEARCOMBINATION });
				}
			}
			BackupUtilities.addEndsMark(datasetFolder + FOURTH_STEP_FOLDER);
			LoggerFactory.getInstance().println("\n-----------\nFOLDER 4 COMPLETE!\n-----------\n");
		}

		LoggerFactory.getInstance().println("Fourth step: " + timer.total());

		if (lastSuccessfulStep < 5) {
			File f = new File(datasetFolder + FIFTH_STEP_FOLDER);
			f.mkdir();
			File inFolder = new File(datasetFolder + FOURTH_STEP_FOLDER);
			for (File folder : inFolder.listFiles()) {
				if (folder.isDirectory()) {
					LoggerFactory.getInstance().println("Folder " + folder.getName());

					File outSubFolder = new File(datasetFolder + FIFTH_STEP_FOLDER + "/" + folder.getName());
					outSubFolder.mkdir();

					for (String method : folder.list()) {
						File outMethodFolder = new File(outSubFolder.getAbsolutePath() + "/matrix_" + method);

						outMethodFolder.mkdir();
						String secondSubFolder = datasetFolder + SECOND_STEP_FOLDER + "/" + folder.getName();

						NewMatrixFromFeaturesCreator.start(new String[] { secondSubFolder + TRAINING_FEATURES, secondSubFolder + TEST_FEATURES, datasetFolder + FOURTH_STEP_FOLDER + "/" + folder.getName() + "/" + method, outMethodFolder.getAbsolutePath() });
						Files.copy(new File(secondSubFolder + TRAINING_CLASSES).toPath(), new File(outMethodFolder.getAbsolutePath() + TRAINING_CLASSES).toPath());

						Files.copy(new File(secondSubFolder + TEST_CLASSES).toPath(), new File(outMethodFolder.getAbsolutePath() + TEST_CLASSES).toPath());

						MatrixUtilities.createWekaMatrix(new File(outMethodFolder.getAbsolutePath() + TRAINING_FEATURES), new File(outMethodFolder.getAbsolutePath() + TRAINING_CLASSES), new File(outMethodFolder.getAbsolutePath() + WEKA_TRAINING));

						MatrixUtilities.createWekaMatrix(new File(outMethodFolder.getAbsolutePath() + TEST_FEATURES), new File(outMethodFolder.getAbsolutePath() + TEST_CLASSES), new File(outMethodFolder.getAbsolutePath() + WEKA_TEST));
					}
				}
				LoggerFactory.getInstance().println(" is done\n");
			}
			BackupUtilities.addEndsMark(datasetFolder + FIFTH_STEP_FOLDER);
			LoggerFactory.getInstance().println("\n-----------\nFOLDER 5 COMPLETE!\n-----------\n");
		}

		LoggerFactory.getInstance().println("Fifth step: " + timer.total());

		if (lastSuccessfulStep < 6) {
			File f = new File(datasetFolder + SIXTH_STEP_FOLDER);
			f.mkdir();
			File inFolder = new File(datasetFolder + FIFTH_STEP_FOLDER);
			for (File folder : inFolder.listFiles()) {
				if (folder.isDirectory()) {
					LoggerFactory.getInstance().println("Folder " + folder.getName());
					File outSubFolder = new File(f.getAbsolutePath() + "/" + folder.getName());
					outSubFolder.mkdir();
					File inSubFolder = new File(inFolder.getAbsolutePath() + "/" + folder.getName());
					for (String method : inSubFolder.list()) {
						RandomForest classifier = new RandomForest();// <====
																		// set
																		// classifier
						classifier.teachClassifier(new File(inFolder.getAbsolutePath() + "/" + folder.getName() + "/" + method + WEKA_TRAINING));
						classifier.writeClassifier(new File(f.getAbsolutePath() + "/" + folder.getName() + CLASSIFIER + method));
					}
				}
				LoggerFactory.getInstance().println(" is done\n");
			}
			BackupUtilities.addEndsMark(datasetFolder + SIXTH_STEP_FOLDER);
			LoggerFactory.getInstance().println("\n-----------\nFOLDER 6 COMPLETE!\n-----------\n");
		}

		LoggerFactory.getInstance().println("Sixth step: " + timer.total());

		if (lastSuccessfulStep < 7) {
			File f = new File(datasetFolder + SEVENTH_STEP_FOLDER);
			f.mkdir();
			File inFolder = new File(datasetFolder + FIFTH_STEP_FOLDER);

			ClassifierEvaluater.setClassifier(new RandomForest());// <==== set
																	// classifier

			for (File folder : inFolder.listFiles()) {
				if (folder.isDirectory()) {
					LoggerFactory.getInstance().println("Folder " + folder.getName());
					File outSubFolder = new File(f.getAbsolutePath() + "/" + folder.getName());
					outSubFolder.mkdir();
					File inSubFolder = new File(inFolder.getAbsolutePath() + "/" + folder.getName());
					for (String method : inSubFolder.list()) {
						ClassifierEvaluater.start(new String[] { inFolder.getAbsolutePath() + "/" + folder.getName() + "/" + method, datasetFolder + SIXTH_STEP_FOLDER + "/" + folder.getName() + CLASSIFIER + method, f.getAbsolutePath() + "/" + folder.getName() + "/" + method + "/" });
					}
				}
				LoggerFactory.getInstance().println(" is done\n");
			}
			BackupUtilities.addEndsMark(datasetFolder + SEVENTH_STEP_FOLDER);
			LoggerFactory.getInstance().println("\n-----------\nFOLDER 7 COMPLETE!\n-----------\n");
		}

		LoggerFactory.getInstance().println("Seventh step: " + timer.total());

	}

	public static void main(String[] args) throws IOException {
		System.setProperty("line.separator", "\n");

		try {
			String[] newArgs = new String[args.length - 1];
			for (int i = 1; i < args.length; i++) {
				newArgs[i - 1] = args[i];
			}

			if (args[0].equals("start")) {
				if (args.length == 2) {
					if (args[1].charAt(args[1].length() - 1) == ('/')) {
						workingFolder = args[1];
					} else {
						workingFolder = args[1] + "/";
					}
				} else {
					LoggerFactory.getInstance().println("Wrong params");
				}
			}

		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			LoggerFactory.getInstance().println(sw.toString());
		}

		File f = new File(workingFolder);

		double oldResult = 0;
		double[] oldCoefficients = new double[] { 1, 1, 1, 1, 1 };
		double[] currentCoefficients = new double[] { 1, 1, 1, 1, 1 };
		double step = 0.5;
		int changedIndex = -1;

		while (changedIndex<5) {
			for (File folder : f.listFiles()) {
				if (!folder.isDirectory()) {
					continue;
				}
				
				if (changedIndex >= 0) {
					currentCoefficients[changedIndex] += step;
					PrintWriter pw = new PrintWriter(folder.getAbsolutePath() + "/config");
					for (int i = 0; i < 5; i++) {
						pw.print(currentCoefficients[i] + " ");
					}
					pw.close();
				}

				prepare(folder.getAbsolutePath() + "/");
				execute(folder.getAbsolutePath() + "/");

				if (changedIndex == -1) {
					changedIndex = 0;
					File resultingAUC = new File(folder.getAbsolutePath() + "/" + SEVENTH_STEP_FOLDER + "/1/matrix_fit_criterion_spearman_symmetrical_uncertainty_vdm_LINEARCOMBINATION_62/rank");
					BufferedReader br = new BufferedReader(new FileReader(resultingAUC));
					oldResult = Double.parseDouble(br.readLine());
					br.close();
					continue;
				}

				File resultingAUC = new File(folder.getAbsolutePath() + "/" + SEVENTH_STEP_FOLDER + "/1/matrix_fit_criterion_spearman_symmetrical_uncertainty_vdm_LINEARCOMBINATION_62/rank");
				BufferedReader br = new BufferedReader(new FileReader(resultingAUC));
				double newResult = Double.parseDouble(br.readLine());
				br.close();
				if (newResult > oldResult) {
					System.err.println("New result = " + newResult);
					oldResult = newResult;
					for (int i = 0; i < 5; i++) {
						oldCoefficients[i] = currentCoefficients[i];
					}
					br.close();
				} else {
					PrintWriter pw = new PrintWriter(folder.getAbsolutePath() + "/config");
					for (int i = 0; i < 5; i++) {
						pw.print(oldCoefficients[i] + " ");
					}
					pw.close();
					if (step == -0.5){
						step = 0.5;
						changedIndex++;
					}
					if (oldCoefficients[changedIndex]==1){
						step=-0.5;
					}
				}
			}
		}

	}
}
