package weka_transformers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

public class WekaTransformMult {

	public static void main(String[] args) throws IOException {
//		args = new String[] { "e:\\24.09.09\\feature_utilities\\datasets\\Kovanikov\\integer\\multiclass\\" };
		
		args = new String[] { "e:\\24.09.09\\feature_utilities\\datasets\\new\\tumors\\raw\\" };
		File inFolder = new File(args[0]);
		File outFolder = new File(inFolder.getAbsolutePath() + "\\rebuilded_mult1\\");
		outFolder.mkdirs();

		for (File wekaFile : inFolder.listFiles()) {
			if (wekaFile.isDirectory()) {
				continue;
			}
			BufferedReader br = new BufferedReader(new FileReader(wekaFile));
			PrintWriter pw = new PrintWriter(outFolder.getAbsolutePath() + "\\" + wekaFile.getName());

			int line = 0;
			int atas = 0;
			boolean classAtTheEnd = false;
			String classString = "";
			while (br.ready()) {
				++line;
				String str = br.readLine();
				if (str.isEmpty()) {
					continue;
				}
				if (str.charAt(0) == '%') {
					continue;
				}
				if (str.charAt(0) == '@') {
					if ((str.charAt(1) == 'a')||(str.charAt(1) == 'A')) {
						++atas;
						if (str.contains(" class ")) {
							if (atas > 1) {
								classAtTheEnd = true;
							}
						}
						
						if (str.contains(" CLASS ")) {
							if (atas > 1) {
								classAtTheEnd = true;
							}
						}
						
						if (str.contains("'class'")) {
							if (atas > 1) {
								classAtTheEnd = true;
							}
						}
					}
					continue;
				}

				StringTokenizer st = new StringTokenizer(str, ",");
				int size = st.countTokens();
				String[] outArray = new String[size];
				
				for (int i = 0; i < size; i++) {
					outArray[i] = st.nextToken();
				}
				
				if (classAtTheEnd) {
					pw.print(outArray[size - 1] + ",");
					for (int i = 0; i < size - 1; i++) {
						pw.print(outArray[i]);
						if (i!=size-2)
							pw.print(",");
					}
				} else {
					for (int i = 0; i < size; i++) {
						pw.print(outArray[i]);
						if (i != (size - 1))
							pw.print(",");
					}
				}
				if (br.ready())
					pw.println();
			}
			System.err.println(wekaFile.getName() + " " + classAtTheEnd);
			br.close();
			pw.close();
		}
	}

}
