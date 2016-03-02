package ru.ifmo.feature_utilities.classifier_ranker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ru.ifmo.utilities.FileUtilities;

public class AUC {

	private List<RocPoint> points;
	
	/**
	 * TPFPTNFN - True positive, False positive, True negative, False negative rates.
	 * @param clResultsm
	 * @param answers
	 * @param w
	 * @return
	 */

	private int[] calcTPFPTNFN(double[] clResultsm, boolean[] answers, double w) {
		int[] res = new int[4];
		for (int i = 0; i < clResultsm.length; i++) {
			if (clResultsm[i] >= w) {
				if (answers[i]) {
					res[0]++;
				} else {
					res[1]++;
				}
			} else {
				if (answers[i]) {
					res[3]++;
				} else {
					res[2]++;
				}
			}
		}
		return res;
	}

	private RocPoint calcTPRFPR(double[] clResults, boolean[] answers, double w) {
		int[] TPFPTNFN = calcTPFPTNFN(clResults, answers, w);
		return new RocPoint(((double) TPFPTNFN[0])
				/ (double) (TPFPTNFN[0] + TPFPTNFN[3]), ((double) TPFPTNFN[1])
				/ (double) (TPFPTNFN[1] + TPFPTNFN[2]));
	}

	private double calcAUC(double[] clResults, boolean[] answers) {
		points = new ArrayList<RocPoint>();
		for (int i = 0; i < answers.length; i++) {
			points.add(calcTPRFPR(clResults, answers, clResults[i]));

		}
		points.add(new RocPoint(0, 0));
		Collections.sort(points, new Comparator<RocPoint>() {

			@Override
			public int compare(RocPoint o1, RocPoint o2) {
				if (o1.FPR > o2.FPR) {
					return 1;
				}
				if (o1.FPR < o2.FPR) {
					return -1;
				}
				if (o1.TPR > o2.TPR) {
					return 1;
				}
				if (o1.TPR < o2.TPR) {
					return -1;
				}
				return 0;
			}
		});
		double auc = 0;
		for (int i = 1; i < points.size(); i++) {
			RocPoint cur = points.get(i);
			RocPoint prev = points.get(i - 1);
			auc += prev.TPR * (cur.FPR - prev.FPR)
					+ ((cur.TPR - prev.TPR) * (cur.FPR - prev.FPR) / 2);
		}
		return auc;
	}

	public double getRank(File clResultsFile, File answersFile)
			throws IOException {
		return calcAUC(
				FileUtilities.loadDoubleVectorFromFileLines(clResultsFile),
				FileUtilities.loadBooleanVectorFromFileLines(answersFile));
	}

	public List<RocPoint> getPoints() {
		return points;
	}
}
