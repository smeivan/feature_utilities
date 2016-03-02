package weka_transformers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;

import ru.ifmo.utilities.FileUtilities;

public class WekaTransformReal2 {
	public static void main(String[] args) throws IOException {
		args = new String[] { "e:\\24.09.09\\feature_utilities\\datasets\\new\\tumors\\" };

		File inFolder = new File(args[0] + "\\rebuilded_mult1\\");
		File outFolder = new File(args[0] + "\\rebuilded_mult2\\");
		outFolder.mkdirs();

		for (File convertedFile : inFolder.listFiles()) {
			if (convertedFile.isDirectory()) {
				continue;
			}
			BufferedReader br = new BufferedReader(new FileReader(convertedFile));
			StringTokenizer st = new StringTokenizer(br.readLine(), ",");

			int numOfFeatures = st.countTokens();
			int numOfStrings = FileUtilities.getNumOfStringsInFile(convertedFile) + 1;

			String[][] inputData = new String[numOfStrings][numOfFeatures];

			br.close();
			br = new BufferedReader(new FileReader(convertedFile));

			int strNum = 0;
			while (br.ready()) {
				int featureNum = 0;
				st = new StringTokenizer(br.readLine(), ",");
				while (st.hasMoreTokens()) {
					inputData[strNum][featureNum] = st.nextToken();
					featureNum++;
				}
				strNum++;
			}
			br.close();

			HashSet<String> classes = new HashSet<String>();
			for (int i = 0; i < numOfStrings; i++) {
				if (!classes.contains(inputData[i][0])) {
					classes.add(inputData[i][0]);
				}
			}
			int classesAmount = classes.size();
			Object[] classesArray = classes.toArray();

			double[] minValues = new double[numOfFeatures];
			double[] maxValues = new double[numOfFeatures];
			
			for (int featureNum = 1; featureNum < numOfFeatures; featureNum++) {
				double minValue = Double.MAX_VALUE;
				double maxValue = Double.MIN_VALUE;
				for (strNum = 0; strNum < numOfStrings; strNum++) {
					double value = Double.parseDouble(inputData[strNum][featureNum]);
					if (value<minValue){
						minValue = value;
					}
					if (value>maxValue){
						maxValue = value;
					}
				}
				minValues[featureNum] = minValue;
				maxValues[featureNum] = maxValue;
			}
			
			for (int featureNum = 1; featureNum < numOfFeatures; featureNum++) {
				double valueIndexer = (maxValues[featureNum] - minValues[featureNum])/10;
				for (strNum = 0; strNum < numOfStrings; strNum++) {
					double currentValue = Double.parseDouble(inputData[strNum][featureNum]);
					inputData[strNum][featureNum] = Integer.toString((int)((currentValue-minValues[featureNum])/valueIndexer));
				}
			}

			for (int classIndex = 0; classIndex < classesAmount; ++classIndex) {
				PrintWriter pw = new PrintWriter(outFolder.getAbsolutePath() + "\\" + convertedFile.getName() + "_" + classesArray[classIndex]);
				System.err.println(convertedFile.getName() + "_" + classesArray[classIndex]);
				for (strNum = 0; strNum < numOfStrings; strNum++) {
					if (inputData[strNum][0].equals((String) (classesArray[classIndex]))) {
						pw.print("0 ");
					} else {
						pw.print("1 ");
					}

					for (int featureNum = 1; featureNum < numOfFeatures; featureNum++) {
						pw.print(inputData[strNum][featureNum] + " ");
					}
					if (strNum != (numOfStrings - 1)) {
						pw.println();
					}
				}
				pw.close();

			}
		}
	}
}
