package ru.ifmo.feature_utilities.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import ru.ifmo.feature_utilities.Starter;
import ru.ifmo.utilities.FileUtilities;

public class MethodVSMethod {

	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			throw new IOException("Input working folder");
		}

		ArrayList<String> fileNames = new ArrayList<String>();

		File workingFolder = new File(args[0] + "/3_evaluated_features/1/");
		for (File f : workingFolder.listFiles()) {
			if (!f.getName().contains("sorted")) {
				fileNames.add(f.getName());
			}
		}

		Object[] tmp = fileNames.toArray();
		String[] names = new String[tmp.length];
		for (int i = 0; i < tmp.length; i++) {
			names[i]=(String)tmp[i];
		}

		int numOfExperiments = Integer.parseInt(Starter.NUM_OF_EXPERIMENTS);
		int numOfFeatures = FileUtilities.getNumOfStringsInFile(workingFolder.listFiles()[0]);
		double[][] methodsScores = new double[names.length][numOfFeatures];

		for (int k = 0; k < numOfExperiments; k++) {
			for (int i = 0; i < names.length; i++) {
				BufferedReader br = new BufferedReader(new FileReader(args[0] + "/3_evaluated_features/" + (k + 1) + "/" + names[i]));
				for (int j = 0; j < numOfFeatures; j++) {
					methodsScores[i][j] += Math.abs(Double.parseDouble(br.readLine()));
				}
				br.close();
			}
		}

		for (int i = 0; i < names.length; i++) {
			for (int j = 0; j < numOfFeatures; j++) {
				methodsScores[i][j] /= (double) numOfExperiments;
			}
		}

		for (int i = 0; i < names.length; i++) {
			for (int j = i + 1; j < names.length; j++) {
				PrintWriter pw = new PrintWriter(new FileWriter("scores_" + names[i] + "_vs_" + names[j]));
				for (int k = 0; k < numOfFeatures; k++) {
					pw.println(methodsScores[i][k] + " " + methodsScores[j][k]);
				}
				pw.close();
			}
		}

	}
}
