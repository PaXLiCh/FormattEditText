package ru.kolotnev.formattedittextexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import ru.kolotnev.formattedittext.MaskedEditText;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final MaskedEditText m = (MaskedEditText)findViewById(R.id.maskEdit);

		TextView txt = (TextView)findViewById(R.id.textView);
		txt.setText("Mask: " + m.getMask());

		Button b1 = (Button)findViewById(R.id.button1);
		Button b2 = (Button)findViewById(R.id.button2);

		b1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(MainActivity.this, "Text without mask: " + m.getText(true), Toast.LENGTH_LONG).show();
			}
		});

		b2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(MainActivity.this, "Text with mask: " + m.getText(false), Toast.LENGTH_LONG).show();
			}
		});
	}
}
