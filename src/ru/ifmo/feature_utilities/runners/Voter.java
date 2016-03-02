package ru.ifmo.feature_utilities.runners;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import ru.ifmo.feature_utilities.feature_number_selection.KSelector;

import ru.ifmo.feature_utilities.importance.LoggerFactory;
import ru.ifmo.feature_utilities.voting.Borda;
import ru.ifmo.feature_utilities.voting.LinearCombination;
import ru.ifmo.feature_utilities.voting.Condorcet;
import ru.ifmo.feature_utilities.voting.Markov1and2;
import ru.ifmo.feature_utilities.voting.NDS;
import ru.ifmo.feature_utilities.voting.PCABased;

public class Voter {

	public static final String BORDA = "Borda";
	public static final String CONDORCET = "Condorcet";
	public static final String MARKOV = "Markov";
	public static final String PCA_BASED = "PCA_based";
	public static final String NDS= "NDS";
	public static final String WPCA= "WPCA";
	public static final String LINEARCOMBINATION= "LINEARCOMBINATION";

	public static void start(String[] args) throws IOException {

		LoggerFactory.getInstance().println("Creating classifier tests");

		if (args.length != 4 && args.length != 5) {
			LoggerFactory.getInstance().println("Wrong num of params: in_folder, num_of_merge, out_folder, method, (num_of_features)");
		}

		LoggerFactory.getInstance().println("Starting " + args[1] + " " + args[3]);

		File f = new File(args[0]);

		int combinationCount = Integer.parseInt(args[1]);
		String folder = f.getAbsolutePath();
		int maxCount = 0;

		if (args.length == 4) {
			KSelector selector = new KSelector();
			maxCount = selector.selectNumOfFeatures(f);
		} else {
			maxCount = Integer.parseInt(args[3]);
		}

		if (!f.isDirectory()) {
			LoggerFactory.getInstance().println(folder + " must be a directory!");
		}

		int sortedFiles = 0;

		for (File file : f.listFiles()) {
			if (file.getName().contains("sorted")) {
				sortedFiles++;
			}
		}

		File[] files = new File[sortedFiles];//ArrayList?

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
			createNum(combinationCount, maxCount, files, newFolder, args[3], false);
		} else {
			createNum(combinationCount, maxCount, files, newFolder, args[3], true);
		}

		LoggerFactory.getInstance().println("Classifier tests created");
	}

	private static void createNum(int combinationCount, int maxCount, File[] files, String newFolder, String method, boolean autoNum) throws FileNotFoundException, IOException {//TODO rename?
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
			outName.append("_" + method);
			if (autoNum) {
				outName.append("_");
				outName.append(maxCount);
			}

			LoggerFactory.getInstance().println("Start voting");

			if (method.equals(BORDA)) {
				Borda m = new Borda();
				m.vote(setFiles, outName.toString(), maxCount);
			}
			if (method.equals(CONDORCET)) {
				Condorcet m = new Condorcet();
				m.vote(setFiles, outName.toString(), maxCount);
			}
			if (method.equals(PCA_BASED)) {
				PCABased m = new PCABased();
				m.vote(setFiles, outName.toString(), maxCount);
			}
			if (method.equals(MARKOV)) {
				Markov1and2 m = new Markov1and2();
				m.vote(setFiles, outName.toString(), maxCount);
			}
			if (method.equals(NDS)) {
				NDS m = new NDS();
				m.vote(setFiles, outName.toString(), maxCount);
			}
			if (method.equals(WPCA)) {
				//TODO insert WPCA
				NDS m = new NDS();
				m.vote(setFiles, outName.toString(), maxCount);
			}
			if (method.equals(LINEARCOMBINATION)) {
				//TODO insert WPCA
				LinearCombination m = new LinearCombination();
				m.vote(setFiles, outName.toString(), maxCount);
			}

		}
	}
}
