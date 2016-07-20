import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

//tasks: read text message
//hide text message in image
//display stego image


public class Steganography {

	private static BufferedImage img;
	private static String mess;
	private static int key;
	private static ArrayList messageList;
	private static String messageString;

	public Steganography() { // constructor displays cover image

		try {

			BufferedImage image = ImageIO.read(new File("Img/icon.png")); // reads
																			// image
			img = image;

			JFrame frame = new JFrame();

			JLabel jImage = new JLabel(new ImageIcon(image));
			frame.getContentPane().add(jImage, BorderLayout.CENTER);
			frame.setSize(300, 250);
			frame.setVisible(true);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Image height: " + img.getHeight());
		System.out.println("Image width: " + img.getWidth());

	}

	public Steganography(String imagePath) { // constructor displays cover image

		try {

			BufferedImage image = ImageIO.read(new File(imagePath)); // reads
																	// image
			img = image;

			JFrame frame = new JFrame();

			JLabel jImage = new JLabel(new ImageIcon(image));
			frame.getContentPane().add(jImage, BorderLayout.CENTER);
			frame.setSize(300, 250);
			frame.setVisible(true);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Image height: " + img.getHeight());
		System.out.println("Image width: " + img.getWidth());

	}

	public static ArrayList<Integer> convertToBinary(String message) { // converts
																		// input
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

		key = bin.size();
		return bin;

	}

	@SuppressWarnings("null")
	public static ArrayList<Integer> convertToBinary(int num) { // for encoding
																// key
		String bin = Integer.toBinaryString(num);
		ArrayList<Integer> binary = null;

		for (int pos = 0; pos < bin.length(); pos++) {
			binary.add(Character.getNumericValue(bin.charAt(pos)));
		}

		return binary;

	}
	

	public static BufferedImage encodeImg(ArrayList<Integer> message) {
		int messBit;
		int bit;

		messageList = message;

		BufferedImage newImg = img;
		System.out.println("Size of image: " + img.getHeight() * img.getWidth());
		System.out.println("Size of message: " + key);

		if ((img.getHeight() - 1) * img.getWidth() < message.size())
			System.out.println("The image is too small to encode this message! Use a larger one.");
		else {

			/*ArrayList<Integer> keyArray = convertToBinary(convertToBinary(mess).size()); // encode key
			
			if(keyArray.size() > img.getWidth())
			{
				System.out.println("Image is too small to encode the key!");
			}
			
			else{

			for (int pos = 0; pos < img.getWidth(); pos++) {
				
				int color1 = newImg.getRGB(pos, 0);
				int green1 = (color1 & 0x0000ff00) >> 8;
				int red1 = (color1 & 0x00ff0000) >> 16;
				int blue1 = color1 & 0x000000ff;
				
				String binBlue1 = Integer.toBinaryString(blue1);
				int keyBit = keyArray.get(pos);
				int bitLSB = Character.getNumericValue(binBlue1.charAt(binBlue1.length() - 1));
				

				if (keyBit - bitLSB != 0) {
					int blueDiff1 = keyBit - bitLSB;

				int bitVal = keyArray.get(pos);

				Color rgb = new Color(red1, green1, blue1 + blueDiff1);
				newImg.setRGB(pos, 0, rgb.getRGB());
			}*/

			int count = 0;
			for (int row = 0; row < img.getHeight(); row++) {
				for (int col = 0; col < img.getWidth(); col++) {

					int color = newImg.getRGB(col, row);
					int green = (color & 0x0000ff00) >> 8;
					int red = (color & 0x00ff0000) >> 16;
					int blue = color & 0x000000ff;

					int blueDiff = 0;
					int greenDiff = 0;
					int redDiff = 0;

					if (message.size() > count) {

						String binBlue = Integer.toBinaryString(blue); // converts
																		// blue
																		// pixel
																		// value
																		// to
																		// string
						// gets LSB of blue pixel value
						bit = Character.getNumericValue(binBlue.charAt(binBlue.length() - 1));

						messBit = message.get(count); // gets bit of message
														// to embed in image
						count++;

						// if bit and messBit are the same,
						// nothing needs to be changed

						if (messBit - bit != 0) {
							blueDiff = messBit - bit;
						}
					}

					if (message.size() > count) {
						String binRed = Integer.toBinaryString(red);
						bit = Character.getNumericValue(binRed.charAt(binRed.length() - 1));
						messBit = message.get(count);
						count++;

						if (messBit - bit != 0)
							redDiff = messBit - bit;

						if (message.size() > count) {

							String binGreen = Integer.toBinaryString(green);
							bit = Character.getNumericValue(binGreen.charAt(binGreen.length() - 1));
							messBit = message.get(count);
							count++;

						}

						if (messBit - bit != 0)
							greenDiff = messBit - bit;
					}

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
		

	
	public static void decodeKey(BufferedImage encImg)
	{
		
	}

	public static void decodeImg(BufferedImage encImg) throws UnsupportedEncodingException {
		byte[] bytes = new byte[key];

		int count = 0;
		for (int row = 0; row < img.getHeight(); row++) { // decode message
			for (int col = 0; col < img.getWidth(); col++) {
		if (messageList.size() > count) {

					Color c = new Color(encImg.getRGB(col, row));
					int blue = c.getBlue();
					bytes[count] = (byte) (blue % 2);

					count++;

					if (messageList.size() > count) {

						int red = c.getRed();
						bytes[count] = (byte) (red % 2);

						count++;
					}

					if (messageList.size() > count) {

						int green = c.getGreen();
						bytes[count] = (byte) (green % 2);

						count++;
					}

				}
			}
		}

		System.out.println("Count: " + count);

		String binString = "";
		for (int pos = 0; pos < bytes.length; pos++) { // concatenates bits to
														// string
			if (pos % 8 == 0 && pos != 0)
				binString += " ";

			binString += Integer.toBinaryString(bytes[pos]);

		}

		System.out.println("Message in binary: " + binString);
		System.out.println("Message decoded: " + convertBinString(binString));
		// converts binString to ASCII

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

	public static boolean checkExists(String imagePath) // checks whether given
														// image exists in image
														// folder
	{
		File f = new File(imagePath);
		if (f.exists() && !f.isDirectory()) {
			return true;
		}

		return false;
	}

	public static void main(String[] args) throws IOException {

		Scanner in = new Scanner(System.in);

		System.out.println("Type '1' to input cover image path or '2' for default image: ");

		if (in.hasNext() && !in.hasNextInt()) {
			System.out.println("Please enter a valid option.");
		} else {
			int response = in.nextInt();
			in.nextLine();

			if (response == 1) {

				System.out.println("Input image path: ");
				String imagePath = in.nextLine();

				if (checkExists(imagePath) == true) // checks if file path is
													// valid
					new Steganography(imagePath);
				else
					System.out.println("Invalid file path.");

			}

			else if (response == 2) {

				new Steganography();
			}

			BufferedImage encodedImage = null;

			System.out.println("Type '1' to manually input text or '2' to read from text file: ");

			if (in.hasNext() && !in.hasNextInt()) {
				System.out.println("Please enter a valid option.");
			} else {
				int response2 = in.nextInt();
				in.nextLine();

				if (response2 == 1) {

					System.out.println("Input text: ");
					String message = in.nextLine();
					// System.out.println(convertToBinary(message));

					encodedImage = encodeImg(convertToBinary(message));

					// System.out.println(message);

				}

				else if (response2 == 2) {

					System.out.println("Please type name of file: ");

					String fileName = in.nextLine();
					BufferedReader br = new BufferedReader(new FileReader(fileName));

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

					encodedImage = encodeImg(convertToBinary(messageString));

				}

				else
					System.out.println("Please enter a valid option.");

				decodeImg(encodedImage);
			}
		}
	}
}
