package ru.ifmo.feature_utilities.runners;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import ru.ifmo.feature_utilities.Starter;


public class ExperimentsPreparer {
	public static void start(String[] args) throws IOException {
		if (args.length != 3) {
			System.err
					.println("ERROR. Args: matrix_file_path, output_dir, num_of_exp");
			return;
		}
		prepare(args[1]);
		int[][] matrix = loadMatrix(new File(args[0]));
		
		// TODO rewrite?
		List<Integer> randomOrder = new ArrayList<Integer>();
		for (int i = 0; i < matrix.length; i++) {
			randomOrder.add(i);
		}

		for (int i = 1; i <= Integer.parseInt(args[2]); i++) {
			Collections.shuffle(randomOrder);
			craeteTest(args[1], i, matrix, randomOrder);
		}

	}

	private static void prepare(String dirPath) {
		File outDir = new File(dirPath);
		if (!outDir.exists() || !outDir.isDirectory()) {
			outDir.mkdirs();
		}
	}

	private static int[][] loadMatrix(File in) throws IOException {
		int numOfExamples = getNumOfStringsInFile(in);
		BufferedReader br = new BufferedReader(new FileReader(in));
		String line = br.readLine();
		int[][] matrix = new int[numOfExamples][stringToVector(line).length];
		int curExample = 0;
		while (line != null) {
			matrix[curExample] = stringToVector(line);
			line = br.readLine();
			curExample++;
		}
		br.close();
		return matrix;
	}

	private static int getNumOfStringsInFile(File in) throws IOException {// stupid?
		int result = 0;
		BufferedReader br = new BufferedReader(new FileReader(in));
		while (br.readLine() != null) {
			result++;
		}
		br.close();
		return result;
	}

	private static int[] stringToVector(String line) {
		StringTokenizer st = new StringTokenizer(line);
		int[] vector = new int[st.countTokens()];
		int curPos = 0;
		while (st.hasMoreTokens()) {
			vector[curPos] = Integer.parseInt((st.nextToken()));
			curPos++;
		}
		return vector;
	}

	private static void craeteTest(String dirPath, int testNum, int[][] matrix,
			List<Integer> order) throws IOException {
		File outDir = new File(dirPath + "/" + testNum);
		if (!outDir.exists() || !outDir.isDirectory()) {
			System.err.println("I ll create " + outDir.getPath());
			outDir.mkdirs();
		}
		else{
			System.err.println("NOPE " + outDir.getPath());
		}

		File trainF = new File(dirPath + "/" + testNum 
				+ Starter.TRAINING_FEATURES);
		if (!trainF.exists()) {
			trainF.createNewFile();
		}
		FileWriter trainFW = new FileWriter(trainF);

		File trainC = new File(dirPath + "/" + testNum
				+ Starter.TRAINING_CLASSES);
		if (!trainC.exists()) {
			trainC.createNewFile();
		}
		FileWriter trainCW = new FileWriter(trainC);

		File testF = new File(dirPath + "/" + testNum + Starter.TEST_FEATURES);
		if (!testF.exists()) {
			testF.createNewFile();
		}
		FileWriter testFW = new FileWriter(testF);

		File testC = new File(dirPath + "/" + testNum + Starter.TEST_CLASSES);
		if (!testC.exists()) {
			testC.createNewFile();
		}
		FileWriter testCW = new FileWriter(testC);

		int cur = 0;
		int trainSize = (int) (matrix.length * 0.6);
		for (Integer i : order) {
			if (cur < trainSize) {
				trainCW.write(matrix[i][0] + "\n");
				for (int j = 1; j < matrix[i].length - 1; j++) {
					trainFW.write(matrix[i][j] + " ");
				}
				trainFW.write(matrix[i][matrix[i].length - 1] + "\n");
			} else {
				testCW.write(matrix[i][0] + "\n");
				for (int j = 1; j < matrix[i].length - 1; j++) {
					testFW.write(matrix[i][j] + " ");
				}
				testFW.write(matrix[i][matrix[i].length - 1] + "\n");
			}
			cur++;
		}
		trainFW.close();
		trainCW.close();
		testFW.close();
		testCW.close();
	}
}
