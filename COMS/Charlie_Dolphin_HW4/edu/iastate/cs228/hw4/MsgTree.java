package cs228.hw4;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/* 
 *  @author Charlie Dolphin
 */

public class MsgTree
{
	// Character for a node in the tree
	public char payloadCharacter;

	// Left Subtree
	public MsgTree left;

	// Right Subtree
	public MsgTree right;

	// Index integer for recursive solution
	private static int staticCharIdx = 0;

	// Stores encoded message
	private static String encodedMsg;

	// Stores binary encoding
	private static String binaryEncoding;

	// Stores file that is read
	private static String file;

	// Constructor with char parameter
	public MsgTree (char payloadChar) {
		this.payloadCharacter = payloadChar;
	}

	// Constructor with String parameter
	public MsgTree (String encodingString) {
		this.payloadCharacter = encodingString.charAt(staticCharIdx);
		staticCharIdx++;
		this.left = new MsgTree(encodingString.charAt(staticCharIdx));

		if (this.left.payloadCharacter == '^') {
			this.left = new MsgTree(encodingString);
		}

		staticCharIdx++;
		this.right = new MsgTree(encodingString.charAt(staticCharIdx));
		if (this.right.payloadCharacter == '^') {
			this.right = new MsgTree(encodingString);
		}
	}

	// Method that prints the binary codes
	public static void printCodes(MsgTree codes, String msg) {
		if (codes == null) {
			return;
		}
		char payloadChar = codes.payloadCharacter;

		if (payloadChar != '^') {
			if (payloadChar == '\n') {
				System.out.print("\\" + "n" + "\t\t\t");
			}
			else {
				System.out.print(codes.payloadCharacter + "\t\t\t");
			}
			System.out.println(msg);
		}
		printCodes(codes.left, msg + "0");
		printCodes(codes.right, msg + "1");
	}

	// Method that decodes the binary combinations using leaf nodes
	public static String decode(MsgTree codes, String msg) {
		StringBuilder decodedMsg = new StringBuilder();
		MsgTree current = codes;

		for (int i = 0; i < msg.length(); i++) {
			current = msg.charAt(i) == '0' ? current.left : current.right;

			if (current.left == null || current.right == null) {
				decodedMsg.append(current.payloadCharacter);
				current = codes;
			}
		}

		if (current != codes) {
			decodedMsg.append(current.payloadCharacter);
		}
		return decodedMsg.toString();
	}

	// Method that prints the statistics
	public static void statistics(String encodingString, String decodedString) {
		System.out.println("----- STATISTICS -----");

		// Average Bits per Character
		// Length of Encoded String / Length of Decoded String
		System.out.printf("Avg bits/char:	\t%.1f%n", encodingString.length() / (double) decodedString.length());
		System.out.println("Total Characters:	\t" + decodedString.length());

		// Space saving
		// (1 - compressedBits / uncompressedBits) * 100
		System.out.printf("Space Savings:	\t%.1f%%%n", (1d - decodedString.length() / (double) encodingString.length()) * 100);
	}

	public static void main(String[] args) throws FileNotFoundException {
		Scanner s = new Scanner(System.in);
		System.out.print("Enter file path: ");

		try {
			file = s.nextLine().trim();
			File f = new File(file);
			if ((f.getAbsolutePath().endsWith(".arch")) == false) {
				s.close();
				throw new FileNotFoundException();
			}
		} catch (Exception e) {
				s.close();
				throw new FileNotFoundException("File does not exist.");
		}
		s.close();

		try (Scanner s2 = new Scanner(new File(file))) { // Second scanner
			encodedMsg = s2.nextLine(); // First line
			StringBuilder sb = new StringBuilder(encodedMsg);
			String file = s2.nextLine(); // Second Line

			for (int i = 0; i < file.length(); i++) {
				if (file.charAt(i) != '1' && file.charAt(i) != '0') {
					sb.append("\n").append(file);
					binaryEncoding = s2.nextLine();

					break;
				}
				else {
					binaryEncoding = file;
				}
			}
			encodedMsg = sb.toString();

		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("File does not exist.");
		}
		MsgTree mt = new MsgTree(encodedMsg);
		System.out.println("character	\t	code:	\n\n");
		printCodes(mt, "");
		System.out.println("MESSAGE:");
		String decoded = decode(mt, binaryEncoding);
		System.out.println(decoded);
		statistics(binaryEncoding, decoded);
	}
}