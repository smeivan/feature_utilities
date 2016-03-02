package weka_transformers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.StringTokenizer;

import ru.ifmo.utilities.FileUtilities;

public class WekaTransform2 {
	public static void main(String[] args) throws IOException {
		args = new String[] { "e:\\24.09.09\\feature_utilities\\datasets\\Kovanikov\\integer\\" };

		File inFolder = new File(args[0] + "\\rebuilded1\\");
		File outFolder = new File(args[0] + "\\rebuilded2\\");
		outFolder.mkdirs();

		for (File convertedFile : inFolder.listFiles()) {
			if (convertedFile.isDirectory()) {
				continue;
			}
			BufferedReader br = new BufferedReader(new FileReader(convertedFile));
			StringTokenizer st = new StringTokenizer(br.readLine(),",");

			int numOfFeatures = st.countTokens();
			int numOfStrings = FileUtilities.getNumOfStringsInFile(convertedFile)+1;

			String[][] inputData = new String[numOfStrings][numOfFeatures];

			br.close();
			br = new BufferedReader(new FileReader(convertedFile));

			int strNum = 0;
			while (br.ready()) {
				int featureNum = 0;
				st = new StringTokenizer(br.readLine(),",");
				while (st.hasMoreTokens()) {
					inputData[strNum][featureNum] = st.nextToken();
					featureNum++;
				}
				strNum++;
			}
			br.close();
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

			PrintWriter pw = new PrintWriter(outFolder.getAbsolutePath() + "\\" + convertedFile.getName());
			for (strNum = 0; strNum < numOfStrings; strNum++) {
				for (int featureNum = 0; featureNum < numOfFeatures; featureNum++) {
					pw.print(inputData[strNum][featureNum]+" ");
				}
				if (strNum!=(numOfStrings-1)){
					pw.println();
				}
			}
			pw.close();
		}
	}
}
