package ru.ifmo.feature_utilities.importance;

public interface IValuer {
	public double[] execute(FeatureData data); //Can't override static method in Java 7
}
