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

//to do: powerpoint slides, desteg without losing image quality, save image to file
//unit tests/debugging - still have errors in user input.


public class Steganography {

	private static BufferedImage img;
	private static String mess;
	private static String messageString;
	private static String imagePath;
	private static String filePath;

	public Steganography(String imagePath) { // constructor displays cover image
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

				imagePath = "Img/forest.bmp";
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

	public static ArrayList<Integer> convertToBinary(int num) {

		String bin = Integer.toBinaryString(num);
		ArrayList<Integer> binary = new ArrayList<Integer>();

		for (int pos = 0; pos < bin.length(); pos++) {

			binary.add(Character.getNumericValue(bin.charAt(pos)));

		}

		return binary;
	}

	// encodes message into pixels of cover image
	public static BufferedImage encodeImg(ArrayList<Integer> message) throws IOException {

		// encode key
		ArrayList<Integer> symbolArray = new ArrayList<Integer>();
		symbolArray = convertToBinary("$$$");

		message.addAll(symbolArray);

		// key = message.size();

		int messBit;
		int bit;

		BufferedImage newImg = img; // newImg is what will be encoded and
									// returned
		System.out.println("Size of image: " + img.getHeight() * img.getWidth());
		System.out.println("Size of message: " + (message.size() - symbolArray.size()));

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

						bit = blue % 2; // gets LSB of blue value
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

						bit = red % 2; // how to switch from least to most
										// significant
						messBit = message.get(count);
						count++;

						if (messBit - bit != 0)
							redDiff = messBit - bit;

						if (message.size() > count) {

							bit = green % 2;
							messBit = message.get(count);
							count++;

						}

						if (messBit - bit != 0)
							greenDiff = messBit - bit;
					}

					// changes least significant bits of each color value
					// to encode message, then changes the pixel in newImg
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
		
		 ImageIO.write(newImg, "bmp", new File("Img/savedImg2.bmp"));

		return newImg;
	}


	// Decodes the encoded image, taking the encoded image as a parameter
	public static void decodeImg(BufferedImage encImg) throws UnsupportedEncodingException {
		int key = getKey(encImg);

		byte[] bytes = new byte[key];

		int count = 0;
		for (int row = 0; row < img.getHeight(); row++) { // decode message
			for (int col = 0; col < img.getWidth(); col++) {

				Color c = new Color(encImg.getRGB(col, row));

				if (key > count) {

					// gets RGB
					// value of
					// pixel
					int blue = c.getBlue(); // gets blue value of pixel
					bytes[count] = (byte) (blue % 2); // uses modulus division
														// to get LSB (0 or 1)

					count++;
				}

				if (key > count) {

					int red = c.getRed();
					bytes[count] = (byte) (red % 2);

					count++;
				}

				if (key > count) {

					int green = c.getGreen();
					bytes[count] = (byte) (green % 2);

					count++;
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
		
		//output = "Message in binary: " + binString + "\nMessage decoded: " + convertBinString(binString);
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
							symbolArray.add(blue % 2);
							key++;
						}
					} else {
						int blue = c.getBlue();
						symbolArray.add(blue % 2);
						key++;
					}

					if (symbolArray.size() == 24) {
						symbolString = convertArrayListToBinaryString(symbolArray);

						if (convertBinString(symbolString).equals("$$$"))
							foundSymbol = true;
						else {
							symbolArray.remove(0);
							int red = c.getRed();
							symbolArray.add(red % 2);
							key++;
						}
					} else {
						int red = c.getRed();
						symbolArray.add(red % 2);
						key++;
					}

					if (symbolArray.size() == 24) {
						symbolString = convertArrayListToBinaryString(symbolArray);

						if (convertBinString(symbolString).equals("$$$"))
							foundSymbol = true;
						else {
							symbolArray.remove(0);
							int green = c.getGreen();
							symbolArray.add(green % 2);
							key++;
						}
					} else {
						int green = c.getGreen();
						symbolArray.add(green % 2);
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
		
	Scanner in = new Scanner(System.in);

		BufferedImage encodedImage = null;

		getImageInput();
		
      /*  if (imagePath != null) {
           getMessageInput();
        if (messageString != null) {
           new Steganography(imagePath);
           encodedImage = encodeImg(convertToBinary(messageString));
        }

        else 
		new Steganography(imagePath);
        	if(img != null)
           decodeImg(img);*/
		/*new Steganography(imagePath);
		if (imagePath != null) {
			System.out.println("Type '1' to encode image or '2' to decode it.");
			if(in.hasNext() && !in.hasNextInt())
				System.out.println("Please enter a valid option.");
			else if(in.nextInt() == 1)
			{
			getMessageInput();
			if (messageString != null) {
				//new Steganography(imagePath);
				encodedImage = encodeImg(convertToBinary(messageString));
			}
			}
			else if(in.nextInt() == 2)
			{
				//new Steganography(imagePath);
				if(img != null)
					decodeImg(img);
			}
			else
				System.out.println("Please enter a valid option.");*/
		



		if (imagePath != null) {
			getMessageInput();
			if (messageString != null) {
				new Steganography(imagePath);
				encodedImage = encodeImg(convertToBinary(messageString));

				if (encodedImage != null)
					decodeImg(encodedImage);
		
		
		
		/*if (imagePath != null) {
			getMessageInput();
			if (messageString != null) {
				new Steganography(imagePath);
				encodedImage = encodeImg(convertToBinary(messageString));
				
			System.out.println(getKey(encodedImage));*/
		
	

			

			

        
		}}
		
}
}
