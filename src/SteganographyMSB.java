import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

//tasks: read text message
//hide text message in image
//display stego image

//to do: encode key, powerpoint slides

public class SteganographyMSB {

	private static BufferedImage img;
	private static String mess;
	private static String messageString;
	private static String imagePath;
	private static String filePath;

	public SteganographyMSB(String imagePath) { // constructor displays cover
												// image
												// and takes image
												// as parameter
		if (checkExists(imagePath) != true) // checks if file path is valid
			System.out.println("Invalid file path.");
		else {
			try {

				BufferedImage image = ImageIO.read(new File(imagePath)); // reads
																			// image
				img = image; // sets private variable img equal to image

				JFrame frame = new JFrame(); // displays image

				JLabel jImage = new JLabel(new ImageIcon(image));
				frame.getContentPane().add(jImage, BorderLayout.CENTER);
				frame.setSize(300, 250);
				frame.setVisible(true);
			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.println("Image height: " + img.getHeight()); // prints
																	// out image
																	// height
																	// and width
			System.out.println("Image width: " + img.getWidth());

		}

	}

	public static boolean checkExists(String filePath) // checks whether given
														// file exists
	{
		File f = new File(imagePath);
		if (f.exists() && !f.isDirectory()) {
			return true;
		}

		return false;
	}

	// reads which cover image the user wants to use
	public static void getImageInput() {
		Scanner in = new Scanner(System.in);
		System.out.println("Type '1' to input cover image path or '2' for default image: ");

		if (in.hasNext() && !in.hasNextInt()) {
			System.out.println("Please enter a valid option.");
		} else {
			int response = in.nextInt();
			in.nextLine();

			if (response == 1) {

				System.out.println("Input image path: ");
				imagePath = in.nextLine();

				if (checkExists(imagePath) == false)
					System.out.println("Invalid file path.");
			}

			else if (response == 2) {

				imagePath = "Img/tree.jpg";
			}

			else
				System.out.println("Please enter a valid option");
		}
		
		
	}

	// reads the message the user wants to encode
	public static String getMessageInput() throws IOException {
		Scanner in = new Scanner(System.in);

		System.out.println("Type '1' to manually input text or '2' to read from text file: ");

		if (in.hasNext() && !in.hasNextInt()) {
			System.out.println("Please enter a valid option.");
		} else {
			int response2 = in.nextInt();
			in.nextLine();

			if (response2 == 1) {

				System.out.println("Input text: ");
				messageString = in.nextLine();

			}

			else if (response2 == 2) {

				System.out.println("Please type name of file: ");

				filePath = in.nextLine();

				if (checkExists(filePath) != true) // checks if file path is
													// valid
					System.out.println("Invalid file path.");
				else {
					BufferedReader br = new BufferedReader(new FileReader(filePath));

					try {
						StringBuilder sb = new StringBuilder();
						String line = br.readLine();

						while (line != null) {
							sb.append(line);
							sb.append(System.lineSeparator());
							line = br.readLine();
						}
						messageString = sb.toString();
					} finally {
						br.close();
					}
				}
			}

			else
				System.out.println("Please enter a valid option.");

		}
	

		return messageString;

	}

	public static ArrayList<Integer> convertToBinary(String message) { // converts
																		// input
																		// to
																		// array
																		// of
																		// binary
		mess = message; // text to binary
		byte[] bytes = message.getBytes(); // array list
		ArrayList<Integer> bin = new ArrayList<Integer>();

		for (byte a : bytes) {
			int num = a;

			for (int pos = 0; pos < 8; pos++) {
				bin.add((num & 128) == 0 ? 0 : 1);
				num <<= 1;
			}

		}


		return bin; // returns array of message in binary

	}



	public static BufferedImage encodeImg(ArrayList<Integer> message) { // encodes
																		// message
																		// into
																		// pixels
																		// of
																		// cover
																		// image
		// encode key
		ArrayList<Integer> symbolArray = new ArrayList<Integer>();
		symbolArray = convertToBinary("$$$");

		message.addAll(symbolArray);

		
		int messBit;
		int bit;



		BufferedImage newImg = img; // newImg is what will be encoded and
									// returned
		System.out.println("Size of image: " + img.getHeight() * img.getWidth());
		System.out.println("Size of message: " + message.size());

		if (img.getHeight() * img.getWidth() < message.size())
			System.out.println("The image is too small to encode this message! Use a larger one.");
		else {


			int count = 0; // by incrementing this variable within the for loop,
							// we can loop through the
							// pixels normally while using all three color
							// variables in each pixel to encode

			for (int row = 0; row < img.getHeight(); row++) {
				for (int col = 0; col < img.getWidth(); col++) {

					int color = newImg.getRGB(col, row);
					int green = (color & 0x0000ff00) >> 8;
					int red = (color & 0x00ff0000) >> 16;
					int blue = color & 0x000000ff;

					int blueDiff = 0;
					int greenDiff = 0;
					int redDiff = 0;

					// this if statement checks for each color whether
					// message.size is greater than count
					// so that there isn't an error if the message isn't
					// divisible by 3 and ends on
					// blue or red instead of green
					if (message.size() > count) {

						if (blue >= 128)
							bit = 1; // gets MSB of blue value
						else
							bit = 0;

						messBit = message.get(count); // gets bit of message
														// to embed in image
						count++;

						// if bit and messBit are the same,
						// nothing needs to be changed
						// otherwise, bluediff will be 1 or -1 and this will be
						// added to
						// the blue value to change the least significant bit
						if (messBit - bit != 0) {
							blueDiff = messBit - bit;
						}
					}

					if (message.size() > count) {

						if (red >= 128)
							bit = 1; // gets MSB of red value
						else
							bit = 0;

						messBit = message.get(count);
						count++;

						if (messBit - bit != 0)
							redDiff = messBit - bit;

						if (message.size() > count) {

							if (green >= 128)
								bit = 1; // gets MSB of green value
							else
								bit = 0;

							messBit = message.get(count);
							count++;

						}

						if (messBit - bit != 0)
							greenDiff = messBit - bit;
					}

					// changes most significant bits of each color value
					// to encode message, then changes the pixel in newImg

					if (blueDiff > 0)
						blueDiff = 128;
					else if (blueDiff < 0)
						blueDiff = -128;

					if (greenDiff > 0)
						greenDiff = 128;
					else if (greenDiff < 0)
						greenDiff = -128;

					if (redDiff > 0)
						redDiff = 128;
					else if (redDiff < 0)
						redDiff = -128;

					Color rgb = new Color(red + redDiff, green + greenDiff, blue + blueDiff);
					newImg.setRGB(col, row, rgb.getRGB());
				}

			}

			JFrame frame = new JFrame(); // display stego (encoded) image
			JLabel jImage = new JLabel(new ImageIcon(newImg));
			frame.getContentPane().add(jImage, BorderLayout.CENTER);
			frame.setSize(300, 250);
			frame.setVisible(true);

		}

		
		return newImg;
	}



	// Decodes the encoded image, taking the encoded image as a parameter
	public static void decodeImg(BufferedImage encImg) throws UnsupportedEncodingException {
		int key = getKey(encImg);
		byte[] bytes = new byte[key];

		int count = 0;
		for (int row = 0; row < img.getHeight(); row++) { // decode message
			for (int col = 0; col < img.getWidth(); col++) {

				if (key > count) {

					Color c = new Color(encImg.getRGB(col, row)); // gets RGB
																	// value of
																	// pixel
					int blue = c.getBlue(); // gets blue value of pixel

					if (blue >= 128) // gets MSB
						bytes[count] = 1;
					else
						bytes[count] = 0;

					count++;

					if (key > count) {

						int red = c.getRed();

						if (red >= 128) // gets MSB
							bytes[count] = 1;
						else
							bytes[count] = 0;

						count++;
					}

					if (key > count) {

						int green = c.getGreen();

						if (green >= 128) // gets MSB
							bytes[count] = 1;
						else
							bytes[count] = 0;

						count++;
					}

				}
			}
		}

		// System.out.println("Count: " + count);

		String binString = "";
		for (int pos = 0; pos < bytes.length; pos++) { // concatenates bits to
														// string
			if (pos % 8 == 0 && pos != 0)
				binString += " "; // adds a space every byte

			binString += Integer.toBinaryString(bytes[pos]);

		}

		System.out.println("Message in binary: " + binString);
		System.out.println("Message decoded: " + convertBinString(binString));
		// converts binString to ASCII


	}
	
	public static int getKey(BufferedImage encImg) {
		int key = 0;
		ArrayList<Integer> symbolArray = new ArrayList<Integer>();
		boolean foundSymbol = false;
		String symbolString = "";

		for (int row = 0; row < img.getHeight(); row++) { // decode message
			for (int col = 0; col < img.getWidth(); col++) {

				if (foundSymbol == false) {

					Color c = new Color(encImg.getRGB(col, row));

					if (symbolArray.size() == 24) {
						symbolString = convertArrayListToBinaryString(symbolArray);

						if (convertBinString(symbolString).equals("$$$"))
							foundSymbol = true;
						else {
							symbolArray.remove(0);
							int blue = c.getBlue();
							
							if (blue >= 128)
								symbolArray.add(1);
							else
								symbolArray.add(0);
							
							
							key++;
						}
					} else {
						int blue = c.getBlue();

						if (blue >= 128)
							symbolArray.add(1);
						else
							symbolArray.add(0);
						
						key++;
					}

					if (symbolArray.size() == 24) {
						symbolString = convertArrayListToBinaryString(symbolArray);

						if (convertBinString(symbolString).equals("$$$"))
							foundSymbol = true;
						else {
							symbolArray.remove(0);
							int red = c.getRed();

							if (red >= 128)
								symbolArray.add(1);
							else
								symbolArray.add(0);
							
							key++;
						}
					} else {
						int red = c.getRed();

						if (red >= 128)
							symbolArray.add(1);
						else
							symbolArray.add(0);
						
						key++;
					}

					if (symbolArray.size() == 24) {
						symbolString = convertArrayListToBinaryString(symbolArray);

						if (convertBinString(symbolString).equals("$$$"))
							foundSymbol = true;
						else {
							symbolArray.remove(0);
							int green = c.getGreen();
							
							if (green >= 128)
								symbolArray.add(1);
							else
								symbolArray.add(0);
							
							key++;
						}
					} else {
						int green = c.getGreen();

						if (green >= 128)
							symbolArray.add(1);
						else
							symbolArray.add(0);
						
						key++;
					}
				}
			}
		}

		key = key - 24; // gets rid of "$$$" at end of message

		return key;

	}

	public static String convertArrayListToBinaryString(ArrayList<Integer> list) {
		String binString = "";

		for (int pos = 0; pos < list.size(); pos++) {
			if (pos % 8 == 0 && pos != 0)
				binString += " "; // adds a space every byte

			binString += Integer.toBinaryString(list.get(pos));
		}
		return binString;

	}

	public static String convertBinString(String s) { // converts binary string
														// to ASCII
		String[] a = s.split(" ");
		StringBuilder sb = new StringBuilder();
		for (int pos = 0; pos < a.length; pos++) {
			sb.append((char) Integer.parseInt(a[pos], 2));
		}
		return sb.toString();
	}

	public static void main(String[] args) throws IOException {

		BufferedImage encodedImage = null;

		getImageInput();

		if (imagePath != null) {
			getMessageInput();
			if (messageString != null) {
				new SteganographyMSB(imagePath);
				encodedImage = encodeImg(convertToBinary(messageString));

				if (encodedImage != null)
					decodeImg(encodedImage);
			}

		}

	}
}
