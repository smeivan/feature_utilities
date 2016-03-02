package ru.ifmo.feature_utilities.runners;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.ifmo.feature_utilities.Starter;
import ru.ifmo.utilities.FileUtilities;

public class NewMatrixFromFeaturesCreator {
	public static void start(String[] args) throws IOException {
		if (args.length != 4) {
			System.err
					.println("ERROR. Args: matrix_file_path_tr, matrix_file_path_te ,features_path, out");
			return;
		}
		createNewMatrix(loadList(new File(args[2])), FileUtilities.loadIntMatrix(new File(
				args[0])), FileUtilities.loadIntMatrix(new File(args[1])), args[3]);

	}

	private static void createNewMatrix(List<Integer> features,
			int[][] matrixTr, int[][] matrixTe, String outDir)
			throws IOException {
		int[][] newMatrixTraining = new int[matrixTr.length][features.size()];
		int[][] newMatrixTest = new int[matrixTe.length][features.size()];
		int total = 0;
		for (Integer i : features) {
			for (int j = 0; j < matrixTr.length; j++) {
				newMatrixTraining[j][total] = matrixTr[j][i];
			}
			for (int j = 0; j < matrixTe.length; j++) {
				newMatrixTest[j][total] = matrixTe[j][i];
			}
			total++;
		}
		printMatrix(newMatrixTraining, new File(outDir + Starter.TRAINING_FEATURES));
		printMatrix(newMatrixTest, new File(outDir + Starter.TEST_FEATURES));
	}

	private static void printMatrix(int[][] matrix, File out)
			throws IOException {
		FileWriter writer = new FileWriter(out);
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length - 1; j++) {
				writer.write(matrix[i][j] + " ");
			}
			writer.write(matrix[i][matrix[i].length - 1] + "\n");
		}

		writer.close();
	}

	private static List<Integer> loadList(File in) throws IOException {
		List<Integer> features = new ArrayList<Integer>();
		BufferedReader br = new BufferedReader(new FileReader(in));
		String line = br.readLine();
		while (line != null) {
			features.add(Integer.parseInt(line));
			line = br.readLine();
		}
		br.close();
		return features;
	}

}
