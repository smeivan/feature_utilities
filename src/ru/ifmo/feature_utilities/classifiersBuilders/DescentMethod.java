package ru.ifmo.feature_utilities.classifiersBuilders;

import java.io.IOException;

import Jama.Matrix;

public class DescentMethod {//TODO Ivan
	private ShitCalculator calc;

	public DescentMethod(ShitCalculator calculator) throws IOException {
		if (calculator == null) {
			throw new IOException("calculator");
		}
		calc = calculator;
	}

	private double initialStep = 100;

	public Matrix calculate() {
		Matrix result = calc.getThetas();
		double prevJ = Double.MAX_VALUE;
		double step = initialStep;
		double eps = 0.000005;
		int iter = 0;
		while (true) {
			iter++;
			if (iter - (iter/10)*10 == 1){
				System.out.println(iter);
			}
			
			ShitContainer container = calc.costFunctionReg();
			if (prevJ < container.J) {
				step /= 1.5;
			}

			result.minusEquals(container.grad.times(step));
			calc.setThetas(result);

			if (Double.isNaN(Math.abs(prevJ - container.J))) {
				calc.getThetas().timesEquals(0);
				initialStep /= 4;
				step = initialStep;
				prevJ = Double.MAX_VALUE;
				continue;
			}

			if (Math.abs(prevJ - container.J) < eps) {
				break;
			}
//			if (iter % 10 == 0) {
//				System.err.println("Iter num = " + iter + ". delta = " + (Math.abs(prevJ - container.J)));
//			}
			prevJ = container.J;
			if (iter == 50){
				break;
			}
		}
		return result;
	}

}
