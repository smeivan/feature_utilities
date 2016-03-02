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

public class Borda implements VotingInterface {
	public void vote(File[] inFiles, String outFile, int numOfFeatures) throws IOException {
		int m = FileUtilities.getNumOfStringsInFile(inFiles[0]);
		int n = inFiles.length;

		IndexingPair[] results = new IndexingPair[m];
		for (int i = 0; i < results.length; i++) {
			results[i] = new IndexingPair(0, i);
		}

		for (int i = 0; i < n; i++) {
			BufferedReader br = new BufferedReader(new FileReader(inFiles[i]));
			int k = 1;
			while (br.ready()) {
				StringTokenizer st = new StringTokenizer(br.readLine());
				int feature = Integer.parseInt(st.nextToken());
				results[feature].key += m - k;
				k++;
			}
			br.close();
		}

		Arrays.sort(results);

		PrintWriter pw = new PrintWriter(outFile);
		for (int i = 0; i < numOfFeatures; i++) {
			pw.println(results[i].value);
		}
		pw.close();
	}

}
