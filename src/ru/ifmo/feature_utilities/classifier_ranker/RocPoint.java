package ru.ifmo.feature_utilities.classifier_ranker;

public class RocPoint {
	public double TPR = 0; //bad?
	public double FPR = 0; //bad?
	
	public RocPoint(double TPR, double FPR) {
		this.TPR = TPR;
		this.FPR = FPR;
	}
}
