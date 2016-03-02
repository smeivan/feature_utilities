package ru.ifmo.feature_utilities.importance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import ru.ifmo.feature_utilities.system.Constants;

public class Logger {

	private final String DEBUG_FOLDER_NAME = LoggerFactory.getInstance().getOutFolder();
	private String subFolder = "";
	private PrintWriter file = null;
	private boolean alwaysWrite = false;
	private String loggerName = UUID.randomUUID().toString();
	private String ID = loggerName;

	public Logger(String fileNamePrefix, String comment, boolean writeFile) {
		alwaysWrite = writeFile;
		if ((Constants.IS_DEBUG)||(alwaysWrite)) {
			File dFolder = new File(DEBUG_FOLDER_NAME);
			if (!dFolder.exists() || !dFolder.isDirectory()) {
				dFolder.mkdirs();
			}

			if (file == null) {
				Calendar cal = Calendar.getInstance();
				cal.getTime();
				SimpleDateFormat sdf = new SimpleDateFormat("HH_mm_ss");
				File innerFile = new File(DEBUG_FOLDER_NAME + subFolder + fileNamePrefix
						+ sdf.format(cal.getTime()) + "_"
						+ cal.get(Calendar.YEAR) + "_"
						+ cal.get(Calendar.MONTH) + ".log");
				try {
					file = new PrintWriter(innerFile);
					file.println(comment);
				} catch (FileNotFoundException e) {
					e.printStackTrace();// /TODO
				}
			}
		}
	}
	
	public void setUniqueName(String s){
		loggerName = s;
	}
	
	public void setSubFolder(String folderName){
		subFolder = folderName;
	}
	
	public String getSubFolder(){
		return subFolder;
	}
	
	public String getUniqueName(){
		return loggerName;
	}
	
	public String getUUID(){
		return ID;
	}

	public void print(String message) {
		if (Constants.CONSOLE_INFO) {
			System.out.print(message);
		}
		if ((Constants.IS_DEBUG)||(alwaysWrite)) {
			file.print(message);
			file.flush();
		}
	}

	public void printDebug(String message) {
		if (Constants.IS_DEBUG) {
			file.print(message);
			file.flush();
		}
	}

	public void println(String message) {
		if (Constants.CONSOLE_INFO) {
			System.out.println(message);
		}
		if ((Constants.IS_DEBUG)||(alwaysWrite)) {
			file.println(message);
			file.flush();
		}
	}

	public void printlnDebug(String message) {
		if (Constants.IS_DEBUG) {
			file.println(message);
			file.flush();
		}
	}

	public void CloseFile() {
		if (file != null) {
			file.close();
		}
	}
}
