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
//unit tests/debugging - still have errors in user input


public class LSBStegDecode {

	private static BufferedImage img;
	private static String mess;
	private static String messageString;
	private static String imagePath;
	private static String filePath;

	public LSBStegDecode(String imagePath) { // constructor displays image
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

				System.out.println("Input image path for image to be decoded: ");
				imagePath = in.nextLine();

				if (checkExists(imagePath) == false)
					System.out.println("Invalid file path.");

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
	

		new LSBStegDecode(imagePath);
		
		if(!imagePath.substring(imagePath.length() - 3, imagePath.length()).equals("bmp"))
		{
			System.out.println("Please enter a valid bitmap image.");
		}
		
		else
			decodeImg(img);
		

}
}
