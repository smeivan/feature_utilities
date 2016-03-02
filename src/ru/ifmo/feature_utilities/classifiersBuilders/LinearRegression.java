package ru.ifmo.feature_utilities.classifiersBuilders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import Jama.Matrix;

public class LinearRegression {

	private Matrix matrix = null;
	private Matrix vector = null;
	private Matrix thetas = null;

	public LinearRegression(String folderName) throws IOException {//TODO Ivan
		File file = new File(folderName);
		if (!file.isDirectory()) {
			throw new IOException("Is not directory");
		}
		File x = null;
		File y = null;
		for (File innerFile : file.listFiles()) {
			if (innerFile.getName().contains("_training_features")) {// "_training_features"))
																		// {
				x = innerFile;
			}
			if (innerFile.getName().contains("_training_classes")) {// "_training_classes"))
																	// {
				y = innerFile;
			}
		}

		BufferedReader reader = new BufferedReader(new FileReader(x));
		StringTokenizer st = new StringTokenizer(reader.readLine());
		int colomns = 0;
		while (st.hasMoreTokens()) {
			st.nextToken();
			colomns++;
		}
		colomns++;
		reader.close();

		reader = new BufferedReader(new FileReader(y));
		int lines = 0;
		while (reader.readLine() != null) {
			lines++;
		}
		reader.close();

		double[][] preMatrix = new double[lines][colomns];
		double[] preVector = new double[lines];
		thetas = new Matrix(colomns, 1);
		for (int i = 0; i < lines; i++) {
			preMatrix[i][0] = 1;
		}

		reader = new BufferedReader(new FileReader(x));
		for (int i = 0; i < lines; i++) {
			st = new StringTokenizer(reader.readLine());
			for (int j = 1; j < colomns; j++) {
				preMatrix[i][j] = Double.parseDouble(st.nextToken());
			}
		}
		reader.close();

		reader = new BufferedReader(new FileReader(y));
		for (int i = 0; i < lines; i++) {
			st = new StringTokenizer(reader.readLine());
			preVector[i] = Double.parseDouble(st.nextToken());
		}
		reader.close();

		matrix = new Matrix(preMatrix);
		vector = new Matrix(preVector, 1);
	}

	public void calculate(double lambda, String outFile) throws IOException {
		ShitCalculator calculator = new ShitCalculator(thetas, matrix, vector, lambda);
		DescentMethod method = new DescentMethod(calculator);
		double[][] result = method.calculate().getArray();
		PrintWriter pw = new PrintWriter(outFile);
		for (int i = 0; i < result.length; i++) {
			pw.println(result[i][0]);
		}
		pw.close();
	}

}
