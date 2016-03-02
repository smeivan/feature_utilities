package weka_transformers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.*;

public class WekaCheck {
	public static void main(String[] args) throws IOException {
		args = new String[]{"e:\\24.09.09\\feature_utilities\\datasets\\Kovanikov\\all\\"};
		int total = 0;
		
		File inFolder = new File(args[0]);
		for (File wekaFile : inFolder.listFiles()) {
			if (wekaFile.isDirectory()){
				continue;
			}
			BufferedReader br = new BufferedReader(new FileReader(wekaFile));
			
			int line = 0;
			int zeros = 0;
			int ones = 0;
			
			while (br.ready()){
				++line;
				String str = br.readLine();
				if (str.isEmpty()){
					continue;
				}
				
				int res = Integer.parseInt(new String(new char[]{str.charAt(0)}));
				if (res == 0){
					zeros++;
				}else{
					ones++;
				}
				
//				if (str.charAt(0)=='%'){
//					continue;
//				}
//				if (str.charAt(0)=='@'){
//					if (str.contains("class")){
//						if (str.lastIndexOf(',')!=str.indexOf(',')){
//							System.err.println("File " + wekaFile.getName() + " has more than 2 classes");
//							break;
//						}
//					}
//					continue;
//				}
//				if (str.contains("?")){
//					System.err.println("File " + wekaFile.getName() + " contain ? in line "+ line);
//					break;
//				}
//				if (str.contains(".")){
//					System.err.println("File " + wekaFile.getName() + " contain . in line "+ line);
//					break;
//				}
			}
			
			br.close();
			if (zeros==0){
				System.err.println(wekaFile.getName() +" zeros 0");
				total++;
				continue;
			}
			if (ones==0){
				System.err.println(wekaFile.getName()+" ones 0");
				total++;
				continue;
			}
			if (((double)zeros/(double)line)<0.1){
				System.err.println(wekaFile.getName()+" zeros proportion");
				total++;
				continue;
			}
			if (((double)ones/(double)line)<0.1){
				System.err.println(wekaFile.getName()+" ones proportion");
				total++;
				continue;
			}
		}	
		System.err.println("total = " +total);
	}
}
