package ru.ifmo.feature_utilities.runners;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import ru.ifmo.feature_utilities.Starter;
import ru.ifmo.feature_utilities.classifier_ranker.AUC;
import ru.ifmo.feature_utilities.classifier_ranker.RocPoint;
import ru.ifmo.feature_utilities.classifiers.IClassifier;
import ru.ifmo.feature_utilities.classifiers.SVM;
import ru.ifmo.utilities.TimerUtilities;

public class ClassifierEvaluater {

	//private static final String TESTING_MATRIX_FILE_NAME = "_testing_features";
	//private static final String TESTING_ANSWERS_FILE_NAME = "_testing_classes";
	private static final String FOLDER_PREFIX = "matrix_";
	private static final String TESTING_CLASSIFIERS_ANSWERS_FILE_NAME = "_test_classifier_answers";
	private static final String RANK_FILE_NAME = "rank";
	private static final String RANK_TIME_FILE_NAME = "rank_time";
	private static final String ROC_POINTS_FILE_NAME = "roc_points";
	
	private static IClassifier classifier; 

	private static void evaluateClassifier(File testingSetFolder, File classifierFile, File outFolder, AUC auc) throws IOException {
		long time = classify(testingSetFolder, classifierFile, outFolder);
		printTime(outFolder, time);
		printRank(testingSetFolder, auc, outFolder);
		printPoints(outFolder, auc);
	}

	private static void printPoints(File folder, AUC auc) throws IOException {
		PrintWriter pw = new PrintWriter(new FileWriter(new File(folder.getAbsolutePath() + "/" + ROC_POINTS_FILE_NAME)));
		for (RocPoint point : auc.getPoints()) {
			pw.println(point.FPR + " " + point.TPR);
		}
		pw.close();
	}

	private static long classify(File inFolder, File paramFile, File outFolder) throws IOException {
		if (classifier == null){
			classifier = new SVM();//default classifier
		}
		classifier.loadClassifier(paramFile);
		TimerUtilities timerUtilities = new TimerUtilities();
		classifier.classify(new File(inFolder.getAbsolutePath() + Starter.TEST_FEATURES), new File(outFolder.getAbsolutePath() + "/" + TESTING_CLASSIFIERS_ANSWERS_FILE_NAME));
		return timerUtilities.stop();
	}

	private static void printRank(File inFolder, AUC auc, File outFolder) throws IOException {//-Dfile.encoding=utf8
		PrintWriter pw = new PrintWriter(new FileWriter(new File(outFolder.getAbsolutePath() + "/" + RANK_FILE_NAME)));
		//PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File(outFolder.getAbsolutePath() + "/" + RANK_FILE_NAME)), "UTF-8"));
		pw.println(auc.getRank(new File(outFolder.getAbsolutePath() + "/" + TESTING_CLASSIFIERS_ANSWERS_FILE_NAME), new File(inFolder.getAbsolutePath() + Starter.TEST_CLASSES)));
		pw.close();
	}

	private static void printTime(File folder, long time) throws IOException {
		PrintWriter pw = new PrintWriter(new FileWriter(new File(folder.getAbsolutePath() + "/" + RANK_TIME_FILE_NAME)));
		pw.println(time);
		pw.close();
	}
	
	public static void setClassifier(IClassifier classifier){
		ClassifierEvaluater.classifier = classifier;
	}

	public static void start(String[] args) throws IOException {
		if (args.length != 3) {
			System.err.println("Wrong num of params: folder with testing set, file with classifier, out folder");
			return;
		}
		AUC auc = new AUC();
		File testingSetFolder = new File(args[0]);
		File classifierFile = new File(args[1]);
		File outFolder = new File(args[2]);
		outFolder.mkdir();
		
		if (testingSetFolder.isDirectory() && testingSetFolder.getName().startsWith(FOLDER_PREFIX)) {
			System.out.println(testingSetFolder.getName() + " started");
			evaluateClassifier(testingSetFolder, classifierFile, outFolder,  auc);
			System.out.println(testingSetFolder.getName() + " done");
		}
	}
}
