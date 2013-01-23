package com.scottibmorris.textyspeaky;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements OnInitListener {

	private TextToSpeech mTTS = null;
	private Locale mTTSLanguage = Locale.US;
	private Button mPlayButton, mStopButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final EditText textToSpeak = (EditText) findViewById(R.id.textInput);

		mStopButton = (Button) findViewById(R.id.buttonStop);
		Button clearButton = (Button) findViewById(R.id.buttonClear);
		mPlayButton = (Button) findViewById(R.id.buttonPlay);

		mPlayButton.setEnabled(false);
		mPlayButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				speakText(textToSpeak.getText());
			}
		});

		clearButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				textToSpeak.setText("");
				mStopButton.setEnabled(false);
			}
		});

		mStopButton.setEnabled(false);
		mStopButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mTTS.isSpeaking()) {
					mTTS.stop();
					v.setEnabled(false);
				}
			}
		});
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if (mTTS == null)
			mTTS = new TextToSpeech(this, this);
	}

	@Override
	protected void onDestroy() {
		if (mTTS != null) {
			mTTS.stop();
			mTTS.shutdown();
			mTTS = null;
		}
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			startActivity(new Intent(this, SettingsActivity.class));
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onInit(int status) {
		// Check Status code to see if we can proceed
		if (status == TextToSpeech.SUCCESS) {
			String language = PreferenceManager.getDefaultSharedPreferences(
					this).getString("tts_lang", "en_US");
			Locale locale = new Locale(language);
			Log.d("MainActivity",
					"TTS Local Suggested: " + locale.getLanguage());
			int result = mTTS.setLanguage(locale);
			// check result
			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e("MainActivity", "Language is not supported");
			} else {
				mPlayButton.setEnabled(true);
			}
			Log.d("MainActivity", "TTS Local: "
					+ mTTS.getLanguage().getLanguage());
		} else {
			Log.e("MainActivity", "TTS failed to Initalize");
		}
	}

	private void speakText(CharSequence text) {
		String textToBeSpoken = text.toString();

		if (mTTS.isSpeaking()) {
			mTTS.stop();
		}

		if (textToBeSpoken.length() == 0 || textToBeSpoken == null
				|| textToBeSpoken.equals("")) {
			Toast.makeText(this, "No Text To Speak", Toast.LENGTH_SHORT).show();
		} else {
			mTTS.speak(textToBeSpoken, TextToSpeech.QUEUE_FLUSH, null);
			mStopButton.setEnabled(true);
		}
	}
}
