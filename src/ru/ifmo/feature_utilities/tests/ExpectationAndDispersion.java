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

public class ExpectationAndDispersion {

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

		int numOfExperiments = Integer.parseInt(Starter.NUM_OF_EXPERIMENTS);
		int numOfFeatures = FileUtilities.getNumOfStringsInFile(workingFolder.listFiles()[0]);
		
		double[] resultingExpectation = new double[numOfFeatures];
		double[] resultingDispersion = new double[numOfFeatures];
	
		int numOfMethods = 0;
		for (String name : fileNames) {
			numOfMethods += 1;
			double minValue = Double.MAX_VALUE;
			double maxValue = Double.MIN_NORMAL;
			for (int i = 0; i < numOfExperiments; i++) {
				BufferedReader br = new BufferedReader(new FileReader(args[0] + "/3_evaluated_features/" + (i + 1) + "/" + name));
				for (int k = 0; k < numOfFeatures; k++) {
					double featureScore = Math.abs(Double.parseDouble(br.readLine()));
					if (featureScore > maxValue) {
						maxValue = featureScore;
					}
					if (featureScore < minValue) {
						minValue = featureScore;
					}
				}
				br.close();
			}

			for (int i = 0; i < numOfExperiments; i++) {
				BufferedReader br = new BufferedReader(new FileReader(args[0] + "/3_evaluated_features/" + (i + 1) + "/" + name));
				for (int k = 0; k < numOfFeatures; k++) {
					double featureScore = Math.abs(Double.parseDouble(br.readLine()));
					resultingExpectation[k] += (featureScore - minValue) / (maxValue - minValue);
				}
				br.close();
			}
		}
		
		for (int i=0; i<numOfFeatures;i++){
			resultingExpectation[i]/=(double)(numOfMethods*numOfFeatures);
		}

		for (String name : fileNames) {
			for (int i = 0; i < numOfExperiments; i++) {
				BufferedReader br = new BufferedReader(new FileReader(args[0] + "/3_evaluated_features/" + (i + 1) + "/" + name));
				for (int k = 0; k < numOfFeatures; k++) {
					double featureScore = Math.abs(Double.parseDouble(br.readLine()));
					resultingDispersion[k] += (featureScore - resultingExpectation[k]) * (featureScore - resultingExpectation[k]);
				}
				br.close();
			}
		}
		
		PrintWriter pw = new PrintWriter(new FileWriter("result"));
		for (int k = 0; k < numOfFeatures; k++) {
			resultingDispersion[k] /= (double) numOfExperiments;
			pw.println(resultingExpectation[k] + " " + resultingDispersion[k]);
		}
		pw.close();
	}
	}
