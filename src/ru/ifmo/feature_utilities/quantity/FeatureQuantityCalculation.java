package ru.ifmo.feature_utilities.quantity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class FeatureQuantityCalculation {//TODO ivan
	public static void main(String[] args) throws IOException {
		File file = new File(args[0]);
		if (!file.isDirectory()) {
			System.out.println(args[0] + " must be a directory!");
			return;
		}
		int maxCounter = Integer.parseInt(args[1]);

		Map<String, Integer> map = new HashMap<String, Integer>();

		int maxFiles = 1;
		int counter = 0;
		for (String innerFileName : file.list()) {
			if (innerFileName.contains("sorted")) {
				BufferedReader br = new BufferedReader(new FileReader(file.getAbsolutePath() + "//" + innerFileName));
				while (br.ready()) {
					++counter;
					if (counter > maxCounter) {
						counter = 0;
						break;
					}
					StringTokenizer st = new StringTokenizer(br.readLine());
					String s = st.nextToken();
					if (map.containsKey(s)) {
						Integer count = map.get(s);
						count++;
						map.remove(s);
						map.put(s, count);
						if (count > maxFiles) {
							maxFiles++;
						}
					} else {
						map.put(s, 1);
					}
				}
				br.close();
			}
		}

		int[] totalCounts = new int[maxFiles + 1];

		for (String name : map.keySet()) {
			totalCounts[map.get(name)]++;
		}

		PrintWriter pw = new PrintWriter(file.getAbsolutePath() + "//CounterResults_" + maxCounter);
		for (int i = 0; i < totalCounts.length; ++i) {
			pw.println(i + ": " + totalCounts[i]);
		}
		
		PrintWriter pwAll = new PrintWriter(file.getAbsolutePath() + "//OccuredIn" + (maxFiles));
		PrintWriter pwAllMinusOne = new PrintWriter(file.getAbsolutePath() + "//OccuredIn" + (maxFiles-1));
		PrintWriter pwAllMinusTwo = new PrintWriter(file.getAbsolutePath() + "//OccuredIn" + (maxFiles-2));
		
		for (String name : map.keySet()) {
			Integer quantity = map.get(name);
			if (quantity >= maxFiles - 2){
				pwAllMinusTwo.println(name);
			}
			if (quantity >= maxFiles - 1){
				pwAllMinusOne.println(name);
			}
			if (quantity == maxFiles){
				pwAll.println(name);
			}
		}
		pw.close();
		pwAll.close();
		pwAllMinusOne.close();
		pwAllMinusTwo.close();
	}
}