package ru.ifmo.feature_utilities.classifiers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import ru.ifmo.utilities.FileUtilities;
import ru.ifmo.utilities.StringUtilities;
import weka.classifiers.lazy.IBk;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class KNN implements IClassifier {

	private IBk kNN;

	@Override
	public void teachClassifier(File in) {
		try {
			DataSource source;
			source = new DataSource(in.getAbsolutePath());

			Instances data = source.getDataSet();
			data.setClassIndex(0);

			kNN = new IBk();
			kNN.setCrossValidate(true);
			kNN.buildClassifier(data);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void loadClassifier(File in) {
		try {
			kNN = (IBk) weka.core.SerializationHelper
					.read(in.getAbsolutePath());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void writeClassifier(File out) {
		try {
			weka.core.SerializationHelper.write(out.getAbsolutePath(),
					kNN);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void classify(File in, File out) throws IOException {
		int numOfExamples = FileUtilities.getNumOfStringsInFile(in);
		BufferedReader br = new BufferedReader(new FileReader(in));
		String line = br.readLine();
		int[][] matrix = new int[numOfExamples][StringUtilities
				.stringToIntVector(line).length];
		int curExample = 0;
		while (line != null) {
			matrix[curExample] = StringUtilities.stringToIntVector(line);
			line = br.readLine();
			curExample++;
		}
		FileUtilities.printDoubleArrToFile(classify(matrix), out);
		br.close();

	}

	@Override
	public double classify(int[] vector) {
		double res = 0;
		// creatre attrs
		FastVector atts = new FastVector();
		atts.addElement(new Attribute("0"));
		for (int i = 1; i < vector.length + 1; i++) {

			atts.addElement(new Attribute(Integer.toString(i)));
		}

		Instances dataUnlabeled = new Instances("Test", atts, 0);
		dataUnlabeled.setClassIndex(0);

		Instance inst = new Instance(vector.length + 1);
		inst.setDataset(dataUnlabeled);

		inst.setMissing(0);
		for (int i = 1; i < vector.length + 1; i++) {
			inst.setValue(i, vector[i - 1]);
		}

		try {
			res = kNN.classifyInstance(inst);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 1 - res;
	}

	@Override
	public double[] classify(int[][] data) {
		double[] result = new double[data.length];
		for (int i = 0; i < data.length; i++) {
			result[i] = classify(data[i]);
		}
		return result;
	}

}
