package ru.ifmo.feature_utilities.voting;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import nds.NonDominatedSorting;
import ru.ifmo.utilities.FileUtilities;

public class NDS implements VotingInterface {

	@Override
	public void vote(File[] inFiles, String outFile, int numOfFeatures)
			throws IOException {

		int m = FileUtilities.getNumOfStringsInFile(inFiles[0]);// number of
																// features
		int n = inFiles.length;// number of votes
		
		Map<Integer, Integer> newToOldIndex = new HashMap<Integer, Integer>();

		double[][] input = FileUtilities.fillMatrix(inFiles, m, n, numOfFeatures, newToOldIndex);

		final int []fronts = NonDominatedSorting.sort(input);
		
		Integer []newFeaturesIndexes = new Integer[input.length];
		
		//System.err.println(numOfFeatures + " " + input.length);
		
		int TMP_num_of_fronts = 0;
		for (int i = 0; i < input.length; i++){
			newFeaturesIndexes[i] = i;
			if (fronts[i] > TMP_num_of_fronts){
				TMP_num_of_fronts = fronts[i];
			}
		}	
		
		Arrays.sort(newFeaturesIndexes, new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				return fronts[o1] - fronts[o2];
			}
		});
		
		//System.err.println("NUM OF FRONTS = " + (TMP_num_of_fronts + 1));
		//System.err.println("First front = " + fronts[newFeaturesIndexes[0]] + " Last front = " + fronts[newFeaturesIndexes[numOfFeatures]]);

		try {
			
			PrintWriter pw = new PrintWriter(outFile); 
			for (int i = 0; i <	 numOfFeatures; i++) 
			{ 
				pw.println(newToOldIndex.get(newFeaturesIndexes[i]));				
			} 
			pw.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		

	}
}
