package ru.ifmo.feature_utilities.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import ru.ifmo.feature_utilities.feature_number_selection.KSelector;
import ru.ifmo.feature_utilities.importance.LoggerFactory;

public class SelectFeatures {//BGF only? TODO Ivan
	public static void start(String[] args) throws IOException {
		//TODO: refactor - move to voting, implementing interface 
		LoggerFactory.getInstance().println("Creating classifier tests");

		if (args.length != 3 && args.length != 4) {
			LoggerFactory
					.getInstance()
					.println(
							"Wrong num of params: in_folder, num_of_merge, out_folder, (num_of_features)");
		}
		
		LoggerFactory.getInstance().println("Starting " + args[1] + " BestGoFirst");

		File f = new File(args[0]);

		int combinationCount = Integer.parseInt(args[1]);
		String folder = f.getAbsolutePath();
		int maxCount = 0;

		if (args.length == 3) {
			KSelector selector = new KSelector();
			maxCount = selector.selectNumOfFeatures(f);
		} else {
			maxCount = Integer.parseInt(args[3]);
		}

		if (!f.isDirectory()) {
			LoggerFactory.getInstance().println(
					folder + " must be a directory!");
		}

		int sortedFiles = 0;

		for (File file : f.listFiles()) {
			if (file.getName().contains("sorted")) {
				sortedFiles++;
			}
		}

		File[] files = new File[sortedFiles];

		int i = 0;
		for (File file : f.listFiles()) {
			if (file.getName().contains("sorted")) {
				files[i] = file;
				i++;
			}
		}

		String newFolder = args[2];
		File fol = new File(newFolder);
		fol.mkdir();

		if (args.length == 3) {
			createNum(combinationCount, maxCount, files, newFolder, false);
		} else {
			createNum(combinationCount, maxCount, files, newFolder, true);
		}

		LoggerFactory.getInstance().println("Classifier tests created");
	}

	private static void createNum(int combinationCount, int maxCount,
			File[] files, String newFolder, boolean autoNum)
			throws FileNotFoundException, IOException {
		LoggerFactory.getInstance().println(
				"Creating " + combinationCount + "tests");
		int maxI = (int) Math.pow(2, files.length);
		for (int i = 0; i < maxI; i++) {
			if (Integer.bitCount(i) != combinationCount) {
				continue;
			}
			int tmp = i;
			File[] setFiles = new File[combinationCount];
			int currentFile = 0;
			for (int k = 0; k < files.length; k++) {
				int tmpOld = tmp;
				int takeFile = tmpOld - ((tmp >> 1) << 1);
				if (takeFile == 1) {
					setFiles[currentFile] = files[k];
					++currentFile;
				}
				tmp = (tmp >> 1);
			}

			BufferedReader[] readers = new BufferedReader[combinationCount];
			StringBuilder outName = new StringBuilder(newFolder);
			for (int j = 0; j < combinationCount; j++) {
				readers[j] = new BufferedReader(new FileReader(setFiles[j]));
				if (j != 0) {
					outName.append("_");
				}
				outName.append(setFiles[j].getName().substring(7));
			}
			outName.append("_BGF");
			if (autoNum) {
				outName.append("_");
				outName.append(maxCount);
			}

			File out = new File(outName.toString());
			out.createNewFile();
			PrintWriter pw = new PrintWriter(out);
			Map<String, Integer> features = new HashMap<String, Integer>();
			whileExit: while (features.size() < maxCount) {

				for (int j = 0; j < combinationCount; j++) {
					if (!readers[j].ready()) {
						break whileExit;
					}
				}

				for (int j = 0; j < combinationCount; j++) {
					StringTokenizer st = new StringTokenizer(
							readers[j].readLine());
					String s = st.nextToken();
					if (!features.containsKey(s)) {
						features.put(s, 1);
						pw.println(s);
						if (features.size() == maxCount) {
							break;
						}
					}
				}
			}
			for (int j = 0; j < combinationCount; j++) {
				readers[j].close();
			}
			pw.close();
		}
	}
}
