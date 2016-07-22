package ru.kolotnev.formattedittext;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.PluralsRes;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Input field for decimals.
 * <p/>
 * Kolotnev Pavel, 2015-2016
 */
@SuppressWarnings("unused")
public class DecimalEditText extends AppCompatEditText {
	private BigDecimal value = BigDecimal.ZERO;
	private String current;
	private int decimalRounding = 3;
	@PluralsRes
	private int pluralLabel = 0;
	private final TextWatcher textWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			/* do nothing */
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			/* do nothing */
		}

		@Override
		public void afterTextChanged(Editable s) {
			String str = s.toString();

			if (str.length() == 0) {
				return;
			}

			if (str.equals(current)) {
				return;
			}

			parseValue(str);
			updateText();
		}
	};

	public DecimalEditText(Context context) {
		this(context, null);
	}

	public DecimalEditText(Context context, AttributeSet attrs) {
		super(context, attrs);

		addTextChangedListener(textWatcher);
		//setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DecimalEditText);
		final int n = a.getIndexCount();
		for (int i = 0; i < n; ++i) {
			int at = a.getIndex(i);
			if (at == R.styleable.DecimalEditText_plural) {
				pluralLabel = a.getResourceId(at, pluralLabel);
			} else if (at == R.styleable.DecimalEditText_rounding) {
				decimalRounding = a.getInt(at, decimalRounding);
			}
		}
		a.recycle();
	}

	/**
	 * Returns decimal value from entered text.
	 *
	 * @return Decimal value.
	 */
	@NonNull
	public BigDecimal getValue() {
		return value;
	}

	/**
	 * Sets new value and updates view.
	 *
	 * @param value
	 * 		New value of input field.
	 */
	public void setValue(@NonNull BigDecimal value) {
		this.value = value;
		updateText();
	}

	/**
	 * Gets current amount of fraction digits.
	 *
	 * @return Amount of fraction digits.
	 */
	public int getDecimalRounding() {
		return decimalRounding;
	}

	/**
	 * Sets amount of fraction digits for formatting and identifying decimal.
	 *
	 * @param decimalRounding
	 * 		Amount of fraction digits (must be >= 0).
	 */
	public void setDecimalRounding(final int decimalRounding) {
		this.decimalRounding = decimalRounding;
		if (this.decimalRounding < 0)
			this.decimalRounding = 0;
		parseValue(current);
		updateText();
	}

	/**
	 * Gets resource ID of plural for formatting view of input field.
	 *
	 * @return ID of current plural in resources.
	 */
	@PluralsRes
	public int getPluralResource() {
		return pluralLabel;
	}

	/**
	 * Sets plural for formatting of current value with label and updates view.
	 *
	 * @param pluralResource
	 * 		Resource ID of plural strings (don't use plurals with numbers!).
	 */
	public void setPluralResource(@PluralsRes final int pluralResource) {
		pluralLabel = pluralResource;
		updateText();
	}

	/**
	 * Set whole format of edit text field for displaying decimal value.
	 *
	 * @param decimalRounding
	 * 		Amount of fraction digits.
	 * @param pluralResource
	 * 		Resource ID of plural strings (don't use plurals with numbers!).
	 */
	public void setFormat(int decimalRounding, @PluralsRes int pluralResource) {
		this.decimalRounding = decimalRounding;
		if (this.decimalRounding < 0)
			this.decimalRounding = 0;
		this.pluralLabel = pluralResource;
		updateText();
	}

	private void parseValue(String str) {
		// Remove all non numeric chars
		String cleanString = str.replaceAll("[^\\d]", "");
		if (cleanString.length() > 0) {
			// Construct decimal value as only integer value and move
			// fraction point according decimalRounding value.
			value = new BigDecimal(cleanString)
					.setScale(decimalRounding, BigDecimal.ROUND_FLOOR)
					.divide(new BigDecimal(Math.pow(10, decimalRounding)), BigDecimal.ROUND_FLOOR);
		} else {
			// Input field have no any digit
			value = BigDecimal.ZERO;
		}
	}

	private void updateText() {
		DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
		df.setMinimumFractionDigits(decimalRounding);
		df.setMaximumFractionDigits(decimalRounding);
		df.setRoundingMode(RoundingMode.FLOOR);
		String formattedClear = df.format(value);
		if (pluralLabel > 0) {
			current = getResources().getQuantityString(pluralLabel, value.intValue(), formattedClear);
		} else {
			current = formattedClear;
		}
		int pos = current.indexOf(formattedClear) + formattedClear.length();

		removeTextChangedListener(textWatcher);
		setText(current);
		setSelection(pos);
		addTextChangedListener(textWatcher);
	}
}
