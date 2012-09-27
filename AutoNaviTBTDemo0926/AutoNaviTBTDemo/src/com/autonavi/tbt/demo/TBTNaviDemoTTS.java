package com.autonavi.tbt.demo;
/*
import android.content.Context;
import android.os.SystemClock;

import com.iflytek.speech.SpeechError;
import com.iflytek.speech.SynthesizerPlayer;
import com.iflytek.speech.SynthesizerPlayerListener;

public class TBTNaviDemoTTS implements SynthesizerPlayerListener{

	private SynthesizerPlayer mSynthesizerPlayer = null;
	private boolean mSpeakOver = false;
	
	public void initTTS(Context text)
	{
		if (mSynthesizerPlayer == null) {
			mSynthesizerPlayer = SynthesizerPlayer.createSynthesizerPlayer(text, null);
			mSynthesizerPlayer.setVoiceName("vivixiaomei");
		}
	}
	
	public void ttsSpeak(String str)
	{
		mSpeakOver = false;
		mSynthesizerPlayer.playText(str, null, this);
		
		while(!mSpeakOver) {
			SystemClock.sleep(1000);
		}
	}
	
	@Override
	public void onBufferPercent(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEnd(SpeechError arg0) {
		// TODO Auto-generated method stub
		mSpeakOver = false;
	}

	@Override
	public void onPlayBegin() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPlayPaused() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPlayPercent(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPlayResumed() {
		// TODO Auto-generated method stub
		
	}

}
*/
