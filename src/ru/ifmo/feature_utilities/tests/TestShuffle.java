package ru.ifmo.feature_utilities.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import ru.ifmo.feature_utilities.importance.LoggerFactory;

public class TestShuffle {//may be don't need
	public static void start(String[] args) throws IOException {
		File folder = new File(args[0]);
		if (!folder.isDirectory()) {
			throw new IOException(args[0] + " must be a directory!");
		}
		int tests = Integer.parseInt(args[1]);
		if (tests < 1) {
			throw new IOException(args[1] + " must be > 0");
		}
		int percent = Integer.parseInt(args[2]);
		if ((percent < 1) || (percent > 99)) {
			throw new IOException(args[2] + " must be > 0 and < 99");
		}

		LoggerFactory.getInstance().println("Creating training and test sets");

		ArrayList<File> files = new ArrayList<File>();
		files.addAll(Arrays.asList(folder.listFiles()));

		double step = (double) 100 / files.size();

		for (int i = 0; i < tests; i++) {
			LoggerFactory.getInstance().println("Step " + (i + 1) + "/" + tests);
			Collections.shuffle(files);
			File train = new File(folder.getAbsolutePath() + "/tests/test" + (i + 1) + "/trainSet/");
			train.mkdirs();

			File test = new File(folder.getAbsolutePath() + "/tests/test" + (i + 1) + "/testSet/");
			test.mkdirs();

			double currentStep = 0;
			for (File file : files) {
				if (percent < currentStep) {
					File newFile = new File(train.getAbsolutePath() + "/" + file.getName());
					copyFile(file, newFile);
				} else {
					File newFile = new File(test.getAbsolutePath() + "/" + file.getName());
					copyFile(file, newFile);
				}
				currentStep += step;
			}
		}
		LoggerFactory.getInstance().println("done!");
	}

	public static void copyFile(File sourceFile, File destFile) throws IOException {
		if (!destFile.exists()) {
			destFile.createNewFile();
		}

		FileChannel source = null;
		FileChannel destination = null;

		try {
			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile).getChannel();
			destination.transferFrom(source, 0, source.size());
		} finally {
			if (source != null) {
				source.close();
			}
			if (destination != null) {
				destination.close();
			}
		}
	}
}
