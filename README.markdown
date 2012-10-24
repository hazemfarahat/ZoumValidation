Zoum Validation
================

Zoum validation is a standalone library designed to offer a new custom EditText that is validatable, the EditText shakes if its contents is invalid.

Validation occours using regular expressions, and for your convenience the library contains some constant validation criteria types:

* VALID_EMAIL
Validate emails, someone@domain.topdomain

* VALID_INTEGER
Validate any integer number

* VALID_ALPHABET
Accepts alphabetical charcteres from A - Z and from a - z

* VALID_ALPHANUMERIC
Validates alphabet and numbers

* VALID_HEXCOLOR
Validates Hexadecimal colors starting with #, ex. #66EEAB

* VALID_URL
Validates any url starting with http or ftp

* VALID_NOT_BLANK
Checks if the field is not blank
	
Usage
=====

Using the validation criteria can be done in two ways  

### Programatically:  
By calling the `setValidationCriteria(String arg)` with a regex string or one of the validation constants mentioned above.

EX: `myValidationEditText.setValidationCriteria(ValidationEditText.VALID_EMAIL);`

### From XML:  
The criteria can be also set from the xml layout file but make sure to include the library namespace at your xml

at the top node add:  `xmlns:zoum="http://schemas.android.com/apk/res-auto"`

and then use this in your layout, note the `zoum:validation_criteria` attribute.  

		<com.zoumapps.validation.ValidationEditText
        android:id="@+id/edittext_validate2"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:ems="10"
        android:padding="8dp"
        zoum:validation_criteria="@string/VALID_EMAIL" />

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