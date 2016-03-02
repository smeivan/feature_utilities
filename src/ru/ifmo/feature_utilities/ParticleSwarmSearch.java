package ru.ifmo.feature_utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import ru.ifmo.feature_utilities.importance.LoggerFactory;
import ru.ifmo.utilities.BackupUtilities;

public class ParticleSwarmSearch {
	private static String workingFolder = "";

	public static Random r = new Random(System.currentTimeMillis());
	public static double omega = -0.2;//-04
	public static double phiP = -0.3;//-07
	public static double phiG = 2.3;//22
	public static int arity = 4;

	protected static class Point {
		public double[] coordinates;
		public double quality;
	}

	protected static class Particle implements Comparable<Particle> {
		public double[] v;
		public Point bestKnown = new Point();
		public double[] coordinates;
		public double quality;

		public Particle(double[] x) {
			coordinates = x.clone();
		}

		public String toString() {
			String result = "";
			for (int i = 0; i < coordinates.length; i++) {
				result += String.format("%.3f", coordinates[i]) + " ";
			}
			result += String.format("%.3f", quality);
			return result;
		}

		@Override
		public int compareTo(Particle arg0) {
			if (arg0.quality > this.quality) {
				return 1;
			}
			if (arg0.quality < this.quality) {
				return -1;
			}
			return 0;
		}

		public double distTo(Particle f) {
			double sqrDist = 0;
			for (int i = 0; i < coordinates.length; i++) {
				sqrDist += (coordinates[i] - f.coordinates[i]) * (coordinates[i] - f.coordinates[i]);
			}
			return Math.sqrt(sqrDist);
		}
	}

	public static void main(String[] args) throws IOException {
		System.setProperty("line.separator", "\n");

		args = new String[] { "start", "e:\\24.09.09\\feature_utilities\\datasets\\new\\all\\test_data2\\" };

		try {
			String[] newArgs = new String[args.length - 1];
			for (int i = 1; i < args.length; i++) {
				newArgs[i - 1] = args[i];
			}

			if (args[0].equals("start")) {
				if (args.length == 2) {
					if (args[1].charAt(args[1].length() - 1) == ('/')) {
						workingFolder = args[1];
					} else {
						workingFolder = args[1] + "/";
					}
				} else {
					LoggerFactory.getInstance().println("Wrong params");
				}
			}

		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			LoggerFactory.getInstance().println(sw.toString());
		}

		File f = new File(workingFolder);

		for (File folder : f.listFiles()) {
			if (completedData(folder)) {
				continue;
			}

			prepareFiles(folder);
			optimizeParticleSwarm(folder, 10, 10);
		}
	}

	private static void optimizeParticleSwarm(File folder, int swarmSize, int maxIterations) throws IOException {

		double[][] boundaries = new double[][] { { -2, 2 }, { -2, 2 }, { -2, 2 }, { -2, 2 } };
		Particle[] swarm = new Particle[swarmSize];

		swarm[0] = new Particle(new double[] { 1, 0, 0, 0 });
		swarm[0].quality = getAUCScore(folder, swarm[0].coordinates);

		swarm[1] = new Particle(new double[] { 0, 1, 0, 0 });
		swarm[1].quality = getAUCScore(folder, swarm[1].coordinates);

		swarm[2] = new Particle(new double[] { 0, 0, 1, 0 });
		swarm[2].quality = getAUCScore(folder, swarm[2].coordinates);

		swarm[3] = new Particle(new double[] { 0, 0, 0, 1 });
		swarm[3].quality = getAUCScore(folder, swarm[3].coordinates);

		swarm[4] = new Particle(new double[] { 1, 1, 1, 1 });
		swarm[4].quality = getAUCScore(folder, swarm[4].coordinates);

		for (int i = 5; i < swarmSize; i++) {
			double[] x = new double[arity];
			for (int j = 0; j < arity; j++) {
				x[j] = r.nextDouble() * (boundaries[j][1] - boundaries[j][0]) + boundaries[j][0];
			}
			swarm[i] = new Particle(x);
			swarm[i].quality = getAUCScore(folder, swarm[i].coordinates);
		}

		Point best = null;
		for (int i = 0; i < swarmSize; i++) {
			double[] x = new double[arity];
			double[] v = new double[arity];
			for (int j = 0; j < arity; j++) {
				x[j] = r.nextDouble() * (boundaries[j][1] - boundaries[j][0]) + boundaries[j][0];
				v[j] = (r.nextDouble() * 2 - 1) * (boundaries[j][1] - boundaries[j][0]);
			}
			swarm[i] = new Particle(x);
			swarm[i].v = v.clone();
			swarm[i].quality = getAUCScore(folder, swarm[i].coordinates);
			swarm[i].bestKnown.quality = swarm[i].quality;
			swarm[i].bestKnown.coordinates = swarm[i].coordinates.clone();
			if (best == null || swarm[i].quality > best.quality) {
				best = new Point();
				best.quality = swarm[i].quality;
				best.coordinates = swarm[i].coordinates.clone();
			}
		}
		
		for (int it = 0; it < maxIterations; it++) {
			writeString(folder, it+"\n");
			writeSwarm(folder, swarm);
			writeString(folder, "\n");
			for (int i = 0; i < swarmSize; i++) {
				for (int j = 0; j < arity; j++) {
					double rg = r.nextDouble(), rp = r.nextDouble();
					swarm[i].v[j] = omega * swarm[i].v[j] + phiG * rg * (best.coordinates[j] - swarm[i].coordinates[j]) + phiP * rp * (swarm[i].bestKnown.coordinates[j] - swarm[i].coordinates[j]);
				}
				for (int j = 0; j < arity; j++) {
					swarm[i].coordinates[j] += swarm[i].v[j];
				}
				swarm[i].quality = getAUCScore(folder, swarm[i].coordinates);
				if (swarm[i].quality > swarm[i].bestKnown.quality) {
					swarm[i].bestKnown = new Point();
					swarm[i].bestKnown.coordinates = swarm[i].coordinates.clone();
					swarm[i].bestKnown.quality = swarm[i].quality;
				}
				if (swarm[i].quality > best.quality) {
					best = new Point();
					best.coordinates = swarm[i].coordinates.clone();
					best.quality = swarm[i].quality;
				}
			}
		}
		writeString(folder, "ending\n");
		writeSwarm(folder, swarm);
		writeString(folder, "\n");
	}

	private static void writeSwarm(File folder, Particle[] swarm) throws IOException {
		Arrays.sort(swarm);
		for (int i = 0; i < swarm.length; i++) {
			writeString(folder, swarm[i].toString() + "\n");
		}
	}

	private static void prepareFiles(File folder) throws IOException {
		BackupUtilities.deleteAllOddFolders(3, folder.getAbsolutePath() + "\\");

		File resultsFile = new File(folder.getAbsolutePath() + "//results");
		resultsFile.createNewFile();
		FileWriter fileWritter = new FileWriter(resultsFile.getAbsolutePath(), true);
		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		bufferWritter.write("fc sp su vdm AUC\n");
		bufferWritter.close();
	}

	private static void writeString(File folder, String s) throws IOException {
		File resultsFile = new File(folder.getAbsolutePath() + "//results");
		FileWriter fileWritter = new FileWriter(resultsFile.getAbsolutePath(), true);
		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		bufferWritter.write(s);
		bufferWritter.close();
	}

	private static double getAUCScore(File folder, double[] point) throws IOException {
		EvaluateOptimisationPoint.main(new String[] { "start", folder.getAbsolutePath(), String.valueOf(point[0]), String.valueOf(point[1]), String.valueOf(point[2]), String.valueOf(point[3]) });
		File subFolder = new File(folder.getAbsolutePath() + "//7_AUC_scores_WPCA_SVM//");
		double auc = 0;
		int experiments = 0;
		for (File experiment : subFolder.listFiles()) {
			if (!experiment.isDirectory()) {
				continue;
			}
			experiments++;
			BufferedReader br = new BufferedReader(new FileReader(experiment.listFiles()[0].getAbsolutePath() + "//rank"));
			auc += Double.parseDouble(br.readLine());
			br.close();
		}
		auc /= experiments;
		BackupUtilities.deleteAllOddFolders(3, folder.getAbsolutePath() + "\\");
		return auc;
	}

	private static boolean completedData(File folder) {
		for (File f : folder.listFiles()) {
			if (f.isDirectory()) {
				continue;
			}
			if (f.getName().equals("ended")) {
				return true;
			}
		}
		return false;
	}

}
