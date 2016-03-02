package ru.ifmo.feature_utilities.classifiers;

import java.io.File;
import java.io.IOException;

public interface IClassifier {
	public void teachClassifier(File in);
	
	public void loadClassifier(File in);
	
	public void writeClassifier(File out);

	public void classify(File in, File out) throws IOException;

	public double classify(int[] vector);

	public double[] classify(int[][] data);
}
