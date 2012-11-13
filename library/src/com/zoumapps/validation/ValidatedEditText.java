package com.zoumapps.validation;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import com.zoumapps.validation.R;

import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Patterns;

/**
 * <p>An {@link EditText} that can validate its input and indicate to the user when the input is invalid through an
 * animation.</p>
 *
 * <p>To set a validation criteria either call {@link #setValidationCriteria(String)} to set a regular expression to
 * validate against, or {@link #setValidation} to use
 * a predefined validation, or set the XML attribute {@code zoum:validation_criteria="criteria"}, this requires adding
 * {@code xmlns:zoum="http://schemas.android.com/apk/res/com.zoumapps.validation"} to the XML main tag.</p>
 *
 * <p>To sanitize invalid input implement the {@link com.zoumapps.validation.ValidatedEditText.OnFixTextListener} interface and set the
 * listener with {@link #setOnFixTextListener(com.zoumapps.validation.ValidatedEditText.OnFixTextListener)}.</p>
 * @author Hazem Farahat <Hazem.farahat@gmail.com>
 * @author Dandré Allison <dandre.allison@gmail.com>
 */
public class ValidatedEditText extends EditText {
	
	/**
     * Callback interface that provides an opportunity to sanitize invalid input. This callback will be triggered when
     * the user navigates away from this field.
     */
    public interface OnFixTextListener {
        /**
         * Corrects the specified text to make it valid. This allows the listener to sanitize invalid input.
         * @param invalid_text A string that doesn't pass validation
         * @return Sanitized text based on the given text
         */
        CharSequence onFixText(CharSequence invalid_text);
    }

    /**
     * Predefined validation criterion.
     */
    public static enum Validation {
        /** Criteria: input is not empty */
        NON_EMPTY(0, ".+"),
        /** Criteria: input has valid URL syntax. */
        URL(1, Patterns.WEB_URL.pattern()),
        /** Criteria: input is a #-sign followed by 3 or 6 hexadecimal digits (0-9, a-f, and A-F are the valid digits) */
        HEXIDECIMAL_COLOR(2, "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$"),
        /** Criteria: input only contains letters (upper-case or lower-case A-z) and numbers (0-9), implies not empty */
        ALPHANUMERIC(3, "[a-zA-Z0-9]+"),
        /** Criteria: input only contains letters (upper-case or lower-case A-z and implies not empty) */
        ALPHABET(4, "[a-zA-Z]+"),
        /** Criteria: input only contains numbers (0-9 and implies not empty) */
        NUMBER(5, "[0-9]+"),
        /** Criteria: input has valid e-mail address syntax */
        EMAIL(6, Patterns.EMAIL_ADDRESS.pattern());

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
    public ValidatedEditText(Context context) {
        this(context, null);
    }

    /** Constructor */
    public ValidatedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    /** Constructor */
    public ValidatedEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context, attrs);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        // If the input has changed since the last validation, then it will need to be validated again
        _should_revalidate = true;
    }

	@Override
	protected void onFocusChanged(boolean focused, int direction, Rect previously_focused_rect) {
        super.onFocusChanged(focused, direction, previously_focused_rect);
        // Performs validation if the view loses focus
        if (!focused && !validate() && _on_fix_text_listener != null)
            setText(_on_fix_text_listener.onFixText(getText()))
    }

    @Override
    public boolean onKeyDown(int key_code, KeyEvent event) {
        switch(key_code) {
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (event.hasNoModifiers() && !validate() && _on_fix_text_listener != null)
                    setText(_on_fix_text_listener.onFixText(getText()))
                break;
        }

        return super.onKeyDown(key_code, event);
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
	 * Sets the invalid input animation that shows when the user enters invalid input.
     * @param animation the invalid input animation
     */
    public void setInvalidInputIndicator(Animation animation) {
        _invalid_input_indicator = animation;
    }

    /**
     * Sets the invalid input animation that shows when the user enters invalid input.
     * @param animation_resource the invalid input animation resource ID
     */
    public void setInvalidInputIndicator(int animation_resource) {
        _invalid_input_indicator = AnimationUtils.loadAnimation(getContext(), animation_resource);
    }

    public void setOnFixTextListener(OnFixTextListener on_fix_text_listener) {
        _on_fix_text_listener = on_fix_text_listener;
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
	/** Handles sanitizing invalid input */
	private OnFixTextListener _on_fix_text_listener;
}
