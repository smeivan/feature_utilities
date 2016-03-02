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

public class WekaTransformMult2 {
	public static void main(String[] args) throws IOException {
		args = new String[] { "e:\\24.09.09\\feature_utilities\\datasets\\Kovanikov\\real\\" };

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

			for (int classIndex = 0; classIndex < classesAmount; ++classIndex) {
				for (int featureNum = 1; featureNum < numOfFeatures; featureNum++) {
					int hashIndex = 0;
					HashMap<String, Integer> values = new HashMap<String, Integer>();
					for (strNum = 0; strNum < numOfStrings; strNum++) {
						String currentString = inputData[strNum][featureNum];
						if (values.containsKey(currentString)) {
							currentString = values.get(currentString).toString();
						} else {
							values.put(currentString, hashIndex);
							currentString = Integer.toString(hashIndex);
							++hashIndex;
						}
						inputData[strNum][featureNum] = currentString;
					}
				}

				PrintWriter pw = new PrintWriter(outFolder.getAbsolutePath() + "\\" + convertedFile.getName()+"_"+classesArray[classIndex]);
				System.err.println(convertedFile.getName()+"_"+classesArray[classIndex]);
				for (strNum = 0; strNum < numOfStrings; strNum++) {
					if (inputData[strNum][0].equals((String)(classesArray[classIndex]))){
						pw.print("0 ");
					}else{
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
