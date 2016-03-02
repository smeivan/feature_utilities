package ru.ifmo.feature_utilities.voting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.StringTokenizer;

import ru.ifmo.feature_utilities.EvaluateOptimisationPoint;
import ru.ifmo.utilities.FileUtilities;
import ru.ifmo.utilities.IndexingPair;

public class LinearCombination implements VotingInterface {

	private double[] coefficients = new double[] { 1, 1, 1, 1 };

	public double[] getCoefficients() {
		return coefficients;
	}

	public void setCoefficients(double[] coefficients) {
		this.coefficients = coefficients;
	}

	@Override
	public void vote(File[] inFiles, String outFile, int numOfFeatures) throws IOException {
		
		File config = new File(inFiles[0].getParent() + "/../../config");
		BufferedReader configReader = new BufferedReader(new FileReader(config));
		StringTokenizer configString = new StringTokenizer(configReader.readLine());
		int methodsAmount = EvaluateOptimisationPoint.METHODS_AMOUNT;
		for (int i = 0; i < methodsAmount; i++) {
			coefficients[i] = Double.parseDouble(configString.nextToken());
		}
		configReader.close();

		int m = FileUtilities.getNumOfStringsInFile(inFiles[0]);
		int n = inFiles.length;

		IndexingPair[] result = new IndexingPair[m];
		for (int i = 0; i < result.length; i++) {
			result[i] = new IndexingPair(0, i);
		}

		for (int i = 0; i < n; i++) {
			BufferedReader br = new BufferedReader(new FileReader(inFiles[i]));
			while (br.ready()) {
				StringTokenizer st = new StringTokenizer(br.readLine());
				int feature = Integer.parseInt(st.nextToken());
				double value = Math.abs(Double.parseDouble(st.nextToken()));
				result[feature].key += coefficients[i] * value;
			}
			br.close();
		}

		Arrays.sort(result);

		PrintWriter pw = new PrintWriter(outFile);
		for (int i = 0; i < numOfFeatures; i++) {
			pw.println(result[i].value);
		}
		pw.close();

	}

}
