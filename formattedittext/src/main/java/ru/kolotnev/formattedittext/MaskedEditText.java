package ru.kolotnev.formattedittext;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

/**
 * Masked input field.
 * <p>
 * Based on sources from Reinaldo Arrosi
 * https://github.com/reinaldoarrosi/MaskedEditText
 */
@SuppressWarnings("unused")
public class MaskedEditText extends AppCompatEditText {
	public static final String TAG = "MaskedEditText";
	private static final char NUMBER_MASK = '9';
	private static final char ALPHA_MASK = 'A';
	private static final char ALPHANUMERIC_MASK = '*';
	private static final char CHARACTER_MASK = '?';
	private static final char ESCAPE_CHAR = '\\';
	private static final char PLACEHOLDER = ' ';

	@NonNull
	private String mask;
	@NonNull
	private String placeholder;

	public MaskedEditText(Context context) {
		this(context, "");
	}

	public MaskedEditText(Context context, String mask) {
		this(context, mask, PLACEHOLDER);
	}

	public MaskedEditText(Context context, String mask, char placeholder) {
		this(context, null, mask, placeholder);
	}

	public MaskedEditText(Context context, AttributeSet attr) {
		this(context, attr, "");
	}

	public MaskedEditText(Context context, AttributeSet attr, String mask) {
		this(context, attr, "", PLACEHOLDER);
	}

	public MaskedEditText(Context context, AttributeSet attr, @NonNull String mask, char placeholder) {
		super(context, attr);

		TypedArray a = context.obtainStyledAttributes(attr, R.styleable.MaskedEditText);
		final int n = a.getIndexCount();
		for (int i = 0; i < n; ++i) {
			int at = a.getIndex(i);
			if (at == R.styleable.MaskedEditText_mask) {
				String m = a.getString(at);
				if (mask.isEmpty() && m != null) {
					mask = m;
				}
			} else if (at == R.styleable.MaskedEditText_placeholder) {
				String pl = a.getString(at);
				if (pl != null && pl.length() > 0 && placeholder == PLACEHOLDER) {
					placeholder = pl.charAt(0);
				}
			}
		}
		a.recycle();

		this.mask = mask;
		this.placeholder = String.valueOf(placeholder);

		TextWatcher textWatcher = new TextWatcher() {
			private boolean updating = false;

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
				if (updating || MaskedEditText.this.mask.length() == 0)
					return;

				updating = true;

				stripMaskChars(s);
				formatMask(s);

				updating = false;
			}
		};
		addTextChangedListener(textWatcher);

		if (mask.length() > 0)
			setText(getText()); // sets the text to create the mask
	}

	/**
	 * Returns the current mask.
	 *
	 * @return String used as mask for formatting text in input field.
	 */
	@NonNull
	public String getMask() {
		return mask;
	}

	/**
	 * Sets the new mask and updates the text in field.
	 *
	 * @param mask
	 * 		New mask.
	 */
	public void setMask(@NonNull final String mask) {
		this.mask = mask;
		setText(getText());
	}

	/**
	 * Returns placeholder char.
	 *
	 * @return Char which currently used as placeholder.
	 */
	public char getPlaceholder() {
		return placeholder.charAt(0);
	}

	/**
	 * Sets the new placeholder and reformats current value.
	 *
	 * @param placeholder
	 * 		New placeholder char.
	 */
	public void setPlaceholder(char placeholder) {
		this.placeholder = String.valueOf(placeholder);
		setText(getText());
	}

	/**
	 * Returns current value in input field.
	 *
	 * @param removeMask
	 * 		Must be value returned without mask.
	 *
	 * @return Current value.
	 */
	@Nullable
	public Editable getText(boolean removeMask) {
		if (removeMask) {
			SpannableStringBuilder value = new SpannableStringBuilder(getText());
			stripMaskChars(value);
			return value;
		} else {
			return getText();
		}
	}

	private void formatMask(@NonNull Editable value) {
		InputFilter[] inputFilters = value.getFilters();
		value.setFilters(new InputFilter[0]);

		int indexInMask = 0;
		int indexInText = 0;
		int maskLength = 0;
		int inputLength = 0;
		boolean treatNextCharAsLiteral = false;
		boolean maskIsNotNumeric = false;

		Object selection = new Object();
		value.setSpan(selection, Selection.getSelectionStart(value), Selection.getSelectionEnd(value), Spanned.SPAN_MARK_MARK);

		while (indexInMask < mask.length()) {
			char charInMask = mask.charAt(indexInMask);
			if (!treatNextCharAsLiteral && isMaskChar(charInMask)) {
				// Found mask character, try to parse text
				maskIsNotNumeric |= charInMask != NUMBER_MASK;
				if (indexInText >= value.length()) {
					// Add trailing placeholders
					value.insert(indexInText, placeholder);
					value.setSpan(new PlaceholderSpan(), indexInText, indexInText + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					++indexInText;
					++inputLength;
					++maskLength;
					++indexInMask;
				} else if (!matchMask(charInMask, value.charAt(indexInText))) {
					// Skip bad character in text
					value.delete(indexInText, indexInText + 1);
				} else {
					// Character in text is acceptable, go to next character in mask
					++indexInText;
					++inputLength;
					++maskLength;
					++indexInMask;
				}
			} else if (!treatNextCharAsLiteral && charInMask == ESCAPE_CHAR) {
				// Next character in mask must be escaped
				treatNextCharAsLiteral = true;
				++indexInMask;
			} else {
				// Found a literal or escaped character in mask
				value.insert(indexInText, String.valueOf(charInMask));
				value.setSpan(new LiteralSpan(), indexInText, indexInText + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				treatNextCharAsLiteral = false;

				++indexInText;
				++maskLength;
				++indexInMask;
			}
		}

		while (value.length() > maskLength) {
			int pos = value.length() - 1;
			value.delete(pos, pos + 1);
		}

		Selection.setSelection(value, value.getSpanStart(selection), value.getSpanEnd(selection));
		value.removeSpan(selection);

		value.setFilters(inputFilters);

		int newInputType = inputLength > 0
				? (maskIsNotNumeric
				? InputType.TYPE_CLASS_TEXT
				: InputType.TYPE_CLASS_NUMBER)
				: 0;
		if (getInputType() != newInputType)
			setInputType(newInputType);
	}

	private void stripMaskChars(@NonNull Editable value) {
		PlaceholderSpan[] pSpans = value.getSpans(0, value.length(), PlaceholderSpan.class);
		LiteralSpan[] literalSpans = value.getSpans(0, value.length(), LiteralSpan.class);

		for (PlaceholderSpan s : pSpans) {
			value.delete(value.getSpanStart(s), value.getSpanEnd(s));
		}

		for (LiteralSpan s : literalSpans) {
			value.delete(value.getSpanStart(s), value.getSpanEnd(s));
		}
	}

	private boolean matchMask(char mask, char value) {
		return mask == CHARACTER_MASK
				|| (mask == ALPHA_MASK && Character.isLetter(value))
				|| (mask == NUMBER_MASK && Character.isDigit(value))
				|| (mask == ALPHANUMERIC_MASK && (Character.isDigit(value) || Character.isLetter(value)));
	}

	private boolean isMaskChar(char mask) {
		switch (mask) {
			case NUMBER_MASK:
			case ALPHA_MASK:
			case ALPHANUMERIC_MASK:
			case CHARACTER_MASK:
				return true;
		}

		return false;
	}

	private class PlaceholderSpan {
		// this class is used just to keep track of placeholders in the text
	}

	private class LiteralSpan {
		// this class is used just to keep track of literal chars in the text
	}
}
