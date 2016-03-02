package ru.ifmo.utilities;

public class IndexingPair implements Comparable<IndexingPair> {
	public double key;
	public int value;

	public IndexingPair(double key, int value) {
		this.key = key;
		this.value = value;
	}

	public int compareTo(IndexingPair o) {
		return -Double.compare(key, o.key);		
	}

}
