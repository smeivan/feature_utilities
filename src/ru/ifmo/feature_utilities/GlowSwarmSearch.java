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

public class GlowSwarmSearch {
	private static String workingFolder = "";
	
	public static Random r = new Random(System.currentTimeMillis());
	public static int arity = 4;
	public static double gamma = 1;
	public static double p = 0.5;
	public static double nt = 5;
	public static double step = 0.1;
	public static double maxV=8;
	public static double beta = 2;


	protected static class Glowworm implements Comparable<Glowworm>{
		public double luciferin = 0;
		public double visibility = 1; 
		public double[] coordinates;
		public double quality;

		public Glowworm(double[] x) {
			coordinates = x.clone();
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
		public int compareTo(Glowworm arg0) {
			if (arg0.quality>this.quality){
				return 1;
			}
			if (arg0.quality<this.quality){
				return -1;
			}
			return 0;
		}

		public double distTo(Glowworm f) {
			double sqrDist = 0;
			for (int i = 0; i < coordinates.length; i++) {
				sqrDist += (coordinates[i] - f.coordinates[i]) * (coordinates[i] - f.coordinates[i]);
			}
			return Math.sqrt(sqrDist);
		}

		public void updateLuciferin() {
			luciferin = luciferin * (1 - p) + gamma * quality;
		}

		protected Glowworm selectNeighbour(Glowworm[] swarm) {
            List<Glowworm> neighbours = new ArrayList<Glowworm>();
            double sum = 0;
            for (Glowworm g: swarm) if (g.quality > quality) {
                double d = distTo(g);
                if (d > 0 && d <= visibility) {
                    neighbours.add(g);
                    sum += quality - g.quality; 
                }								
            }
            double q = r.nextDouble() * sum;
            int j = 0;
            sum = 0;
            while (j < neighbours.size() - 1 && sum + quality - neighbours.get(j).quality < q) { // Вообще не понял смысла этого шага
                sum += quality - neighbours.get(j).quality;
                j++;
            }
            updateVisibility(neighbours);
            if (neighbours.size() > 0) {
                return neighbours.get(j);
            } else {
                return null;
            }
        }

		public void moveToNeighbour(Glowworm[] swarm) {
			Glowworm g = selectNeighbour(swarm);
			if (g != null) {
				double d = distTo(g);
				for (int i = 0; i < coordinates.length; i++) {
					coordinates[i] += step * (g.coordinates[i] - coordinates[i]) / d;
				}
			} else {
				double[] m = randomMove(coordinates.length);
				for (int i = 0; i < coordinates.length; i++) {
					coordinates[i] += m[i];
				}
			}
		}

		public void updateVisibility(List<Glowworm> neighbour) {
			visibility = Math.min(maxV, Math.max(visibility, beta * (nt - neighbour.size())));
		}
	}
	
	protected static double[] randomMove(int arity) {
        double[] result = new double[arity];
        for (int i = 0; i < arity; i++) {
            result[i] = (r.nextDouble() * 2 - 1) * step / 2;
        }
        return result;
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
			optimizeGlowSwarm(folder, 10, 10);
		}
	}

	private static void optimizeGlowSwarm(File folder, int swarmSize, int maxIterations) throws IOException {
		
		double[][] boundaries = new double[][] { { -2, 2 }, { -2, 2 }, { -2, 2 }, { -2, 2 } };
		Glowworm[] swarm = new Glowworm[swarmSize];

		swarm[0] = new Glowworm(new double[] { 1, 0, 0, 0 });
		swarm[0].quality = getAUCScore(folder, swarm[0].coordinates);
		
		swarm[1] = new Glowworm(new double[] { 0, 1, 0, 0 });
		swarm[1].quality = getAUCScore(folder, swarm[1].coordinates);
		
		swarm[2] = new Glowworm(new double[] { 0, 0, 1, 0 });
		swarm[2].quality = getAUCScore(folder, swarm[2].coordinates);
		
		swarm[3] = new Glowworm(new double[] { 0, 0, 0, 1 });
		swarm[3].quality = getAUCScore(folder, swarm[3].coordinates);
		
		swarm[4] = new Glowworm(new double[] { 1, 1, 1, 1 });
		swarm[4].quality = getAUCScore(folder, swarm[4].coordinates);

		for (int i = 5; i < swarmSize; i++) {
			double[] x = new double[arity];
			for (int j = 0; j < arity; j++) {
				x[j] = r.nextDouble() * (boundaries[j][1] - boundaries[j][0]) + boundaries[j][0];
			}
			swarm[i] = new Glowworm(x);
			swarm[i].quality = getAUCScore(folder, swarm[i].coordinates);
		}

		for (int it = 0; it < maxIterations; it++) {
			writeString(folder, it+"\n");
			writeSwarm(folder, swarm);
			writeString(folder, "\n");
            for (int i = 0; i < swarmSize; i++) {
                swarm[i].moveToNeighbour(swarm);
            }
            for (int i = 0; i < swarmSize; i++) {
                swarm[i].quality = getAUCScore(folder, swarm[i].coordinates);
                swarm[i].updateLuciferin();
            }
        }
		
		writeString(folder, "ending\n");
		writeSwarm(folder, swarm);
		writeString(folder, "\n");
	
	}

	private static void writeSwarm(File folder, Glowworm[] swarm) throws IOException {
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
