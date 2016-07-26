# Formatted EditText

Simple implementations of a EditText widgets, which can help to visualise inputted text. Classes are inherited from AppCompatEditText.

**MaskedEditText** based on code of [Reinaldo Arrosi](https://github.com/reinaldoarrosi/MaskedEditText)


## Integration

Add the library project or grab to build.gradle:
```groovy
compile 'ru.kolotnev:formatt-edit-text:0.3'
```
or plain maven:
```maven
<dependency>
  <groupId>ru.kolotnev</groupId>
  <artifactId>formatt-edit-text</artifactId>
  <version>0.3</version>
  <type>pom</type>
</dependency>
```

## Usage

### Masked EditText

Insert the view in XML:

```xml
    <ru.kolotnev.formattedittext.MaskedEditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text=""
        app:mask="+999-9A?*"
        app:placeholder="_"/>
```

#### Properties:

- mask -- mask for text
- placeholder -- character will be shown where an input is expected an by default the placeholder char is a white-space

#### Methods:

**setMask(String mask)**
This is used to set a mask. MaskedEditText will try to match the current text with the new mask, so if you change the mask and your text is still valid you'll not lose it.

**getMask()**
This method returns the current mask.

**setPlaceholder(char placeholder)**
This is used to set the placeholder character. This character is shown where an input is expected an by default the placeholder char is a white-space

**getPlaceholder()**
This method returns the current character that is being used as the placeholder char.

**getText(boolean removeMask)**
This method is exaclty like getText() except that you're able to pass a parameter that will determine if the value returned will contain the mask characters or not.

#### Mask
The mask is a simple sequence of character where some of these have a special meaning. Any character that does not have a special meaning will be treated as a literal character and will appear as is in the MaskedEditText.

These are the chars that have a special meaning within the mask:
- 9: Numeric mask (this will accept only numbers to be typed)
- A: Alpha mask (this will accept only alphabetic letters to be typed)
- *: Alphanumeric (this will accept numbers and alphabetic letters to be typed)
- ?: Character mask (this will accept anything to be typed)

Examples:
- phone mask: (999) 999-9999
- money: $999,999,999.99
- random valid mask: (A)?*99A-9++*??

If you need one of these special chars to be treated as a literal char you can use the escape char '\' in front of it:

Example: Suppose that in the phone mask we need to add a preceding digit that will always be 9, to do this we can change the mask like this:
- phone mask: \9(999) 999-9999

If you need to display the escape char as a literal char just double the escape char like this:
- phone mask: \\\\(999) 999-9999


### Decimal EditText

Insert the view in XML:

```xml
    <ru.kolotnev.formattedittext.DecimalEditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:inputType="numberSigned"
        app:plural="@plurals/plural_decimal_test_decimal"
        app:rounding="3"/>
```

#### Properties:

- plural -- plural resource
- rounding -- amount of digits after comma (zero for integer)

#### Methods:

**BigDecimal getValue()**
Returns decimal value from entered text.

**setValue(BigDecimal value)**
Sets new value and updates view.

**int getDecimalRounding()**
Gets current amount of fraction digits.

**setDecimalRounding(int decimalRounding)**
Sets amount of fraction digits for formatting and identifying decimal.

**int getPluralResource()**
Gets resource ID of plural for formatting view of input field.

**setPluralResource(int pluralResource)**
Sets plural for formatting of current value with label and updates view.

**setFormat(int decimalRounding, int pluralResource)**
Set whole format of edit text field for displaying decimal value.

**setLimits(BigDecimal min, BigDecimal max)**
Sets the limits for value which can be entered (both ZERO limits means no limits).


### Currency EditText

Insert the view in XML:
```xml
    <ru.kolotnev.formattedittext.CurrencyEditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:currency="USD"
        app:locale="fr"/>
```

#### Properties:

- currency -- currency code in ISO 4217
- locale -- locale code in ISO 639

#### Methods:

**BigDecimal getValue()**
Get current decimal value for currency.

**setValue(BigDecimal bigDecimal)**
Sets new value for edit field.

**Locale getLocale()**
Gets current locale for text field.

**setLocale(Locale locale)**
Sets locale for text field.

**Currency getCurrency()**
Returns current currency for text field.

**setCurrency(Currency currency)**
Sets new currency for text field.


## License
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
