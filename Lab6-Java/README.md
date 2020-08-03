# CSCD437_TechSupport
Ron Robinson - Katherine Bozin

Shortcomings:
-had to set length restrictions on some inputs (like password and filename) that may rule out some valid input
-used System.in to read inputs, which utilizes File Input Stream that has associated vulnerabilities
-we were unable to finish the c portion. we are both pretty inexperienced with C.

Protections:
-all variables and non-main methods private
-first or last name longer than 50 characters or shorter than 1 character, containing anything other than characters in set [a-z,.'-]
-integers longer than 11 characters (allowing for negative sign) or shorter than one character, containing anything other than digits.
Also checks that entered integer is not greater than max integer value or less than min integer value.
-doesn't write bad data to output file, rather writes that data was bad
-files names that contain invalid characters and don't follow the format of name.extension. limits size of both.
checks that file exists and can be read from in the case of the input file, and written to in the case of the output file.
-sequence '\0' in password, and passwords of length 0
-add salt to password for extra security layer, used secure random for cryptographically strong rng
-salts and hashes second password to compare it to the hash of the first, which we retrieve from the file
-compare passwords a character at a time, so the comparison fails as soon as the passwords start to differ (to protect against malicious second password)
-checks that password and error files don't already exist before creating them
-use big int to store result of integer addition and multiplication to ensure it is large enough to contain it
