package ru.ifmo.feature_utilities.classifiersBuilders;

import Jama.Matrix;

public class ShitContainer {//TODO Ivan
	public double J;
	public Matrix grad;
	public ShitContainer(double J, Matrix grad){
		this.J = J;
		this.grad = grad;
	}
}
