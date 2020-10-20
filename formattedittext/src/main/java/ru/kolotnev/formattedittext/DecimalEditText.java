package ru.kolotnev.formattedittext;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.PluralsRes;
import androidx.appcompat.widget.AppCompatEditText;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Input field for decimals.
 * <p>
 * Kolotnev Pavel, 2015-2020
 */
@SuppressWarnings("unused")
public class DecimalEditText extends AppCompatEditText {
	public static final String TAG = "DecimalEditText";
	@NonNull
	private BigDecimal value = BigDecimal.ZERO;
	@NonNull
	private BigDecimal min = BigDecimal.ZERO;
	@NonNull
	private BigDecimal max = BigDecimal.ZERO;
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
			if (str.length() > 0 && str.equals(current)) {
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

		setInputType(getInputType()
				| InputType.TYPE_CLASS_NUMBER
				| InputType.TYPE_NUMBER_FLAG_DECIMAL);
		addTextChangedListener(textWatcher);

		setText(getText());
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
		clampCurrentValue();
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
	 * 		Amount of fraction digits (must be greater than or equal to zero).
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

	/**
	 * Sets the limits for value which can be entered (both ZERO limits means no limits).
	 *
	 * @param min
	 * 		Minimal value, default ZERO.
	 * @param max
	 * 		Maximal value, default ZERO.
	 */
	public void setLimits(@NonNull BigDecimal min, @NonNull BigDecimal max) {
		this.min = min;
		this.max = max;
	}

	private void parseValue(@NonNull String str) {
		// Remove all non numeric chars
		String cleanString = str.replaceAll("((?<!^)[\\D]|^[^\\d+-]|([+-]$)|(^\\D+$))", "");
		if (cleanString.length() > 0) {
			// Construct decimal value as only signed integer value and
			// move fraction point according decimalRounding value.
			try {
				value = new BigDecimal(cleanString)
						.setScale(decimalRounding, BigDecimal.ROUND_FLOOR)
						.divide(BigDecimal.valueOf(Math.pow(10, decimalRounding)), BigDecimal.ROUND_FLOOR);
			} catch (NumberFormatException e) {
				Log.e(TAG, "Failed to convert " + cleanString + " to decimal. Parameter " + str);
				e.printStackTrace();
			}
			clampCurrentValue();
		} else {
			// Input field have no any digit
			value = BigDecimal.ZERO;
		}
	}

	private void clampCurrentValue() {
		if (min.compareTo(max) != 0) {
			if (max.compareTo(value) < 0)
				value = max;
			if (min.compareTo(value) > 0)
				value = min;
		}
	}

	private void updateText() {
		DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
		df.setMinimumFractionDigits(decimalRounding);
		df.setMaximumFractionDigits(decimalRounding);
		df.setRoundingMode(RoundingMode.FLOOR);
		String formattedClear = df.format(value);
		if (pluralLabel == 0) {
			current = formattedClear;
		} else {
			current = getResources().getQuantityString(pluralLabel, value.intValue(), formattedClear);
		}
		int pos;
		if (current.contains(formattedClear)) {
			pos = current.indexOf(formattedClear) + formattedClear.length();
		} else {
			pos = 0;
		}

		removeTextChangedListener(textWatcher);
		setText(current);
		setSelection(pos);
		addTextChangedListener(textWatcher);
	}
}
