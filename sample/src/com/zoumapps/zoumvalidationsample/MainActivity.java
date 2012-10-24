package com.zoumapps.zoumvalidationsample;

import com.zoumapps.validation.ValidationEditText;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;

public class MainActivity extends Activity {

	private ValidationEditText mValidationEditText;
	private ValidationEditText mValidationEditText2;
	private ValidationEditText mValidationEditText3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mValidationEditText = (ValidationEditText) findViewById(R.id.edittext_validate);
		mValidationEditText2 = (ValidationEditText) findViewById(R.id.edittext_validate2);
		mValidationEditText3 = (ValidationEditText) findViewById(R.id.edittext_validate3);

		// Set validation criteria as it's not set on XML
		mValidationEditText
				.setValidationCriteria(ValidationEditText.VALID_NOT_BLANK);
	}

	public void onButtonValidateClicked(View v) {
		mValidationEditText.validate();
		mValidationEditText2.validate();
		mValidationEditText3.validate();

	}

}
