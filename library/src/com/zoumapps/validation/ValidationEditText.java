package com.zoumapps.validation;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import com.keepandshare.android.R;

import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An {@link EditText} that can validate its input and indicate to the user when the input is invalid through an
 * animation.
 *
 * To set a validation criteria either call {@link #setValidationCriteria(String)} to set a regular expression to
 * validate against, or {@link #setValidationCriteria(com.zoumappds.validation.ValidationEditText.Validation)} to use
 * a predefined validation, or set the XML attribute {@code zoum:validation_criteria="criteria"}, this requires adding
 * {@code xmlns:zoum="http://schemas.android.com/apk/res/com.zoumapps.validation"} to the XML main tag.
 *
 * @author Dandré Allison
 */
public class ValidationEditText extends EditText {

    /**
     * Predefined validation criterion.
     */
    public static enum Validation {
        /** Criteria: input is not empty */
        NON_EMPTY(0, ".+"),
        /** Criteria: input has approximately valid URL syntax. Only accepts URLs starting with http, https, or ftp */
        URL(1, "^(https?|ftp)://.+\\..+$"),
        /** Criteria: input is a #-sign followed by 3 or 6 hexidecimal digits (0-9, a-f, and A-F are the valid digits) */
        HEXIDECIMAL_COLOR(2, "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$"),
        /** Criteria: input only contains letters (upper-case or lower-case A-z) and numbers (0-9), implies not empty */
        ALPHANUMERIC(3, "[a-zA-Z0-9]+"),
        /** Criteria: input only contains letters (upper-case or lower-case A-z and implies not empty) */
        ALPHABET(4, "[a-zA-Z]+"),
        /** Criteria: input only contains numbers (0-9 and implies not empty) */
        NUMBER(5, "[0-9]+"),
        /** Criteria: input has approximately valid e-mail address syntax */
        EMAIL(6, "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\." 
               + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");

        private Validation(int value, String criteria) {
            _value = value;
            _criteria = criteria;
        }

        /**
         * Creates the criteria for valid input given a value.
         * @param from_value value associated with a {@link Validation}
         * @return the criteria for passing the associated validation
         */
        public static String getCriteria(int from_value) {
            for(Validation validation : EnumSet.allOf(Validation.class))
                if (validation._value == from_value)
                    return validation._criteria;

            throw new IllegalArgumentException(from_value + " is not a valid Validation.");
        }

        // These aren't private to allow ValidatedEditText to access them directly
        final int _value;
        final String _criteria;
    }

    /** Constructor */
    public ValidationEditText(Context context) {
        this(context, null);
    }

    /** Constructor */
    public ValidationEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    /** Constructor */
    public ValidationEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context, attrs);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        // If the input has changed since the last validation, then it will need to be validated again
        _should_revalidate = true;
    }

    /**
     * Sets the validation to a predefined method.
     * @param validation a predefined validation
     * @see Validation
     */
    public void setValidation(Validation validation) {
        // This maintains that the input field is never in a state where it can't validate its input
        if (validation != null)
            setMatcherCriteria(validation._criteria);
        else
            setMatcherCriteria(Validation.NON_EMPTY._criteria);
        _should_revalidate = true;
    }

    /**
     * Sets the criteria the text in the {@linkplain EditText input field} must meet to be valid.
     * @param validation_criteria the regular expression to match against
     */
    public void setValidationCriteria(String validation_criteria) {
        setMatcherCriteria(validation_criteria);
    }

    /**
     * Checks whether the input is valid by comparing it to the validation criteria and <b>displays the result to the
     * user</b>. Indicates to the user through an animation that the input is invalid.
     * @return true if the data is valid false if not
     */
    public boolean showValidity() {
        if (validate())
            return true;

        indicateInvalidInput();
        return false;
    }

    /**
     * Checks whether the input is valid by comparing it to the validation criteria, but <b>doesn't display the result
     * to the user</b>.
     * @return true if the data is valid and false if not
     */
    public boolean checkValidity() {
        return validate();
    }

    /**
     * Initializes the validation decoration to the {@link EditText}. After initialization, the input field can validate
     * its input and indicate when the input is invalid.
     * @param context the given context
     * @param attributes the input field's attributes
     */
    private void init(Context context, AttributeSet attributes) {
        if (attributes != null) {
            final TypedArray a = getContext().obtainStyledAttributes(attributes, R.styleable.ValidatedEditText);
            // Sets the criteria based on the validation method chosen in XML
            setMatcherCriteria(Validation.getCriteria(a.getInt(R.styleable.ValidatedEditText_validation,
                    Validation.NON_EMPTY._value)));
            // Sets the criteria based on the custom criteria specified in XML
            // Notice that this will override a validation method chosen in XML
            final String criteria = a.getString(R.styleable.ValidatedEditText_custom_criteria);
            if (criteria != null)
                setMatcherCriteria(criteria);
            // Sets the invalid input indicator, the default is the provided shake animation
            final int invalid_input_indicator = a.getResourceId(R.styleable.ValidatedEditText_invalid_input_indicator,
                    R.anim.shake);
            _invalid_input_indicator = AnimationUtils.loadAnimation(context, invalid_input_indicator);
            a.recycle();
        } else {
            setMatcherCriteria(Validation.NON_EMPTY._criteria);
            _invalid_input_indicator = AnimationUtils.loadAnimation(context, R.anim.shake);
        }
    }

    /**
     * Checks whether the input is valid by comparing it to the validation criteria.
     * @return true if the data is valid and false if not
     */
    private boolean validate() {
        final boolean is_valid = _should_revalidate
                                    ? _criteria.reset(getText().toString()).matches()
                                    : _criteria.matches();
        _should_revalidate = false;
        return is_valid;
    }

    /**
     * Sets the {@link Matcher} criteria to the given {@link String}
     * @param string the regular expression to match against
     */
    private void setMatcherCriteria(String string) {
        // This maintains that the input field is never in a state where it can't validate its input
        _criteria = _criteria.usePattern(Pattern.compile(string == null? "" : string));
    }

    /**
     * Animates the {@linkplain EditText input field} to indicate that the input is invalid.
     */
    private void indicateInvalidInput() {
        requestFocus();
        startAnimation(_invalid_input_indicator);
    }

    /** The criteria that must be matched by valid input */
    private Matcher _criteria = Pattern.compile(".+").matcher("");
    /** The invalid input animation to show the user where the error exists */
    private Animation _invalid_input_indicator;
    /** Monitors whether the text validation needs to be performed */
    private boolean _should_revalidate = true;
}
