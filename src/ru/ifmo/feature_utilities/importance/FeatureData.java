package ru.ifmo.feature_utilities.importance;

import java.io.File;
import java.io.IOException;

import ru.ifmo.utilities.FileUtilities;

public class FeatureData {

	private final int[][] featureMatrix; // first index - example number; second
											// index - feature number;
	private final int[] classVector; // index - example number;

	public FeatureData(File matrixFile, File vectorFile) throws IOException {
		featureMatrix = FileUtilities.loadIntMatrix(matrixFile);
		classVector = FileUtilities.loadIntVectorFromFileLines(vectorFile);
	}

	public int[][] getFeatureMatrix() {
		return featureMatrix;
	}

	public int[] getClassVector() {
		return classVector;
	}

}
