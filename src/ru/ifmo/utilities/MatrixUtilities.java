package ru.ifmo.utilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MatrixUtilities {
	public static double[] getColumn(int[][] matrix, int colNum) {
		double[] column = new double[matrix.length];
		for (int row = 0; row < column.length; row++) {
			column[row] = matrix[row][colNum];
		}
		return column;
	}

	public static double[][] getProbabilityDistributions(int[][] features,
			int firstFeature, int secondFeature) {
		Map<Integer, Integer> featuresValuesMap = new HashMap<Integer, Integer>();
		for (int instance = 0; instance < features.length; instance++) {
			if (!featuresValuesMap
					.containsKey(features[instance][firstFeature])) {
				featuresValuesMap.put(features[instance][firstFeature],
						featuresValuesMap.size());
			}
			if (!featuresValuesMap
					.containsKey(features[instance][secondFeature])) {
				featuresValuesMap.put(features[instance][secondFeature],
						featuresValuesMap.size());
			}
		}
		double[][] probabilityDistributions = new double[2][featuresValuesMap
				.size()];
		for (int instance = 0; instance < features.length; instance++) {
			probabilityDistributions[0][featuresValuesMap
					.get(features[instance][firstFeature])]++;
			probabilityDistributions[1][featuresValuesMap
					.get(features[instance][secondFeature])]++;
		}
		for (int instance = 0; instance < features.length; instance++) {
			probabilityDistributions[0][featuresValuesMap
					.get(features[instance][firstFeature])] /= features.length;
			probabilityDistributions[1][featuresValuesMap
					.get(features[instance][secondFeature])] /= features.length;
		}
		return probabilityDistributions;
	}

	public static void createWekaMatrix(File featuresFile, File classesFile,
			File out) throws IOException {
		int[][] features = FileUtilities.loadIntMatrix(featuresFile);
		int[] classes = FileUtilities.loadIntVectorFromFileLines(classesFile);
		FileWriter writer = new FileWriter(out);
		writer.write("0");
		for (int i = 1; i <= features[0].length; i++) {// features name
			writer.write("," + i);
		}
		writer.write("\n");
		
		writer.write("\"c0\"");
		for (int j = 0; j < features[0].length; j++) {
			writer.write(",?");
		}
		writer.write("\n");
		writer.write("\"c1\"");
		for (int j = 0; j < features[0].length; j++) {
			writer.write(",?");
		}
		writer.write("\n");
		for (int i = 0; i < features.length; i++) {
			writer.write("\"c" + classes[i]+"\"");
			for (int j = 0; j < features[i].length; j++) {
				writer.write("," + features[i][j]);
			}
			writer.write("\n");	
		}
		writer.close();
	}
}
