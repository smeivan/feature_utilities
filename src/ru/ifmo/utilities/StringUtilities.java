package ru.ifmo.utilities;

import java.util.StringTokenizer;

public class StringUtilities {

	public static int[] stringToIntVector(String line) {
		StringTokenizer st = new StringTokenizer(line);
		int[] vector = new int[st.countTokens()];
		int curPos = 0;
		while (st.hasMoreTokens()) {
			vector[curPos] = Integer.parseInt((st.nextToken()));
			curPos++;
		}
		return vector;
	}

}
