package ru.ifmo.feature_utilities.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import ru.ifmo.feature_utilities.importance.LoggerFactory;

public class CreateDatasetFromSparse {
	public static void start(String[] args) throws IOException {
		//args = new String[] { "datasets/Dorothea", "binary", "100000" }; example
		LoggerFactory.getInstance().println("Creating normal matrix from sparse matrix");
		if (args[1].compareTo("integer") != 0) {
			if (args[1].compareTo("binary") != 0) {
				LoggerFactory.getInstance().println("Wrong matrix type: " + args[1] + ", must be integer or binary");
				return;
			}
		}

		int featuresNum = Integer.parseInt(args[2]);

		LoggerFactory.getInstance().println("Matrix type: " + args[1]);

		if (args[1].compareTo("binary") == 0) {
			buildFromBinarySparse(args[0], featuresNum);
		} else if (args[1].compareTo("integer") == 0) {
			buildFromIntegerSparse(args[0], featuresNum);
		}

		LoggerFactory.getInstance().println("Full matrix created");
	}

	public static void buildFromBinarySparse(String folderPath, int featuresNum) throws IOException {
		File f = new File(folderPath);
		String folder = f.getCanonicalPath();
		if (!f.isDirectory()) {
			LoggerFactory.getInstance().println(folder + " must be a directory!");
		}

		File[] files = f.listFiles();

		PrintWriter pw = new PrintWriter(folderPath + "\\" + f.getName());

		for (File file : files) {
			if (file.getName().contains("labels")) {
				String fileSubName = file.getName().replace(".labels", "");
				BufferedReader data = new BufferedReader(new FileReader(folderPath + "\\" + fileSubName + ".data"));
				BufferedReader labels = new BufferedReader(new FileReader(folderPath + "\\" + fileSubName + ".labels"));
				while (labels.ready()) {
					StringTokenizer labelSt = new StringTokenizer(labels.readLine());
					StringTokenizer st = new StringTokenizer(data.readLine());
					int tokens = st.countTokens();
					int[] indexes = new int[tokens + 1];
					for (int i = 0; i < tokens; i++) {
						indexes[i] = Integer.parseInt(st.nextToken());
					}
					indexes[tokens] = featuresNum + 1;
					pw.print((labelSt.nextToken().compareTo("-1") == 0 ? 0 : 1) + " ");
					int j = 0;
					for (int i = 0; i < featuresNum; i++) {
						if (i == indexes[j] - 1) {
							pw.print("1 ");
							j++;
						} else {
							pw.print("0 ");
						}
					}
					pw.println();
				}
				data.close();
				labels.close();
			}
		}

		pw.close();
	}

	public static void buildFromIntegerSparse(String folderPath, int featuresNum) throws IOException {
		File f = new File(folderPath);
		String folder = f.getAbsolutePath();
		if (!f.isDirectory()) {
			LoggerFactory.getInstance().println(folder + " must be a directory!");
		}

		File[] files = f.listFiles();

		PrintWriter pw = new PrintWriter(folderPath + "\\" + f.getName());

		for (File file : files) {
			if (file.getName().contains("labels")) {
				String fileSubName = file.getName().replace(".labels", "");
				BufferedReader data = new BufferedReader(new FileReader(folderPath + "\\" + fileSubName + ".data"));
				BufferedReader labels = new BufferedReader(new FileReader(folderPath + "\\" + fileSubName + ".labels"));
				while (labels.ready()) {
					StringTokenizer labelSt = new StringTokenizer(labels.readLine());
					StringTokenizer st = new StringTokenizer(data.readLine());
					int tokens = st.countTokens() / 2;
					int[] indexes = new int[tokens + 1];
					int[] values = new int[tokens + 1];
					for (int i = 0; i < tokens; i++) {
						indexes[i] = Integer.parseInt(st.nextToken());
						values[i] = Integer.parseInt(st.nextToken());
					}
					indexes[tokens] = featuresNum + 1;
					values[tokens] = -1;
					pw.print((labelSt.nextToken().compareTo("-1") == 0 ? 0 : 1) + " ");
					int j = 0;
					for (int i = 0; i < featuresNum; i++) {
						if (i == indexes[j] - 1) {
							pw.print(values[j] + " ");
							j++;
						} else {
							pw.print("0 ");
						}
					}
					pw.println();
				}
				data.close();
				labels.close();
			}
		}

		pw.close();
	}

}
