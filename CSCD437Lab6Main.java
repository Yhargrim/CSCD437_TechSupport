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

import java.io.File;
import java.util.Scanner;

public class CSCD437Lab6Main {
	
	public static void main(String[] args) {
		Scanner kb = new Scanner(System.in);
		
		String firstname = name("first", kb);
		String lastname = name("last", kb);
		System.out.println(firstname + " " + lastname);
		System.out.println();
		
		int[] numbers = numbers(kb);
		System.out.println("Addition: " + (numbers[0] + numbers[1]));
		System.out.println("Multiply: " + (numbers[0] * numbers[1]));
		System.out.println();
		
		String infname = inFile(kb);
		System.out.println("Input File Name: " + infname);
		System.out.println();
		
		String outfname = outFile(kb);
		System.out.println("Output File Name: " + outfname);
		System.out.println();
		
		String password = password(kb);
		System.out.println("Password: " + password);
		
		kb.close();
	}
	
	private static String name(final String which, final Scanner kb) {
		System.out.print("Enter your " + which +  " name: ");
		String hold = "";
		boolean flag = true;
		while (flag) {
			if (kb.hasNext()) {
				hold = kb.nextLine();
				if (hold.length() == 0 || hold.length() > 50)
					System.out.print("Invalid - Try Again: ");
				else
					flag = false;
			}
		}
		return hold;
	}
	
	private static int[] numbers(final Scanner kb) {
		System.out.print("Enter an integer: ");
		int one = number(kb);
		
		System.out.print("Enter another integer: ");
		int two = number(kb);
		
		int[] arr = {one, two};
		return arr;
	}
	
	private static int number(final Scanner kb) {
		int hold = 0;
		boolean flag = true;
		while (flag) {
			if (kb.hasNextInt()) {
				hold = kb.nextInt();
				flag = false;
			} else
				System.out.print("Invalid - Try Again: ");
			kb.nextLine();
		}
		return hold;
	}
	
	private static String inFile(final Scanner kb) {
		System.out.print("Enter input file name: ");
		String fname = "";
		while (true) {
			if (kb.hasNext())
				fname = kb.nextLine();
			File file = new File(fname);
			if (file.exists())
				if (file.canRead())
					return fname;
			System.out.print("Invalid - Try Again: ");
		}
	}
	
	private static String outFile(final Scanner kb) {
		System.out.print("Enter output file name: ");
		String fname = "";
		while (true) {
			if (kb.hasNext())
				fname = kb.nextLine();
			File file = new File(fname);
			if (file.exists())
				if (file.canWrite())
					return fname;
			System.out.print("Invalid - Try Again: ");
		}
	}
	
	private static String password(final Scanner kb) {
		boolean flag = true;
		String first = "", second = "";
		while (flag) {
			System.out.print("Enter desired password: ");
			first = kb.nextLine();
			System.out.print("Re-enter password: ");
			second = kb.nextLine();
			if (first.equals(second))
				flag = false;
			else
				System.out.println("Invalid - Try Again");
		}
		return first;
	}
}
