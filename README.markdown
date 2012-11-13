Zoum Validation
================

Zoum validation is a standalone library designed to offer a new custom EditText that is validatable, the EditText animates (default is a shake) if its contents is invalid.

Validation occurs using regular expressions, and for your convenience the library provides some validation methods:

* EMAIL
Validate emails, someone@domain.topdomain

* NUMBER
Validate any integer number

* ALPHABET
Accepts alphabetical charcteres from A - Z and from a - z

* ALPHANUMERIC
Validates alphabet and numbers

* HEXIDECIMAL_COLOR
Validates Hexadecimal colors starting with #, ex. #66EEAB or #777

* URL
Validates any URL starting with http, https, or ftp

* NON_EMPTY
Accepts any non-empty input
	
Usage
=====

## Validation criteria  
### Programatically:  
By calling the `setValidationCriteria(String validation_criteria)` with a regular expression. Or by calling the 
`setValidation(Validation validation)` with a predefined validation method.

EX: `myValidatedEditText.setValidationCriteria("^[0-9]*(1|3|5|7|9)$");` or 
`myValidatedEditText.setValidation(Validation.EMAIL);`

### From XML:  
The criteria can be also set from the XML layout file, but requires the library namespace at your XML is included

at the top node: `xmlns:zoum="http://schemas.android.com/apk/res-auto"`.

Then use this in your layout, note the `zoum:custom_criteria` attribute.  

    <com.zoumapps.validated.ValidationEditText
        android:id="@+id/edittext_validate2"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:ems="10"
        android:padding="8dp"
        zoum:custom_criteria="^[0-9]*(1|3|5|7|9)$" />

## Invalid Input Indicator
### Programatically:
By calling the `setInvalidInputIndicator(Animation animation)`. Or by calling the 
`setInvalidInputIndicator(int animation_resource)` with the ID to an animation resource.

### From XML:
The invalid input animation can also be set in the XML layout file, using the `zoum:invalid_input_indicator` attribute.

    <com.zoumapps.validated.ValidationEditText
        android:id="@+id/edittext_validate2"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:ems="10"
        android:padding="8dp"
        zoum:invalid_input_indicator="@anim/my_custom_animation" />

## Sanitization
Provides callback to automatically sanitize input text when the user navigates away from the field. Implement `ValidatedEditText.OnFixTextListener`'s `onFixText(CharSequence)` to return a sanitized version of the given text. Then set this listener with `myValidatedEditText.setOnFixTextListener(myOnFixTextListener)`.

Project contents
================
* /library - Which contains the library code itself, you will need to import this library into your workspace.
* /sample - A sample application that uses the library.
* /README - This readme file.


Developed By
============

* Hazem Farahat | [Google+](http://profiles.google.com/HazemFarahat) - [Twitter](http://twitter.com/hazemfarahat)



License
=======

    Copyright 2012 Hazem Farahat

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.