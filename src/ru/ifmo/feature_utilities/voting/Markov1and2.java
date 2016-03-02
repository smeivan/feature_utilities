package ru.ifmo.feature_utilities.voting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.StringTokenizer;

import ru.ifmo.utilities.FileUtilities;
import ru.ifmo.utilities.IndexingPair;

public class Markov1and2 implements VotingInterface {
	public void vote(File[] inFiles, String outFile, int numOfFeatures)
			throws IOException {
		int m = FileUtilities.getNumOfStringsInFile(inFiles[0]);
		int n = inFiles.length;

		double[] weightsM1 = new double[m];
		Arrays.fill(weightsM1, 1);

		double[] weightsM2 = new double[m];
		Arrays.fill(weightsM2, 1);

		double subtrahend = 1 / ((double) m);

		IndexingPair[][] input = new IndexingPair[n][m];

		for (int i = 0; i < n; i++) {
			BufferedReader br = new BufferedReader(new FileReader(inFiles[i]));

			for (int j = 0; j < m; j++) {
				StringTokenizer st = new StringTokenizer(br.readLine());
				input[i][j] = new IndexingPair(m
						- Integer.parseInt(st.nextToken()) + 1, j);

			}
			br.close();
			Arrays.sort(input[i]);
		}

		for (int i = 0; i < m; i++) {
			int[] moreAt = new int[m];
			int medium = n / 2;
			for (int j = 0; j < n; j++) {
				int featureIndex = input[j][i].value;
				for (int k = 0; k < m; k++) {
					if (input[j][k].value > featureIndex) {
						moreAt[k] += 1;
					}
				}
			}
			for (int j = 0; j < m; j++) {
				if (moreAt[j] > 0) {
					weightsM1[j] -= subtrahend;
				}
				if (moreAt[j] > medium) {
					weightsM2[j] -= subtrahend;
				}
			}
		}

		IndexingPair[] resultsM1 = new IndexingPair[m];
		for (int i = 0; i < m; i++) {
			resultsM1[i] = new IndexingPair((int) (weightsM1[i] * 1000000000),
					i);//*1000000000????????
		}
		Arrays.sort(resultsM1);

		PrintWriter pw = new PrintWriter(outFile + "_M1");
		for (int i = 0; i < numOfFeatures; i++) {
			pw.println(resultsM1[i].value);
		}
		pw.close();

		IndexingPair[] resultsM2 = new IndexingPair[m];
		for (int i = 0; i < m; i++) {
			resultsM2[i] = new IndexingPair((int) (weightsM2[i] * 1000000000),
					i);
		}
		Arrays.sort(resultsM2);

		PrintWriter pw2 = new PrintWriter(outFile + "_M2");
		for (int i = 0; i < numOfFeatures; i++) {
			pw2.println(resultsM2[i].value);
		}
		pw2.close();

	}
}
