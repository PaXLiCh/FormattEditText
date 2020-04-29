package ru.kolotnev.formattedittext;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

/**
 * Text field for currency.
 * <p>
 * Kolotnev Pavel, 2015-2020
 */
@SuppressWarnings("unused")
public class CurrencyEditText extends AppCompatEditText {
	private static final String TAG = "CurrencyEditText";

	@NonNull
	private Locale locale;
	@NonNull
	private Currency currency;
	@NonNull
	private BigDecimal value = BigDecimal.ZERO;
	private String current = "";

	private final TextWatcher textWatcher = new TextWatcher() {
		private boolean isDeleting;

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			isDeleting = after <= 0 && count > 0;
			/*if (!s.toString().equals(current)) {
				removeTextChangedListener(this);
				String cleanString = s.toString().replaceAll("[^\\d]", "");
				setText(cleanString);
				addTextChangedListener(this);
			}*/
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			/* do nothing */
		}

		@Override
		public void afterTextChanged(Editable s) {
			//Log.i(TAG, "::afterTextChanged:" + "Editable " + s + "; Current " + current);
			String str = s.toString();
			if (str.length() > 0 && str.equals(current)) {
				return;
			}

			parseValue(str);
			updateText();
		}
	};

	public CurrencyEditText(Context context) {
		this(context, null, null, null);
	}

	public CurrencyEditText(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, null, null);
	}

	public CurrencyEditText(Context context, @Nullable AttributeSet attrs, @Nullable Locale locale) {
		this(context, attrs, locale, null);
	}

	public CurrencyEditText(Context context, @Nullable AttributeSet attrs, @Nullable Locale locale, @Nullable Currency currency) {
		super(context, attrs);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CurrencyEditText);
		final int n = a.getIndexCount();
		for (int i = 0; i < n; ++i) {
			int at = a.getIndex(i);
			if (at == R.styleable.CurrencyEditText_locale) {
				String lang = a.getString(at);
				if (lang != null) {
					locale = new Locale(lang);
				}
			} else if (at == R.styleable.CurrencyEditText_currency) {
				currency = Currency.getInstance(a.getString(at));
			}
		}
		a.recycle();

		if (locale == null) {
			locale = Locale.getDefault();
		}
		this.locale = locale;
		if (currency == null) {
			currency = NumberFormat.getCurrencyInstance(locale).getCurrency();
		}
		if (currency == null) {
			currency = Currency.getInstance("USD");
		}
		this.currency = currency;

		setInputType(getInputType()
				| InputType.TYPE_CLASS_NUMBER
				| InputType.TYPE_NUMBER_FLAG_DECIMAL);
		addTextChangedListener(textWatcher);

		setText(getText());
	}

	/**
	 * Get current decimal value for currency.
	 *
	 * @return Decimal value.
	 */
	@NonNull
	public BigDecimal getValue() {
		return value;
	}

	/**
	 * Sets new value for edit field.
	 *
	 * @param bigDecimal
	 * 		New decimal value.
	 */
	public void setValue(@NonNull BigDecimal bigDecimal) {
		value = bigDecimal;
		updateText();
	}

	/**
	 * Gets current locale for text field.
	 *
	 * @return Current locale.
	 */
	@NonNull
	public Locale getLocale() {
		return locale;
	}

	/**
	 * Sets locale for text field.
	 *
	 * @param locale
	 * 		New locale.
	 */
	public void setLocale(@NonNull Locale locale) {
		this.locale = locale;
		updateText();
	}

	/**
	 * Returns current currency for text field.
	 *
	 * @return Current currency.
	 */
	@NonNull
	public Currency getCurrency() {
		return currency;
	}

	/**
	 * Sets new currency for text field.
	 *
	 * @param currency
	 * 		New currency.
	 */
	public void setCurrency(@NonNull Currency currency) {
		this.currency = currency;
		updateText();
	}

	/**
	 * Parses value from text in edit field.
	 *
	 * @param str
	 * 		String with digits.
	 */
	private void parseValue(String str) {
		// Remove all non numeric chars
		String cleanString = str.replaceAll("[^\\d]", "");
		if (cleanString.length() > 0) {
			// Construct decimal value as only integer value and move
			// fraction point for two places.
			value = new BigDecimal(cleanString)
					.setScale(2, BigDecimal.ROUND_FLOOR)
					.divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
		} else {
			// Input field have no any digit
			value = BigDecimal.ZERO;
		}
	}

	/**
	 * Returns decimal formatter for specified currency.
	 *
	 * @return Currency formatter for current locale and currency.
	 */
	private DecimalFormat getCurrencyFormatter() {
		NumberFormat.getCurrencyInstance();
		NumberFormat format = NumberFormat.getCurrencyInstance(locale);
		format.setMaximumFractionDigits(currency.getDefaultFractionDigits());
		format.setCurrency(currency);
		return (DecimalFormat) format;
	}

	private void updateText() {
		DecimalFormat formatter = getCurrencyFormatter();

		current = formatter.format(value);

		// Now we need to find clear string without currency symbols
		DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
		symbols.setCurrencySymbol("");
		formatter.setDecimalFormatSymbols(symbols);
		String formattedClear = formatter.format(value);

		// Currency symbols may be placed with spaces (e.g. nbsp) at start or at end of string
		int start = 0;
		if (Character.isSpaceChar(formattedClear.charAt(start)))
			++start;

		int end = formattedClear.length() - 1;
		if (Character.isSpaceChar(formattedClear.charAt(end)))
			--end;

		// Trim spaces (String.trim() may skip most spaces)
		formattedClear = formattedClear.substring(start, end + 1);

		// Set position of cursor at end of clear string in formatted string
		int pos = current.indexOf(formattedClear) + formattedClear.length();

		removeTextChangedListener(textWatcher);
		setText(current);
		setSelection(Math.min(pos, current.length()));
		addTextChangedListener(textWatcher);
	}
}
