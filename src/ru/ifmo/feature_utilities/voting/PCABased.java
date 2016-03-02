package ru.ifmo.feature_utilities.voting;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ru.ifmo.utilities.DoubleIntPair;
import ru.ifmo.utilities.FileUtilities;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.PrincipalComponents;

public class PCABased implements VotingInterface {

	@Override
	public void vote(File[] inFiles, String outFile, int numOfFeatures)
			throws IOException {

		int m = FileUtilities.getNumOfStringsInFile(inFiles[0]);// number of
																// features
		int n = inFiles.length;// number of votes

		Map<Integer, Integer> newToOldIndex = new HashMap<Integer, Integer>();

		double[][] input = FileUtilities.fillMatrix(inFiles, m, n,
				numOfFeatures, newToOldIndex);

		normalizeData(input.length, n, input);

		Instances data = createWekaInstances(input.length, n, input);

		PrincipalComponents pca = new PrincipalComponents();
		DoubleIntPair[] newMeasure = new DoubleIntPair[input.length];

		try {
			pca.setMaximumAttributes(1);
			pca.setInputFormat(data);
			Instances newData = Filter.useFilter(data, pca);
			for (int i = 0; i < input.length; i++) {
				newMeasure[i] = new DoubleIntPair(newData.instance(i).value(0),
						i);
			}
			Arrays.sort(newMeasure);
		} catch (Exception e) {
			System.err.println("Weka error:");
			e.printStackTrace();
			return;
		}

		PrintWriter pw = new PrintWriter(outFile);
		for (int i = 0; i < numOfFeatures; i++) {
			pw.println(newToOldIndex.get(newMeasure[i].value));
		}
		pw.close();

	}

	private void normalizeData(int m, int n, double[][] input) {
		for (int measure = 0; measure < n; measure++) {
			double min = Double.MAX_VALUE;
			double max = Double.MIN_VALUE;
			for (int inst = 0; inst < m; inst++) {
				if (input[inst][measure] > max) {
					max = input[inst][measure];
				}
				if (input[inst][measure] < min) {
					min = input[inst][measure];
				}
			}
			for (int inst = 0; inst < m; inst++) {
				input[inst][measure] = (input[inst][measure] - min)
						/ (max - min);
			}
		}
	}

	private Instances createWekaInstances(int m, int n, double[][] input) {
		FastVector atts = new FastVector();
		for (int i = 1; i < n + 1; i++) {
			atts.addElement(new Attribute(Integer.toString(i)));
		}
		Instances instances = new Instances("Test", atts, 0);
		for (int i = 0; i < m; i++) {
			Instance inst = new Instance(n);
			inst.setDataset(instances);
			for (int j = 0; j < n; j++) {
				inst.setValue(j, input[i][j]);
			}
			instances.add(inst);
		}
		return instances;
	}

}
