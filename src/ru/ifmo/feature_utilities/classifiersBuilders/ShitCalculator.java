package ru.ifmo.feature_utilities.classifiersBuilders;

import java.io.IOException;

import Jama.Matrix;

public class ShitCalculator {

	private Matrix matrix = null;
	private double lambda = 1;
	private Matrix transposedMatrix = null;
	private Matrix vector = null;
	private Matrix thetas = null;

	public ShitCalculator(Matrix initialThetas, Matrix matrix, Matrix vector, double lambda) throws IOException {//TODO Ivan
		if (initialThetas == null) {
			throw new IOException("initialThetas");
		}
		if (matrix == null) {
			throw new IOException("matrix");
		}
		if (vector == null) {
			throw new IOException("vector");
		}
		if (lambda <= 0) {
			throw new IOException("lambda");
		}
		thetas = initialThetas;
		this.lambda = lambda;
		this.vector = vector;
		this.matrix = matrix;

		transposedMatrix = matrix.transpose();
	}

	public ShitContainer costFunctionReg() {
		//double[][] mt = thetas.getArray();
		/*System.err.println("StartTheta");
		for (int i = 0; i < mt.length; i++) {
			for (int k = 0; k < mt[i].length; k++) {
				System.err.print(mt[i][k] + " ");
			}
		}
		System.err.println("StopTheta");*/
		int m = vector.getColumnDimension();
		double[] s = sigmoid(matrix.times(thetas));
		double J = 0;
		for (int i = 0; i < s.length; i++) {
			double yi = vector.getArray()[0][i];
			J += yi * Math.log(s[i]) + (1 - yi) * Math.log(1 - s[i]);
		}
		J = -J / m;
		Matrix reg = power(thetas, 2).timesEquals((double) lambda / 2.0 / m);
		reg.getArray()[0][0] = 0;
		J += sum(reg);
		Matrix grad = thetas.times((double) lambda / m);

		
		grad.getArray()[0][0] = 0;
		Matrix sMatrix = new Matrix(s, 1);
		grad.plusEquals(transposedMatrix.times(sMatrix.minus(vector).transpose()).timesEquals(1.0 / m));
/*		mt = grad.getArray();

		for (int i = 0; i < mt.length; i++) {
			for (int k = 0; k < mt[i].length; k++) {
				System.err.print(mt[i][k] + " ");
			}
		}*/

		return new ShitContainer(J, grad);
	}

	private double[] sigmoid(Matrix x) {
		double[][] m = x.getArray();
		double result[] = new double[m.length];
		for (int i = 0; i < m.length; i++) {
			result[i] = (double) 1 / (1 + Math.exp(-m[i][0]));
		}
		return result;
	}

	private Matrix power(Matrix m, double d) {
		double[][] innerArray = m.getArrayCopy();
		for (int i = 0; i < innerArray.length; i++) {
			for (int j = 0; j < innerArray[i].length; j++) {
				innerArray[i][j] = Math.pow(innerArray[i][j], d);
			}
		}
		return new Matrix(innerArray);
	}

	private double sum(Matrix m) {
		double res = 0;
		double[][] innerArray = m.getArrayCopy();
		for (int i = 0; i < innerArray.length; i++) {
			for (int j = 0; j < innerArray[0].length; j++) {
				res += innerArray[i][j];
			}
		}
		return res;
	}

	public void setThetas(Matrix newThetas) {
		thetas = newThetas;
	}

	public Matrix getThetas() {
		return thetas;
	}
}
