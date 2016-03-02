package ru.ifmo.feature_utilities.tests;


import java.io.File;
import java.io.IOException;

import ru.ifmo.feature_utilities.feature_number_selection.KSelector;
import ru.ifmo.utilities.FileUtilities;

public class TestFeatureNumberSelector {//may be don't need


	public static void start(String[] args) throws IOException {
		if (args.length != 1) {
			System.err.println("Wrong num of params: file_with_map");
			return;
		}

		System.err.println("Num of features = "
				+ (new KSelector()).selectNumberOfFeatures(FileUtilities.loadStrToDoubleMap(new File(
						args[0]))));

	}
}
