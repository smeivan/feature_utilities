package weka_transformers;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

import ru.ifmo.utilities.FileUtilities;

public class WekaPrepareFolders {

	public static void main(String[] args) throws IOException{
		args = new String[]{"e:\\24.09.09\\feature_utilities\\datasets\\new\\all\\"};
		File inFolder = new File(args[0]);
		File outFolder = new File(args[0]+"\\test_data4\\");
		
		outFolder.mkdirs();
		
		for (File wekaFile : inFolder.listFiles()) {
			if (wekaFile.isDirectory()){
				continue;
			}
			File newFolder = new File(outFolder.getAbsolutePath()+"\\"+wekaFile.getName()+"\\1_start_matrix\\");
			newFolder.mkdirs();
			File newFile = new File(newFolder.getAbsolutePath()+"\\matrix");
			newFile.createNewFile();
			FileUtilities.copyFileUsingStream(wekaFile, newFile);
			prepareDatasetValidation(outFolder.getAbsolutePath()+"\\"+wekaFile.getName()+"\\");
			break;
		}
		
		
	}
	
	
	public static void prepareDatasetValidation(String folderName) throws IOException{
		File workingFolder = new File(folderName);
		File outFile = new File(workingFolder + "\\99_validation_matrix\\");
		outFile.mkdirs();
		outFile = new File(workingFolder + "\\99_validation_matrix\\matrix");
		outFile.createNewFile();
		
		FileUtilities.shuffleLines(new File(folderName+"\\1_start_matrix\\matrix"));
		
		BufferedReader br = new BufferedReader(new FileReader(folderName+"\\1_start_matrix\\matrix"));
		int zeros = 0;
		int ones = 0;
		while (br.ready()){
			String str = br.readLine();
			if (str.isEmpty())
				continue;
			if (str.charAt(0)=='0'){
				zeros++;
			}else if (str.charAt(0)=='1'){
				ones++;
			}
		}
		br.close();
		zeros = (int)(zeros*0.2);
		ones = (int)(ones*0.2);
		
		File reducedFile = new File(folderName+"\\1_start_matrix\\new_matrix");
		int currentZeros = 0;
		int currentOnes = 0;
		br = new BufferedReader(new FileReader(folderName+"\\1_start_matrix\\matrix"));
		while (br.ready()){
			String str = br.readLine()+"\n";
			if (str.isEmpty())
				continue;
			if (str.charAt(0)=='0'){
				if (currentZeros<zeros){//TODO: optimize writing
					FileUtilities.writeString(outFile, str);
					++currentZeros;
				}else{
					FileUtilities.writeString(reducedFile, str);
					++currentZeros;
				}
			}else if (str.charAt(0)=='1'){
				if (currentOnes<ones){
					FileUtilities.writeString(outFile, str);
					++currentOnes;
				}else{
					FileUtilities.writeString(reducedFile, str);
					++currentOnes;
				}
			}
		}
		br.close();
		File f = new File(folderName+"\\1_start_matrix\\matrix");
		f.delete();
		reducedFile.renameTo(f);
	}


}
