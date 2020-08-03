/*
 * Ron Robinson III - Katherine Bozin
 * Tech Support
 * CSCD437-U20
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

	private static ArrayList<String> mErrors;
	private static boolean mFirstNameCorrect, mLastNameCorrect, mNum1Correct, mNum2Correct, mInFileCorrect, mOutFileCorrect, mPasswordCorrect;
	private static File mInput, mOutput;
	private static String mFirst, mLast, mNum1, mNum2, mPasswordFile;
	
	public static void main(String[] args) {
		mErrors=new ArrayList<>();
		mFirstNameCorrect=false;
		mLastNameCorrect=false;
		mNum1Correct=false;
		mNum2Correct=false;
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
		writeToFile("errors-java.txt", null, null);
		
		in.close();
	}

	private static void name(final String which, final Scanner in) {
		System.out.print("Enter "+which+" name: ");
		String name=in.nextLine();

		String namePattern = "^[A-Za-z,.'-]{1,50}$";
		Pattern pattern = Pattern.compile(namePattern);
		Matcher match = pattern.matcher(name);
		if(match.matches()) {
			if(which.equals("first")) {
				mFirst=name;
				mFirstNameCorrect=true;
			}
			else {
				mLast=name;
				mLastNameCorrect=true;
			}
		} else {
			mErrors.add("Error when reading "+which+" name. Entered bad value '"+name+"'.");
		}
	}
	
	private static void number(final String which, final Scanner in) {
		System.out.print("Enter "+which+" integer: ");
		String number=in.nextLine();

		String numberPattern = "^(-?\\d+){1,11}$";
		Pattern pattern = Pattern.compile(numberPattern);
		Matcher match = pattern.matcher(number);

		if(match.matches()) {
			BigInteger bigInt=new BigInteger(number);
			if(bigInt.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) >= 0 &&
					bigInt.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) <= 0) {
				if(which.equals("first")) {
					mNum1=number;
					mNum1Correct=true;
				}
				else {
					mNum2=number;
					mNum2Correct=true;
				}
			} else {
				mErrors.add("Error when reading "+which+" number. Entered bad value '"+number+"'.");
			}
		} else {
			mErrors.add("Error when reading "+which+" number. Entered bad value '"+number+"'.");
		}

	}

	private static void openFile(final String mode, final Scanner in) {
		System.out.print("Enter "+mode+" file name: ");
		String fileName = in.nextLine();

		String filePattern = "^[\\w,-. ]{1,200}\\.([A-Za-z]){1,20}$";
		Pattern pattern = Pattern.compile(filePattern);
		Matcher match = pattern.matcher(fileName);

		if (match.matches()) {
			File file = new File(fileName);
			if (file.exists()) {
				if (mode.equals("input") && file.canRead()) {
					mInput=file;
					mInFileCorrect=true;
				}
				else if (mode.equals("output") && file.canWrite()) {
					mOutput=file;
					mOutFileCorrect=true;
				} else fileError(mode, fileName);
			} else fileError(mode, fileName);
		} else fileError(mode, fileName);
	}

	private static void fileError(String mode, String fileName) {
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
			mErrors.add("Error when reading password. Entered bad value '"+password+"'.");
		}
	}

	private static void hashPassword(String password) {
		String hash;
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[16];
		random.nextBytes(salt);
		hash=Arrays.toString(salt);
		hash=hash(hash, password);

		writeToFile("password.txt", hash, salt);
	}

	private static String hash(String hash, String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			byte[] messageDigest = md.digest(password.getBytes());
			BigInteger bigInt = new BigInteger(1, messageDigest);
			hash = hash+bigInt.toString(16);
			while (hash.length()<32) hash = "0".concat(hash);
		} catch (NoSuchAlgorithmException e) { mErrors.add("NoSuchAlgorithmException in method 'hash'. "+e.getMessage()); }

		return hash;
	}

	private static void writeToFile(String fileName, String hash, byte[] salt) {
		File file=new File(fileName);
		String fname=fileName;
		while(file.exists()) {
			fname="0".concat(fname);
			file=new File(fname);
		}
		if(fileName.equals("password.txt")) mPasswordFile=fname;
		try { file.createNewFile(); }
		catch (IOException e) { mErrors.add("IOException in method 'writeToFile'. "+e.getMessage()); }

		BufferedWriter fout = null;
		try { fout = new BufferedWriter(new FileWriter(file)); }
		catch (IOException e) { mErrors.add("IOException in method 'writeToFile'. "+e.getMessage()); }

		assert fout != null;
		if(fileName.equals("errors-java.txt")) writeError(fout);
		else writePassword(fout, hash);
		try { fout.close(); }
		catch (IOException e) { mErrors.add("IOException in method 'writeToFile'. "+e.getMessage()); }
	}

	private static void writePassword(BufferedWriter fout, String hash) {
		try { fout.write(hash); }
		catch (IOException e) { mErrors.add("IOException in method 'writePassword'. "+e.getMessage()); }
	}

	private static void writeError(BufferedWriter fout) {
		try {
			for (String mError : mErrors) fout.write(mError + "\n");
			if (mErrors.isEmpty()) fout.write("No errors.");
		} catch (IOException e) { mErrors.add("IOException in method 'writeError'. "+e.getMessage()); }
	}

	private static void reenterPassword(Scanner in) {
		if(mPasswordCorrect) {
			System.out.print("Re-enter password: ");
			String password=in.nextLine();
			String hash="", firstPass="", salt="";

			FileInputStream fis = null;
			try { fis = new FileInputStream(mPasswordFile); }
			catch (FileNotFoundException e) { mErrors.add("FileNotFoundException in method 'reenterPassword'. "+e.getMessage()); }

			assert fis!=null;
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			try {
				firstPass=br.readLine();
				salt=firstPass.split("\\]")[0]+"]";
				hash=hash(salt, password);
			} catch (IOException e) { mErrors.add("IOException in method 'reenterPassword'. "+e.getMessage()); }

			try { br.close(); }
			catch (IOException e) { mErrors.add("IOException in method 'reenterPassword'. "+e.getMessage()); }

			boolean same=true;
			int i=hash.length(), cur=0;
			if(i!=firstPass.length()) passwordError(password);
			else {
				while(cur<i) {
					same=firstPass.charAt(cur)==hash.charAt(cur);
					cur++;
					if(!same) break;
				}

				if(same) System.out.println("Passwords are equivalent.");
				else passwordError(password);
			}
		}
		else System.out.println("Previous password not formatted correctly, can't re-enter password.");

	}

	private static void passwordError(String password) {
		System.out.println("The password '"+password+"' does not match the first password entered.");
		mErrors.add("Error when rereading password. Entered bad value '"+password+"'. Password does not match original password.");
	}

	private static void writeToOutput() {
		if(mOutFileCorrect) {
			BufferedWriter fout = null;
			try { fout = new BufferedWriter(new FileWriter(mOutput)); }
			catch (IOException e) { mErrors.add("IOException in method 'writeToOutput'. "+e.getMessage()); }
			assert fout!=null;

			if(mFirstNameCorrect) {
				try {
					fout.write(mFirst+" ");
					if(!mLastNameCorrect) fout.write("\n");
				}
				catch (IOException e) { mErrors.add("IOException in method 'writeToOutput'. "+e.getMessage()); }
			}
			if(mLastNameCorrect) {
				try { fout.write(mLast+"\n"); }
				catch (IOException e) { mErrors.add("IOException in method 'writeToOutput'. "+e.getMessage()); }
			}
			if(!(mFirstNameCorrect&&mLastNameCorrect)) {
				mErrors.add("Could not write name to output file; valid names not given.");
			}

			if(mNum1Correct&&mNum2Correct) {
				BigInteger bigIntOne=new BigInteger(mNum1);
				BigInteger bigIntTwo=new BigInteger(mNum2);
				try { fout.write(mNum1+"+"+mNum2+"="+bigIntOne.add(bigIntTwo)+"\n"+mNum1+"*"+mNum2+"="+bigIntOne.multiply(bigIntTwo)+"\n"); }
				catch (IOException e) { mErrors.add("IOException in method 'writeToOutput'. "+e.getMessage()); }
			} else {
				mErrors.add("Could not write integer addition/multiplication to output file; one or both integers not valid.");
				try { fout.write("\n"); }
				catch (IOException e) { mErrors.add("IOException in method 'writeToOutput'. "+e.getMessage()); }
			}

			if(mInFileCorrect) {
				try { fout.write("Input file contents:\n"); }
				catch (IOException e) { mErrors.add("IOException in method 'writeToOutput'. "+e.getMessage()); }
				FileInputStream fis = null;
				try { fis = new FileInputStream(mInput); }
				catch (FileNotFoundException e) { mErrors.add("FileNotFoundException in method 'writeToOutput'. "+e.getMessage()); }

				assert fis!=null;
				BufferedReader in = new BufferedReader(new InputStreamReader(fis));
				String line = null;
				while (true) {
					try { if ((line = in.readLine()) == null) break; }
					catch (IOException e) { mErrors.add("IOException in method 'writeToOutput'. "+e.getMessage()); }
					try { fout.write(line+"\n"); }
					catch (IOException e) { mErrors.add("IOException in method 'writeToOutput'. "+e.getMessage()); }
				}
				try { fis.close(); }
				catch (IOException e) { mErrors.add("IOException in method 'writeToOutput'. "+e.getMessage()); }
				try { in.close(); }
				catch (IOException e) { mErrors.add("IOException in method 'writeToOutput'. "+e.getMessage()); }
			} else {
				mErrors.add("Could not write input file contents to output file; valid filename not given.");
			}
			try { fout.close(); }
			catch (IOException e) { mErrors.add("IOException in method 'writeToOutput'. "+e.getMessage()); }
		} else {
			mErrors.add("Could not write to output file; valid filename not given.");
		}
	}
}