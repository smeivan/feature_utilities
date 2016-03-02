package ru.ifmo.feature_utilities.voting;

import java.io.File;
import java.io.IOException;

public interface VotingInterface{
	public void vote(File[] inFiles, String outFile, int numOfFeatures) throws IOException;
}
