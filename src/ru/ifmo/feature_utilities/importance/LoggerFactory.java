package ru.ifmo.feature_utilities.importance;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ru.ifmo.feature_utilities.Starter;
import ru.ifmo.feature_utilities.system.Constants;

public class LoggerFactory {
	private static LoggerFactory instance;		
	private List<Logger> loggers = new ArrayList<Logger>();
	private String outSubFolder = "";
	private int tabLvl = 0;
	private boolean isGlobalLogExists = false;
	private static final String globalLogName = "/GlobalLog";
	
	public String getOutFolder(){
		return outSubFolder;
	}

	private Object loggersSyncObject = new Object();

	public static LoggerFactory getInstance() {
		if (instance == null) {
			instance = new LoggerFactory();
			Calendar cal = Calendar.getInstance();
			cal.getTime();
			SimpleDateFormat sdf = new SimpleDateFormat("HH_mm_ss");
			instance.outSubFolder = sdf.format(cal.getTime()) + "_" + cal.get(Calendar.YEAR) + "_" + cal.get(Calendar.MONTH) +"_"+ Starter.SEVENTH_STEP_FOLDER +"/";
		}
		return instance;
	}

	private LoggerFactory() {
	}

	private void writelnToGlobalLogFile(String line) {
		checkGlobalLog();
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(outSubFolder + globalLogName, true));
			for (int i=0;i<tabLvl;i++){
				pw.print("	");
			}
			pw.println(line);
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void checkGlobalLog() {
		if (!isGlobalLogExists) {
			if (Constants.FILE_LOGGING) {
				File f = new File(outSubFolder);
				if (!f.exists()) {
					f.mkdir();
				}
				PrintWriter pw;
				try {
					pw = new PrintWriter(new FileWriter(outSubFolder + globalLogName));
					pw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			isGlobalLogExists = true;
		}
	}

	public Logger createLogger(String methodName, String methodParams, String comments, boolean writeFile) {
		synchronized (loggersSyncObject) {

		}
		Logger result = new Logger(methodName + "_" + methodParams + "_", comments, writeFile);
		if (writeFile) {
			result.setSubFolder(outSubFolder);
		}
		synchronized (loggersSyncObject) {
			loggers.add(result);
		}
		return result;
	}

	public void deleteLogger(Logger logger) {
		logger.CloseFile();
		synchronized (loggersSyncObject) {
			loggers.remove(logger);
		}
	}

	public static void startPrintProgress(String message) {
		if (Constants.CONSOLE_INFO) {
			System.out.print(message + ":  0% done");
		}
	}

	public void finalize() {
		synchronized (loggersSyncObject) {
			for (Logger logger : loggers) {
				logger.CloseFile();
				loggers.remove(logger);
			}
		}
	}

	public static void printProgress(int progress) {
		if (Constants.CONSOLE_INFO) {
			System.out.print("\b\b\b\b\b\b\b\b");
			if (progress < 10) {
				System.out.print(" ");
			}
			System.out.print(progress + "% done");
			if (progress == 100) {
				System.out.println();
			}
		}
	}

	public void print(String message) {
		if (Constants.CONSOLE_INFO) {
			System.out.print(message);
		}
	}

	public void println(String message) {
		if (Constants.CONSOLE_INFO) {
			System.out.println(message);
		}
		if (Constants.FILE_LOGGING) {
			writelnToGlobalLogFile(message);
		}
	}

}
