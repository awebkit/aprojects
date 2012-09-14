package com.iflytek.tts.TtsService;

import android.util.Log;

public final class Tts {
	// /**
	// * 开始合成任务线程
	// */
	// public synchronized static void startReadThread(Activity ctx, final
	// String playMsg) {
	// class TtsRunThread implements Runnable {
	// @Override
	// public void run() {
	// JniSpeak(playMsg);
	// }
	// }
	// Thread ttsRun = (new Thread(new TtsRunThread()));
	// ttsRun.setPriority(Thread.MAX_PRIORITY);
	// ttsRun.start();
	// }

	public static native int JniGetVersion();

	public static native int JniCreate(String resFilename);

	public static native int JniDestory();

	public static native int JniStop();

	public static native int JniSpeak(String text);

	public static native int JniSetParam(int paramId, int value);

	public static native int JniGetParam(int paramId);

	public static native int JniIsPlaying();

	public static native boolean JniIsCreated();

	// private static String playMsg;

	/**
	 * 开始合成任务线程
	 */
	public synchronized static void startReadThread(final String ctx) {
		class TtsRunThread implements Runnable {
			@Override
			public void run() {
				JniSpeak(ctx);
			}
		}
		Thread ttsRun = (new Thread(new TtsRunThread()));
		ttsRun.setPriority(Thread.MAX_PRIORITY);
		ttsRun.start();
	}

}
