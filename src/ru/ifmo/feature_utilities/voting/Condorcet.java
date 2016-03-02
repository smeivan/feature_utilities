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

public class Condorcet implements VotingInterface {
	public void vote(File[] inFiles, String outFile, int numOfFeatures) throws IOException {
		int m = FileUtilities.getNumOfStringsInFile(inFiles[0]);
		int n = inFiles.length;

		int[][] input = new int[n][m];

		for (int i = 0; i < n; i++) {
			BufferedReader br = new BufferedReader(new FileReader(inFiles[i]));
			IndexingPair[] currentInput = new IndexingPair[m];
			for (int j = 0; j < m; j++) {
				StringTokenizer st = new StringTokenizer(br.readLine());
				currentInput[j] = new IndexingPair(m - Integer.parseInt(st.nextToken()), j);
				
			}
			br.close();
			Arrays.sort(currentInput);
			for (int j = 0; j < m; j++) {
				input[i][j] = currentInput[j].value;
			}
		}

		IndexingPair[] result = new IndexingPair[m];
		int[] temp = new int[m];

		for (int i = 0; i < m; i++) {
			Arrays.fill(temp, 0);
			
			for (int j = 0; j < n; j++) {
				int ithFeatureIndex = input[j][i];
				for (int k = 0; k < m; k++) {
					if (input[j][k] > ithFeatureIndex) {
						temp[input[j][k]] += 1;
					}
				}
			}
			int barrier = n / 2;
			int res = 0;
			for (int j = 0; j < m; j++) {
				if (temp[j] > barrier) {
					res += 1;
				}
			}
			result[input[0][i]] = new IndexingPair(res, i);
		}

		Arrays.sort(result);

		PrintWriter pw = new PrintWriter(outFile);
		for (int i = 0; i < numOfFeatures; i++) {
			pw.println(result[i].value);
		}
		pw.close();

	}

}
