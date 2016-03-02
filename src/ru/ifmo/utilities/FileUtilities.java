package ru.ifmo.utilities;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public final class FileUtilities {

	public static void writeString(File file, String s) throws IOException {
		FileWriter fileWritter = new FileWriter(file.getAbsolutePath(), true);
		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		bufferWritter.write(s);
		bufferWritter.close();
	}
	
	public static void copyFolder(File source, File destination)
	{
	    if (source.isDirectory())
	    {
	        if (!destination.exists())
	        {
	            destination.mkdirs();
	        }

	        String files[] = source.list();

	        for (String file : files)
	        {
	            File srcFile = new File(source, file);
	            File destFile = new File(destination, file);

	            copyFolder(srcFile, destFile);
	        }
	    }
	    else
	    {
	        InputStream in = null;
	        OutputStream out = null;

	        try
	        {
	            in = new FileInputStream(source);
	            out = new FileOutputStream(destination);

	            byte[] buffer = new byte[1024];

	            int length;
	            while ((length = in.read(buffer)) > 0)
	            {
	                out.write(buffer, 0, length);
	            }
	            in.close();
	            out.close();
	        }
	        catch (Exception e)
	        {
	            try
	            {
	                in.close();
	            }
	            catch (IOException e1)
	            {
	                e1.printStackTrace();
	            }

	            try
	            {
	                out.close();
	            }
	            catch (IOException e1)
	            {
	                e1.printStackTrace();
	            }
	        }
	    }
	}

	public static void copyFileUsingStream(File source, File dest) throws IOException {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(source);
			os = new FileOutputStream(dest);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
		} finally {
			is.close();
			os.close();
		}
	}

	/**
	 * <p>
	 * Calculate number of lines in file
	 * </p>
	 */
	public static int getNumOfStringsInFile(File in) throws IOException {// stupid?
		String filename = in.getAbsolutePath();
		InputStream is = new BufferedInputStream(new FileInputStream(filename));
		try {
			byte[] c = new byte[1024];
			int count = 0;
			int readChars = 0;
			boolean empty = true;
			while ((readChars = is.read(c)) != -1) {
				empty = false;
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n') {
						++count;
					}
				}
			}
			return (count == 0 && !empty) ? 1 : count;
		} finally {
			is.close();
		}
	}

	/**
	 * <p>
	 * Load double vector (File format: 1 line = 1 double)
	 * </p>
	 */
	public static double[] loadDoubleVectorFromFileLines(File resultsFile) throws IOException {
		double[] vector = new double[getNumOfStringsInFile(resultsFile)];
		BufferedReader br = new BufferedReader(new FileReader(resultsFile));
		String line = br.readLine();
		int curExample = 0;

		while (line != null) {
			vector[curExample] = Double.parseDouble(line);
			line = br.readLine();
			curExample++;
		}
		br.close();
		return vector;
	}

	/**
	 * <p>
	 * Load boolean vector (File format: 1 line = 1 value (true == 1; false ==
	 * not 1))
	 * </p>
	 */
	public static boolean[] loadBooleanVectorFromFileLines(File resultsFile) throws IOException {
		boolean[] vector = new boolean[FileUtilities.getNumOfStringsInFile(resultsFile)];
		BufferedReader br = new BufferedReader(new FileReader(resultsFile));
		String line = br.readLine();
		int curExample = 0;

		while (line != null) {
			vector[curExample] = Integer.parseInt(line) == 1;
			line = br.readLine();
			curExample++;
		}
		br.close();
		return vector;
	}

	/**
	 * <p>
	 * Load int vector (File format: 1 line = 1 int)
	 * </p>
	 */
	public static int[] loadIntVectorFromFileLines(File resultsFile) throws IOException {
		int[] vector = new int[getNumOfStringsInFile(resultsFile)];
		BufferedReader br = new BufferedReader(new FileReader(resultsFile));
		String line = br.readLine();
		int curExample = 0;

		while (line != null) {
			vector[curExample] = Integer.parseInt(line);
			line = br.readLine();
			curExample++;
		}
		br.close();
		return vector;
	}

	/**
	 * <p>
	 * Load int matrix (matrix[num_of_line][num_of_col])
	 * </p>
	 */
	public static int[][] loadIntMatrix(File in) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(in));
		String line = br.readLine();
		int[][] matrix = new int[getNumOfStringsInFile(in)][StringUtilities.stringToIntVector(line).length];
		int curExample = 0;
		while (line != null) {
			matrix[curExample] = StringUtilities.stringToIntVector(line);
			line = br.readLine();
			curExample++;
		}
		br.close();
		return matrix;
	}

	/**
	 * <p>
	 * Load int to str map (File format: str1 int1...)
	 * </p>
	 */
	public static Map<Integer, String> loadIntToStrMap(File in) throws IOException {
		Map<Integer, String> map = new HashMap<Integer, String>();
		BufferedReader br = new BufferedReader(new FileReader(in));
		String line;
		while ((line = br.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line);
			String feature = st.nextToken();
			int id = Integer.parseInt(st.nextToken());
			map.put(id, feature);
		}
		br.close();
		return map;
	}

	/**
	 * <p>
	 * Load List<Double> (File format: 1 line = 1 double)
	 * </p>
	 */
	public static List<Double> loadDoubleListLines(File in) throws IOException {
		List<Double> score = new ArrayList<Double>();
		BufferedReader br = new BufferedReader(new FileReader(in));
		String line;
		while ((line = br.readLine()) != null) {
			score.add(Double.parseDouble(line));
		}
		br.close();
		return score;
	}

	/**
	 * <p>
	 * Load str to double map (File format: str1 double...)
	 * </p>
	 */
	public static Map<String, Double> loadStrToDoubleMap(File in) throws IOException {
		Map<String, Double> map = new HashMap<String, Double>();
		BufferedReader br = new BufferedReader(new FileReader(in));

		String line = br.readLine();
		while (line != null) {
			String[] mapEntry = line.split(" ");
			map.put(mapEntry[0], Double.parseDouble(mapEntry[1]));
			line = br.readLine();
		}
		br.close();
		return map;
	}

	/**
	 * <p>
	 * Load int to double map (File format: int1 double1...)
	 * </p>
	 */
	public static Map<Integer, Double> loadIntToDoubleMap(File in) throws IOException {
		Map<Integer, Double> map = new HashMap<Integer, Double>();
		BufferedReader br = new BufferedReader(new FileReader(in));

		String line = br.readLine();
		while (line != null) {
			String[] mapEntry = line.split(" ");
			map.put(Integer.parseInt(mapEntry[0]), Double.parseDouble(mapEntry[1]));
			line = br.readLine();
		}
		br.close();
		return map;
	}

	/**
	 * <p>
	 * Load int list (File format: int1 *1...)
	 * </p>
	 */
	public static List<Integer> loadIntList(File in) throws IOException {
		List<Integer> map = new ArrayList<Integer>();
		BufferedReader br = new BufferedReader(new FileReader(in));

		String line = br.readLine();
		while (line != null) {
			String[] mapEntry = line.split(" ");
			map.add(Integer.parseInt(mapEntry[0]));
			line = br.readLine();
		}
		br.close();
		return map;
	}

	/**
	 * <p>
	 * remove directory and all contents
	 * </p>
	 */

	public static boolean removeDirectory(File directory) {
		if (directory == null)
			return false;
		if (!directory.exists())
			return true;
		if (!directory.isDirectory())
			return false;

		String[] list = directory.list();

		if (list != null) {
			for (int i = 0; i < list.length; i++) {
				File entry = new File(directory, list[i]);
				if (entry.isDirectory()) {
					if (!removeDirectory(entry))
						return false;
				} else {
					if (!entry.delete())
						return false;
				}
			}
		}

		return directory.delete();
	}

	/**
	 * <p>
	 * print double[] to file
	 * </p>
	 */

	public static void printDoubleArrToFile(double[] res, File out) throws IOException {
		PrintWriter pw = new PrintWriter(new FileWriter(out));
		for (int i = 0; i < res.length; i++) {
			pw.println(res[i]);
		}
		pw.close();
	}

	public static void shuffleLines(File file) throws IOException {
		List<String> lines = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(file.getAbsolutePath()));
		while(br.ready()){
			lines.add(br.readLine());
		}
		br.close();
		Collections.shuffle(lines);
		File out = new File(file.getAbsolutePath()+"_tmp");
		PrintWriter pw = new PrintWriter(out);
		for (String s:lines){
			if (s.isEmpty()){
				continue;
			}
			pw.println(s);
		}
		pw.close();
		file.delete();
		out.renameTo(file);
	}

	/**
	 * <p>
	 * load matrix from different files with maps
	 * </p>
	 */
	public static double[][] fillMatrix(File[] inFiles, int m, int n, int numOfFeatures, Map<Integer, Integer> newToOldIndex) throws FileNotFoundException, IOException {
		Set<Integer> added = new HashSet<Integer>();
		double[][] input = new double[m][n];
		int totalNewFeatures = 0;

		for (int i = 0; i < n; i++) {
			BufferedReader br = new BufferedReader(new FileReader(inFiles[i]));
			for (int j = 0; j < m; j++) {
				StringTokenizer st = new StringTokenizer(br.readLine());
				int fNubmer = Integer.parseInt(st.nextToken());
				double mValue = Math.abs(Double.parseDouble(st.nextToken()));
				input[fNubmer][i] = mValue;
				if (j < numOfFeatures) {
					if (!added.contains(fNubmer)) {
						newToOldIndex.put(totalNewFeatures, fNubmer);
						added.add(fNubmer);
						totalNewFeatures++;
					}
				}
			}
			br.close();
		}
		double[][] newInput = new double[totalNewFeatures][n];
		for (int j = 0; j < totalNewFeatures; j++) {
			for (int i = 0; i < n; i++) {
				newInput[j][i] = input[newToOldIndex.get(j)][i];
			}
		}
		return newInput;
	}
}
