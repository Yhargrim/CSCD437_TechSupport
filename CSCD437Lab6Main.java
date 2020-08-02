/*
 * Ron Robinson III - Katherine Bozin
 * Tech Support
 * CSCD437-U20
 * 
 * TO-DO:
 * Ensure integer overflow causes no issues - 2,147,483,647(8)
 * Encrypt the password via hash/salt (possibly verify after?)
 * Read-in from input file
 * Write everything to output file
 * 
 * 
*/

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CSCD437Lab6Main {

	private static ArrayList<String> mErrors, mCorrect;
	private static boolean mNameCorrect, mNumberCorrect, mInFileCorrect, mOutFileCorrect, mPasswordCorrect;
	private static File mInput, mOutput;
	private static String mFirst, mLast, mNum1, mNum2, mPassword;
	
	public static void main(String[] args) {
		mErrors=new ArrayList<>();
		mCorrect=new ArrayList<>();
		mNameCorrect=false;
		mNumberCorrect=false;
		mInFileCorrect=false;
		mOutFileCorrect=false;
		mPasswordCorrect=false;
		mInput=null;
		mOutput=null;

		Scanner in = new Scanner(System.in);
		name("first", in);
		name("last", in);

		number("first", in);
		number("second", in);
		
		openFile("input", in);
		openFile("output", in);

		password(in);
		reenterPassword(in);

		writeToOutput();
		
		in.close();
	}

	private static void name(final String which, final Scanner in) {
		System.out.print("Enter "+which+" name: ");
		String name=in.nextLine();

		String namePattern = "^[a-z,.'-]{1,50}$";
		Pattern pattern = Pattern.compile(namePattern);
		Matcher match = pattern.matcher(name);
		if(match.matches()) {
			System.out.println("Value entered for "+which+" name: "+name+"."); //for debugging
			mCorrect.add("Value entered for "+which+" name: "+name+".");
			mNameCorrect=true;
			if(which.equals("first")) mFirst=name;
			else mLast=name;
		} else {
			System.out.println("Error when reading "+which+" name. Entered bad value '"+name+"'."); //for debugging
			mErrors.add("Error when reading "+which+" name. Entered bad value '"+name+"'.");
		}
	}
	
	private static void number(final String which, final Scanner in) {
		System.out.print("Enter "+which+" integer:");
		String number=in.nextLine();

		String numberPattern = "^(-?\\d+){1,11}$";
		Pattern pattern = Pattern.compile(numberPattern);
		Matcher match = pattern.matcher(number);

		if(match.matches()) {
			BigInteger bigInt=new BigInteger(number);
			if(bigInt.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) >= 0 &&
					bigInt.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) <= 0) {
				System.out.println("Value entered for "+which+" integer: "+number+"."); //for debugging
				mCorrect.add("Value entered for "+which+" integer: "+number+".");
				mNumberCorrect=true;
				if(which.equals("first")) mNum1=number;
				else mNum2=number;
			}
		} else {
			System.out.println("Error when reading "+which+" number. Entered bad value '"+number+"'."); //for debugging
			mErrors.add("Error when reading "+which+" number. Entered bad value '"+number+"'.");
		}

	}

	private static void openFile(final String mode, final Scanner in) {
		System.out.print("Enter "+mode+" file name: ");
		String fileName = in.nextLine();

		String filePattern = "^([\\w,-. ]+){1,200}\\.([A-Za-z]){1,20}$";
		Pattern pattern = Pattern.compile(filePattern);
		Matcher match = pattern.matcher(fileName);

		if (match.matches()) {
			File file = new File(fileName);
			if (file.exists()) {
				if (mode.equals("input") && file.canRead()) {
					mInput=file;
					mInFileCorrect=true;
				} else fileError(mode, fileName);
				if (mode.equals("output") && file.canWrite()) {
					mOutput=file;
					mOutFileCorrect=true;
				} else fileError(mode, fileName);
			} else fileError(mode, fileName);
		} else fileError(mode, fileName);
	}

	private static void fileError(String mode, String fileName) {
		System.out.println("Error when reading "+mode+" file. Entered bad value '"+fileName+"'."); //for debugging
		mErrors.add("Error when reading "+mode+" file. Entered bad value '"+fileName+"'.");
	}
	
	private static void password(final Scanner in) {
		System.out.print("Enter password: ");
		String password = in.nextLine();

		String passwordPattern = "^((?!\\\\0).){1,128}$";
		Pattern pattern = Pattern.compile(passwordPattern);
		Matcher match = pattern.matcher(password);

		if(match.matches()) {
			mPasswordCorrect=true;
			hashPassword(password);
		}
		else {
			System.out.println("Error when reading password. Entered bad value '"+password+"'."); //for debugging
			mErrors.add("Error when reading password. Entered bad value '"+password+"'.");
		}
	}

	private static void hashPassword(String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			byte[] messageDigest = md.digest(password.getBytes());
			BigInteger bigInt = new BigInteger(1, messageDigest);
			String hash = bigInt.toString(16);
			while (hash.length()<32) hash = "0".concat(hash);

			SecureRandom random = new SecureRandom();
			byte[] salt = new byte[16];
			random.nextBytes(salt);
			hash= Arrays.toString(salt)+":"+hash;

			System.out.println(hash);//remove later
			storePassword(hash, salt);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace(); //should remove all stack traces for final product
			//should add fallback method if this encryption doesn't work
		}
	}

	private static void storePassword(String hash, byte[] salt) {
		String fileName="password.txt";
		File file=new File(fileName);
		while(file.exists()) {
			fileName="0".concat(fileName);
			file=new File(fileName);
		}
		try { file.createNewFile(); }
		catch (IOException e) {
			System.out.println("Failed to create new file.");
			e.printStackTrace();
		}

		BufferedWriter fout = null;
		try { fout = new BufferedWriter(new FileWriter(file)); }
		catch (IOException e) {
			System.out.println("Failed to open buffered writer to write to file.");
			e.printStackTrace();
		}
		assert fout != null;
		try { fout.write(hash+"\n" + Arrays.toString(salt)); }
		catch (IOException e) {
			System.out.println("Failed to write to file.");
			e.printStackTrace();
		}
		try { fout.close(); }
		catch (IOException e) {
			System.out.println("Failed to close buffered writer.");
			e.printStackTrace();
		}
	}

	private static void reenterPassword(Scanner in) {
		System.out.print("Re-enter password: ");
		String password=in.nextLine();
		boolean same=true;
		int i=password.length(), cur=0;
		if(i!=mPassword.length()) passwordError(password);

		while(cur<i) {
			same=mPassword.charAt(cur)==password.charAt(cur);
			cur++;
			if(!same) break;
		}

		if(same) System.out.println("Passwords are equivalent.");
		else passwordError(password);
	}

	private static void passwordError(String password) {
		System.out.println("The password '"+password+"' does not match the first password entered.");
		mErrors.add("Error when rereading password. Entered bad value '"+password+"'. Password does not match original password.");
	}

	private static void writeToOutput() { //might want to break this method into parts
		if(mOutFileCorrect) {
			BufferedWriter fout = null;
			try { fout = new BufferedWriter(new FileWriter(mOutput)); }
			catch (IOException e) {
				System.out.println("Failed to open buffered writer to write to output file.");
				e.printStackTrace();
			}
			assert fout!=null;

			if(mNameCorrect) {
				try { fout.write(mFirst+" "+mLast+"\n"); }
				catch (IOException e) {
					System.out.println("Failed to write name to file.");
					e.printStackTrace();
				}
			} else {
				System.out.println("Bad value for name. Unable to write name to output file.");
				mErrors.add("Could not write name to output file; valid filename not given.");
			}

			if(mNumberCorrect) {
				BigInteger bigIntOne=new BigInteger(mNum1);
				BigInteger bigIntTwo=new BigInteger(mNum2);
				try { fout.write(mNum1+"+"+mNum2+"="+bigIntOne.add(bigIntTwo)+"\n"+mNum1+"*"+mNum2+"="+bigIntOne.multiply(bigIntTwo)+"\n"); }
				catch (IOException e) {
					System.out.println("Failed to write name to file.");
					e.printStackTrace();
				}
			} else {
				System.out.println("Bad value for one or both integers. Unable to write result of integer addition/multiplication to output file.");
				mErrors.add("Could not write integer addition/multiplication to output file; one or both integers not valid.");
			}

			if(mInFileCorrect) {
				FileInputStream fis = null;
				try { fis = new FileInputStream(mInput);
				} catch (FileNotFoundException e) {
					System.out.println("Failed to open file input stream to read from input file.");
					e.printStackTrace();
				}
				assert fis!=null;
				BufferedReader in = new BufferedReader(new InputStreamReader(fis));

				String line = null;
				while (true) {
					try { if ((line = in.readLine()) == null) break; }
					catch (IOException e) {
						e.printStackTrace();
						System.out.println("Failed to read line from input file.");
					}
					try { fout.write(line+"\n"); }
					catch (IOException e) {
						System.out.println("Failed to write input file contents to output file.");
						e.printStackTrace();
					}
				}
				try { in.close(); }
				catch (IOException e) {
					System.out.println("Failed to close file input stream.");
					e.printStackTrace();
				}
			}
			else {
				System.out.println("Bad filename for input file. Unable to write input file contents to output file.");
				mErrors.add("Could not write input file contents to output file; valid filename not given.");
			}
			try { fout.close(); }
			catch (IOException e) {
				System.out.println("Failed to close buffered writer.");
				e.printStackTrace();
			}
		} else {
			System.out.println("Bad filename for output file. Unable to write to file.");
			mErrors.add("Could not write to output file; valid filename not given.");
		}
	}

}

/*
Shortcomings:
- had to set length restrictions on some inputs (like password and filename) that may rule out some valid input
- used System.in to read inputs, which utilizes File Input Stream that has associated vulnerabilities

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
-compare passwords a character at a time, so the comparison fails as soon as the passwords start to differ (to protect against malicious second password)
-checks that password file does not already exist before creating it
-use big int to store result of integer addition and multiplication to ensure it is large enough to contain it
 */
