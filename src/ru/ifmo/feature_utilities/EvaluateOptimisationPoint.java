package ru.ifmo.feature_utilities;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;

import ru.ifmo.feature_utilities.classifiers.SVM;
import ru.ifmo.feature_utilities.importance.LoggerFactory;
import ru.ifmo.feature_utilities.runners.ClassifierEvaluater;
import ru.ifmo.feature_utilities.runners.ExperimentsPreparer;
import ru.ifmo.feature_utilities.runners.FeatureEvaluater;
import ru.ifmo.feature_utilities.runners.NewMatrixFromFeaturesCreator;
import ru.ifmo.feature_utilities.runners.Voter;
import ru.ifmo.feature_utilities.tests.CreateDatasetFromSparse;
import ru.ifmo.feature_utilities.tests.SelectFeatures;
import ru.ifmo.feature_utilities.tests.TestFeatureNumberSelector;
import ru.ifmo.feature_utilities.tests.TestShuffle;
import ru.ifmo.utilities.BackupUtilities;
import ru.ifmo.utilities.MatrixUtilities;
import ru.ifmo.utilities.TimerUtilities;

public class EvaluateOptimisationPoint {
	private static String workingFolder = "";
	public static final String FIRST_STEP_FILE = "1_start_matrix/matrix";

	public static final String SECOND_STEP_FOLDER = "2_matrixes_with_data";
	public static final String THIRD_STEP_FOLDER = "3_evaluated_features";
	public static final String FOURTH_STEP_FOLDER = "4_feature_sets_WPCA";
	public static final String FIFTH_STEP_FOLDER = "5_feature_matrixes_WPCA";
	public static final String SIXTH_STEP_FOLDER = "6_classifiers_WPCA_SVM";
	public static final String SEVENTH_STEP_FOLDER = "7_AUC_scores_WPCA_SVM";

	public static final String NUM_OF_EXPERIMENTS = "3";

	public static final String TRAINING_FEATURES = "/_training_features";
	public static final String TRAINING_CLASSES = "/_training_classes";

	public static final String TEST_FEATURES = "/_test_features";
	public static final String TEST_CLASSES = "/_test_classes";

	public static final String WEKA_TRAINING = "/_weka_training.csv";
	public static final String WEKA_TEST = "/_weka_test.csv";

	public static final String CLASSIFIER = "/classifier_";

	public static final int METHODS_AMOUNT = 4;

	public static void main(String[] args) {// TODO write
											// java-doc
											// (cause of
											// wtf?)
		System.setProperty("line.separator", "\n");

		try {
			String[] newArgs = new String[args.length - 1];
			for (int i = 1; i < args.length; i++) {
				newArgs[i - 1] = args[i];
			}

			if (args[0].equals("start")) {
				if (args.length == 2 + METHODS_AMOUNT) {
					if (args[1].charAt(args[1].length() - 1) == ('/')) {
						workingFolder = args[1];
					} else {
						workingFolder = args[1] + "/";
					}

					double[] coeff = new double[METHODS_AMOUNT];
					for (int i = 0; i < METHODS_AMOUNT; i++) {
						coeff[i] = Double.parseDouble(args[i + 2]);
					}

					int step = BackupUtilities.detectLastSucsessfulStep(workingFolder);
					LoggerFactory.getInstance().println("Last full step detected: " + step + ". Continue to work...");
					startAll(step, coeff);
				} else {
					LoggerFactory.getInstance().println("Wrong params");
				}
			} else if (args[0].equals("FeatureEvaluater")) {
				FeatureEvaluater.start(newArgs);
			} else if (args[0].equals("MakeClassifierTests")) {
				SelectFeatures.start(newArgs);
			} else if (args[0].equals("TestFeatureNumberSelector")) {
				TestFeatureNumberSelector.start(newArgs);
			} else if (args[0].equals("TestShuffle")) {
				TestShuffle.start(newArgs);
			} else if (args[0].equals("CreateMatrixFromMatrix")) {
				NewMatrixFromFeaturesCreator.start(newArgs);
			} else if (args[0].equals("CreateDatasetFromSparse")) {
				CreateDatasetFromSparse.start(newArgs);
			}
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			LoggerFactory.getInstance().println(sw.toString());
		}
	}

	private static void startAll(int lastSuccessfulStep, double[] coeff) throws IOException {
		BackupUtilities.deleteAllOddFolders(lastSuccessfulStep, workingFolder);

		TimerUtilities timer = new TimerUtilities();
		timer.start();
		LoggerFactory.getInstance().println("First step: " + timer.total());

		if (lastSuccessfulStep < 2) {
			ExperimentsPreparer.start(new String[] { workingFolder + FIRST_STEP_FILE, workingFolder + SECOND_STEP_FOLDER, NUM_OF_EXPERIMENTS });
			BackupUtilities.addEndsMark(workingFolder + SECOND_STEP_FOLDER);
			LoggerFactory.getInstance().println("\n-----------\nFOLDER 2 COMPLETE!\n-----------\n");
		}

		LoggerFactory.getInstance().println("Second step: " + timer.total());

		if (lastSuccessfulStep < 3) {
			File f = new File(workingFolder + SECOND_STEP_FOLDER);
			File outFolder = new File(workingFolder + THIRD_STEP_FOLDER);
			outFolder.mkdir();
			for (File folder : f.listFiles()) {
				if (folder.isDirectory()) {
					FeatureEvaluater.start(new String[] { folder.getAbsolutePath() + TRAINING_FEATURES, folder.getAbsolutePath() + TRAINING_CLASSES, outFolder.getAbsolutePath() + "/" + folder.getName() });
				}
			}
			BackupUtilities.addEndsMark(workingFolder + THIRD_STEP_FOLDER);
			LoggerFactory.getInstance().println("\n-----------\nFOLDER 3 COMPLETE!\n-----------\n");
		}

		LoggerFactory.getInstance().println("Third step: " + timer.total());

		if (lastSuccessfulStep < 4) {
			File f = new File(workingFolder + FOURTH_STEP_FOLDER);
			f.mkdir();
			File inFolder = new File(workingFolder + THIRD_STEP_FOLDER);

			File configFile = new File(workingFolder + "//config");
			PrintWriter cf = new PrintWriter(configFile);
			for (int i = 0; i < METHODS_AMOUNT; i++) {
				cf.print(coeff[i] + " ");
			}
			cf.close();

			for (File folder : inFolder.listFiles()) {
				if (folder.isDirectory()) {
					String outFolder = f.getAbsolutePath() + "/" + folder.getName() + "/";
					Voter.start(new String[] { folder.getAbsolutePath(), "4", outFolder, Voter.LINEARCOMBINATION });
				}
			}
			BackupUtilities.addEndsMark(workingFolder + FOURTH_STEP_FOLDER);
			LoggerFactory.getInstance().println("\n-----------\nFOLDER 4 COMPLETE!\n-----------\n");
		}

		LoggerFactory.getInstance().println("Fourth step: " + timer.total());

		if (lastSuccessfulStep < 5) {
			File f = new File(workingFolder + FIFTH_STEP_FOLDER);
			f.mkdir();
			File inFolder = new File(workingFolder + FOURTH_STEP_FOLDER);
			for (File folder : inFolder.listFiles()) {
				if (folder.isDirectory()) {
					LoggerFactory.getInstance().println("Folder " + folder.getName());

					File outSubFolder = new File(workingFolder + FIFTH_STEP_FOLDER + "/" + folder.getName());
					outSubFolder.mkdir();

					for (String method : folder.list()) {
						File outMethodFolder = new File(outSubFolder.getAbsolutePath() + "/matrix_" + method);

						outMethodFolder.mkdir();
						String secondSubFolder = workingFolder + SECOND_STEP_FOLDER + "/" + folder.getName();

						NewMatrixFromFeaturesCreator.start(new String[] { secondSubFolder + TRAINING_FEATURES, secondSubFolder + TEST_FEATURES, workingFolder + FOURTH_STEP_FOLDER + "/" + folder.getName() + "/" + method, outMethodFolder.getAbsolutePath() });
						// TODO may be move to individual class
						Files.copy(new File(secondSubFolder + TRAINING_CLASSES).toPath(), new File(outMethodFolder.getAbsolutePath() + TRAINING_CLASSES).toPath());

						Files.copy(new File(secondSubFolder + TEST_CLASSES).toPath(), new File(outMethodFolder.getAbsolutePath() + TEST_CLASSES).toPath());

						MatrixUtilities.createWekaMatrix(new File(outMethodFolder.getAbsolutePath() + TRAINING_FEATURES), new File(outMethodFolder.getAbsolutePath() + TRAINING_CLASSES), new File(outMethodFolder.getAbsolutePath() + WEKA_TRAINING));

						MatrixUtilities.createWekaMatrix(new File(outMethodFolder.getAbsolutePath() + TEST_FEATURES), new File(outMethodFolder.getAbsolutePath() + TEST_CLASSES), new File(outMethodFolder.getAbsolutePath() + WEKA_TEST));
					}
				}
				LoggerFactory.getInstance().println(" is done\n");
			}
			BackupUtilities.addEndsMark(workingFolder + FIFTH_STEP_FOLDER);
			LoggerFactory.getInstance().println("\n-----------\nFOLDER 5 COMPLETE!\n-----------\n");
		}

		LoggerFactory.getInstance().println("Fifth step: " + timer.total());

		if (lastSuccessfulStep < 6) {
			File f = new File(workingFolder + SIXTH_STEP_FOLDER);
			f.mkdir();
			File inFolder = new File(workingFolder + FIFTH_STEP_FOLDER);
			for (File folder : inFolder.listFiles()) {
				if (folder.isDirectory()) {
					LoggerFactory.getInstance().println("Folder " + folder.getName());
					File outSubFolder = new File(f.getAbsolutePath() + "/" + folder.getName());
					outSubFolder.mkdir();
					File inSubFolder = new File(inFolder.getAbsolutePath() + "/" + folder.getName());
					for (String method : inSubFolder.list()) {
						// TODO may be move to individual class
						SVM classifier = new SVM();// <==== set classifier
						classifier.teachClassifier(new File(inFolder.getAbsolutePath() + "/" + folder.getName() + "/" + method + WEKA_TRAINING));
						classifier.writeClassifier(new File(f.getAbsolutePath() + "/" + folder.getName() + CLASSIFIER + method));
					}
				}
				LoggerFactory.getInstance().println(" is done\n");
			}
			BackupUtilities.addEndsMark(workingFolder + SIXTH_STEP_FOLDER);
			LoggerFactory.getInstance().println("\n-----------\nFOLDER 6 COMPLETE!\n-----------\n");
		}

		LoggerFactory.getInstance().println("Sixth step: " + timer.total());

		if (lastSuccessfulStep < 7) {
			File f = new File(workingFolder + SEVENTH_STEP_FOLDER);
			f.mkdir();
			File inFolder = new File(workingFolder + FIFTH_STEP_FOLDER);

			ClassifierEvaluater.setClassifier(new SVM());// <==== set classifier

			for (File folder : inFolder.listFiles()) {
				if (folder.isDirectory()) {
					LoggerFactory.getInstance().println("Folder " + folder.getName());
					File outSubFolder = new File(f.getAbsolutePath() + "/" + folder.getName());
					outSubFolder.mkdir();
					File inSubFolder = new File(inFolder.getAbsolutePath() + "/" + folder.getName());
					for (String method : inSubFolder.list()) {
						ClassifierEvaluater.start(new String[] { inFolder.getAbsolutePath() + "/" + folder.getName() + "/" + method, workingFolder + SIXTH_STEP_FOLDER + "/" + folder.getName() + CLASSIFIER + method, f.getAbsolutePath() + "/" + folder.getName() + "/" + method + "/" });
					}
				}
				LoggerFactory.getInstance().println(" is done\n");
			}
			BackupUtilities.addEndsMark(workingFolder + SEVENTH_STEP_FOLDER);
			LoggerFactory.getInstance().println("\n-----------\nFOLDER 7 COMPLETE!\n-----------\n");
		}

		LoggerFactory.getInstance().println("Seventh step: " + timer.total());

	}
}
