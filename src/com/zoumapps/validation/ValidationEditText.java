package com.zoumapps.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

;
/**
 * An editable text view extending {@link EditText} to allow it to be validated, When the EditText
 * is not valid the view shakes to deny the value it contains.
 * 
 * To set a validation criteria either call {@code setValidationCriteria(String s)} to set a regular
 * expression to validate upon (Note that some default regular expression can be found as constants in ValidationEditText View)
 * or set the xml attribute zoum:validation_criteria="criteria_string"
 * while adding {@code xmlns:zoum="http://schemas.android.com/apk/res/com.zoumapps.validation" } to the xml
 * main tag, also some default regular expression can be found in xml under @string/VALID_*
 * 
 * @author Hazem Farahat <Hazem.farahat@gmail.com>
 */
public class ValidationEditText extends EditText {

  /**
   * Constant that represents a REGEX that matches email addresses
   */
  public static final String VALID_EMAIL = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
      + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+";

  /**
   * Constant that represents a REGEX that matches all valid integers
   */
  public static final String VALID_INTEGER = "[0-9]+";

  /**
   * Constant that represents a REGEX that matches all alphabet
   */
  public static final String VALID_ALPHABET = "[a-zA-Z]+";

  /**
   * Constant that represents a REGEX that matches alphanumerics
   */
  public static final String VALID_ALPHANUMERIC = "[a-zA-Z0-9]+";

  /**
   * Constant that represents a REGEX that matches all HEX colors starting with a #
   */
  public static final String VALID_HEXCOLOR = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";

  /**
   * Constant that represents a REGEX that matches all http, https and ftp urls
   */
  public static final String VALID_URL = "^(https?|ftp)://.+\\..+$";

  /**
   * Constant that represents a REGEX that matches any non blank text
   */
  public static final String VALID_NOT_BLANK = ".+";

  private Animation mShakeAnimation;
  private String mCriteria;

  private static final String TAG = "com.zoumapps.validation.ValidationEditText";

  public ValidationEditText(Context context) {
    super(context);
    init(context, null);
  }

  public ValidationEditText(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init(context, attrs);
  }

  public ValidationEditText(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs) {
    this.mShakeAnimation = AnimationUtils.loadAnimation(context, R.anim.shake);
    if (attrs != null) {
      TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ValidationEditText);
      this.mCriteria = a.getString(R.styleable.ValidationEditText_validation_criteria);
    }

  }

  private void shake() {
    requestFocus();
    this.startAnimation(mShakeAnimation);
  }

  /**
   * Sets the validation criteria (REGEX) to validate against, this must be called before calling
   * validate()
   * 
   * @param s
   *          the regular expression to validate against could be any of the constants existing in
   *          this class starting with VALID_ or you could supply your own regex
   */
  public void setValidationCriteria(String s) {
    mCriteria = s;
  }

  /**
   * Checks that the view contains valid data by comparing it to the validation criteria
   * setValidationCriteria must be called before calling this function or the criteria should be set
   * through the xml layout file, if not a nullPointerException will be thrown if the value is not
   * valid the view shakes in denial
   * 
   * @return true if the data is valid and false if not
   */
  public boolean validate() {
    if (mCriteria == null) {
      throw new NullPointerException(
          TAG
              + " Validation criteria is not set, setValidationCriteria(String s) must be called before trying to Validate");
    }
    String s = getText().toString();
    Pattern patt = Pattern.compile(mCriteria);
    Matcher matcher = patt.matcher(s);
    boolean matches = matcher.matches();
    if (matches) {
      return true;
    } else {
      shake();
      return false;
    }

  }
}
