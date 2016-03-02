package ru.ifmo.utilities;

public class TimerUtilities {
	private long lastStart;

	private long firstStart;

	public TimerUtilities() {
		start();
	}

	public void start() {
		firstStart = System.currentTimeMillis();
		lastStart = firstStart;
	}

	public long stop() {
		long ret = System.currentTimeMillis() - lastStart;
		lastStart = System.currentTimeMillis();
		return ret;
	}

	public long total() {
		return System.currentTimeMillis() - firstStart;
	}
}
