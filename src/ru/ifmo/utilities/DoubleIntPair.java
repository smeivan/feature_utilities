package ru.ifmo.utilities;

public class DoubleIntPair implements Comparable<DoubleIntPair> {
	public double key;
	public int value;

	public DoubleIntPair(double key, int value) {
		this.key = key;
		this.value = value;
	}

	public int compareTo(DoubleIntPair o) {
		return -Double.compare(key, o.key);
		
	}

}
