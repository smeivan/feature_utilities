package ru.ifmo.feature_utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Random;

import ru.ifmo.feature_utilities.importance.LoggerFactory;
import ru.ifmo.utilities.BackupUtilities;

public class FireFlySearch {
	private static String workingFolder = "";
	protected static class Firefly implements Comparable<Firefly>{
		public double[] coordinates;
		public double quality = 0;

		public Firefly(double[] x) {
			coordinates = x.clone();
		}

		public double distTo(Firefly f) {
			double sqrDist = 0;
			for (int i = 0; i < coordinates.length; i++) {
				sqrDist += (coordinates[i] - f.coordinates[i]) * (coordinates[i] - f.coordinates[i]); // Euclidian
				// distance
			}
			return Math.sqrt(sqrDist);
		}
		
		public String toString(){
			String result = "";
			for (int i=0;i<coordinates.length;i++){
				result+=String.format("%.3f", coordinates[i])+" ";
			}
			result += String.format("%.3f", quality);
			return result;
		}

		@Override
		public int compareTo(Firefly arg0) {
			if (arg0.quality>this.quality){
				return 1;
			}
			if (arg0.quality<this.quality){
				return -1;
			}
			return 0;
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
			optimizeFirefly(folder, 5, 10, 0.1, 0.1, 1);
		}
	}

	private static void optimizeFirefly(File folder, int swarmSize, int maxIterations, double alpha, double beta, double gamma) throws IOException {
		Random r = new Random(System.currentTimeMillis());
		int arity = 4;
		double[][] boundaries = new double[][] { { -2, 2 }, { -2, 2 }, { -2, 2 }, { -2, 2 } };
		writeString(folder, swarmSize + " " + maxIterations + " " + alpha + " " + beta + " " + gamma + "\n");
		Firefly[] swarm = new Firefly[swarmSize];

//		swarm[0] = new Firefly(new double[] { 1, 0, 0, 0 });
//		swarm[0].quality = getAUCScore(folder, swarm[0].coordinates);
//		
//		swarm[1] = new Firefly(new double[] { 0, 1, 0, 0 });
//		swarm[1].quality = getAUCScore(folder, swarm[1].coordinates);
//		
//		swarm[2] = new Firefly(new double[] { 0, 0, 1, 0 });
//		swarm[2].quality = getAUCScore(folder, swarm[2].coordinates);
//		
//		swarm[3] = new Firefly(new double[] { 0, 0, 0, 1 });
//		swarm[3].quality = getAUCScore(folder, swarm[3].coordinates);
//		
//		swarm[4] = new Firefly(new double[] { 1, 1, 1, 1 });
//		swarm[4].quality = getAUCScore(folder, swarm[4].coordinates);

		for (int i = 0; i < swarmSize; i++) {
			double[] x = new double[arity];
			for (int j = 0; j < arity; j++) {
				x[j] = r.nextDouble() * (boundaries[j][1] - boundaries[j][0]) + boundaries[j][0];
			}
			swarm[i] = new Firefly(x);
			swarm[i].quality = getAUCScore(folder, swarm[i].coordinates);
		}
		for (int it = 0; it < maxIterations; it++) {
			writeString(folder, it+"\n");
			writeSwarm(folder, swarm);
			writeString(folder, "\n");
			for (int i = 0; i < swarmSize; i++) {
				for (int j = 0; j <= i; j++) {
					Firefly brighter = swarm[i], darker = swarm[j];
					if (swarm[i].quality < swarm[j].quality) {
						brighter = swarm[j];
						darker = swarm[i];
					}
					double attraction = Math.exp(-gamma * swarm[i].distTo(swarm[j])) * beta;
					double[] move = randomMove(r, alpha, arity);
					for (int k = 0; k < arity; k++) {
						darker.coordinates[k] += attraction * (brighter.coordinates[k] - darker.coordinates[k]) + move[k];
						darker.coordinates[k] = Math.min(boundaries[k][1], Math.max(darker.coordinates[k], boundaries[k][0]));
					}
				}
			}
			for (int i = 0; i < swarmSize; i++) {
				swarm[i].quality = getAUCScore(folder, swarm[i].coordinates);
			}
		}
		writeString(folder, "ending\n");
		writeSwarm(folder, swarm);
		writeString(folder, "\n");
	}
	
	protected static double[] randomMove(Random r, double alpha, int arity) {
        double[] result = new double[arity];
        for (int i = 0; i < arity; i++) {
            result[i] = (r.nextDouble() * 2 - 1) * alpha;
        }
        return result;
    }
	
	private static void writeSwarm(File folder, Firefly[] swarm) throws IOException{
		Arrays.sort(swarm);
		for (int i=0;i<swarm.length;i++){
			writeString(folder, swarm[i].toString()+"\n");
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
