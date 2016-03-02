package ru.ifmo.utilities;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import ru.ifmo.feature_utilities.Starter;

public class BackupUtilities {
	public static final String ENDS_MARK = "/ended";

	public static int detectLastSucsessfulStep(String workingFolder) {
		File f = new File(workingFolder + Starter.SEVENTH_STEP_FOLDER + ENDS_MARK);
		if (f.exists()) {
			return 7;
		}
		f = new File(workingFolder + Starter.SIXTH_STEP_FOLDER + ENDS_MARK);
		if (f.exists()) {
			return 6;
		}
		f = new File(workingFolder + Starter.FIFTH_STEP_FOLDER + ENDS_MARK);
		if (f.exists()) {
			return 5;
		}
		f = new File(workingFolder + Starter.FOURTH_STEP_FOLDER + ENDS_MARK);
		if (f.exists()) {
			return 4;
		}
		f = new File(workingFolder + Starter.THIRD_STEP_FOLDER + ENDS_MARK);
		if (f.exists()) {
			return 3;
		}
		f = new File(workingFolder + Starter.SECOND_STEP_FOLDER + ENDS_MARK);
		if (f.exists()) {
			return 2;
		}
		return 1;
	}

	public static void storePoint(String workingFolder, double[] point, double AUC){
		File folder = new File(workingFolder+"/99_resulting_features/");
		int folderIndex = folder.listFiles().length;
		String folderName="";
		if (folderIndex>99)
			folderName += folderIndex+"_";
		else
			if (folderIndex>9)
				folderName += "0"+folderIndex+"_";
			else
				folderName += "00"+folderIndex+"_";
		
		folderName += String.format("%.3f", AUC);
		
		for (int i=0;i<point.length;i++){
			folderName+="_"+String.format("%.3f", point[i]);
		}
		
		File copyFolder = new File(folder+"/"+folderName+"/");
		copyFolder.mkdir();
		FileUtilities.copyFolder(new File(workingFolder+"/"+Starter.FOURTH_STEP_FOLDER), copyFolder);
		
	}

	public static void deleteAllOddFolders(int lastSuccessfulStep, String workingFolder) throws IOException {
		if (lastSuccessfulStep < 7)
			FileUtilities.removeDirectory(new File(workingFolder + Starter.SEVENTH_STEP_FOLDER));
		if (lastSuccessfulStep < 6)
			FileUtilities.removeDirectory(new File(workingFolder + Starter.SIXTH_STEP_FOLDER));
		if (lastSuccessfulStep < 5)
			FileUtilities.removeDirectory(new File(workingFolder + Starter.FIFTH_STEP_FOLDER));
		if (lastSuccessfulStep < 4)
			FileUtilities.removeDirectory(new File(workingFolder + Starter.FOURTH_STEP_FOLDER));
		if (lastSuccessfulStep < 3)
			FileUtilities.removeDirectory(new File(workingFolder + Starter.THIRD_STEP_FOLDER));
		if (lastSuccessfulStep < 2)
			FileUtilities.removeDirectory(new File(workingFolder + Starter.SECOND_STEP_FOLDER));
	}

	public static void addEndsMark(String path) throws IOException {
		// (new File(path+ENDS_MARK)).createNewFile(); // why not?
		PrintWriter pw = new PrintWriter(path + ENDS_MARK);
		pw.close();
	}

}
