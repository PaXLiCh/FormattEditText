package ru.kolotnev.formattedittextexample;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ru.kolotnev.formattedittext.CurrencyEditText;
import ru.kolotnev.formattedittext.DecimalEditText;
import ru.kolotnev.formattedittext.MaskedEditText;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Masked input test
		final MaskedEditText editTextMasked = findViewById(R.id.edit_masked);
		final TextView textViewMasked = findViewById(R.id.text_masked);
		editTextMasked.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				/* do nothing */
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				/* do nothing */
			}

			@Override
			public void afterTextChanged(Editable editable) {
				textViewMasked.setText(
						getString(
								R.string.text_masked_test,
								editTextMasked.getMask(),
								editTextMasked.getText(true)));
			}
		});

		textViewMasked.setText(getString(
				R.string.text_masked_test,
				editTextMasked.getMask(),
				editTextMasked.getText(true)));

		// Decimal input test
		final DecimalEditText editTextDecimal = findViewById(R.id.edit_decimal);
		final TextView textViewDecimal = findViewById(R.id.text_decimal);
		editTextDecimal.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				/* do nothing */
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				/* do nothing */
			}

			@Override
			public void afterTextChanged(Editable editable) {
				textViewDecimal.setText(getString(
						R.string.text_decimal_test,
						editTextDecimal.getDecimalRounding(),
						editTextDecimal.getValue()));
			}
		});

		textViewDecimal.setText(getString(
				R.string.text_decimal_test,
				editTextDecimal.getDecimalRounding(),
				editTextDecimal.getValue()));

		// Integer input test
		final DecimalEditText editTextInteger = findViewById(R.id.edit_integer);
		final TextView textViewInteger = findViewById(R.id.text_integer);
		editTextInteger.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				/* do nothing */
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				/* do nothing */
			}

			@Override
			public void afterTextChanged(Editable editable) {
				textViewInteger.setText(getString(
						R.string.text_integer_test,
						editTextInteger.getDecimalRounding(),
						editTextInteger.getValue().longValue()));
			}
		});

		textViewInteger.setText(getString(
				R.string.text_integer_test,
				editTextInteger.getDecimalRounding(),
				editTextInteger.getValue().intValue()));

		// Currency input test
		final CurrencyEditText editTextCurrency = findViewById(R.id.edit_currency);
		final TextView textViewCurrency = findViewById(R.id.text_currency);
		editTextCurrency.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				/* do nothing */
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				/* do nothing */
			}

			@Override
			public void afterTextChanged(Editable editable) {
				textViewCurrency.setText(getString(
						R.string.text_currency_test,
						editTextCurrency.getLocale(),
						editTextCurrency.getCurrency(),
						editTextCurrency.getValue()));
			}
		});

		textViewCurrency.setText(getString(
				R.string.text_currency_test,
				editTextCurrency.getLocale(),
				editTextCurrency.getCurrency(),
				editTextCurrency.getValue()));
	}
}
