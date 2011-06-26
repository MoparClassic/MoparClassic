package org.moparscape.msc.gs.tools;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.util.Logger;

public class Captcha {
	/*
	 * ArrayList that includes the preset colours.
	 */

	public ArrayList<Color> colors = new ArrayList<Color>();
	/*
	 * Boolean to decide whether to draw the grid on top or not
	 */
	public boolean drawGrid = true;
	/*
	 * String that represents the folder to load the fonts from
	 */
	public String fontFolder = "." + File.separator + "conf" + File.separator
			+ "fonts" + File.separator;
	private final int LETTERS_MAX = 5;
	private final int LETTERS_MIN = 4;
	private final int LINES_MAX = 10;
	private final int LINES_MIN = 5;
	/*
	 * Font array that includes all the loaded fonts with a preset size (between
	 * 25 and 30)
	 */
	public Font loadedFonts[];
	private Random rand = new Random();
	private final int SQUARES_MAX = 13;
	/*
	 * Settings to disable OCRs
	 */
	private final int SQUARES_MIN = 7;

	/*
	 * Boolean that defines if we should display what Font we loaded.
	 */
	public boolean verboseLoad = false;

	public byte[] generateCaptcha(Player p) {
		BufferedImage image = new BufferedImage(307, 49,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D gfx = image.createGraphics();
		String captcha = "";

		gfx.setColor(Color.white);
		gfx.fillRect(0, 0, 308, 52);

		int howManyLetters = random(LETTERS_MIN, LETTERS_MAX);
		for (int i = 1; i <= howManyLetters; i++) {
			char temp = generateLetter();
			if (temp == 'i' || temp == 'l') {
				temp = 'q'; // ez fix.
			}
			captcha += temp;
			gfx.setColor(colors.get(random(0, colors.size() - 1)));
			gfx.setFont(loadedFonts[random(0, loadedFonts.length - 1)]);
			double shear = (rand.nextDouble()) - 0.5;
			gfx.shear(shear, 0);
			gfx.drawString(String.valueOf(temp), i * random(40, 45),
					random(30, 40));
			gfx.shear(-shear, 0);
		}

		int howManySquares = random(SQUARES_MIN, SQUARES_MAX);
		for (int i = 0; i < howManySquares; i++) // Draw the squares, math by
		// xEnt.
		{
			gfx.setColor(colors.get(random(0, colors.size() - 1)));
			gfx.drawRect((int) random(0, image.getWidth() - 80),
					(int) random(0, image.getHeight()), 3, 3);
		}

		int howManyLines = random(LINES_MIN, LINES_MAX);
		for (int i = 0; i < howManyLines; i++) // Draw the lines, math by xEnt.
		{
			gfx.setColor(colors.get(random(0, colors.size() - 1)));
			int x = random(0, image.getWidth() - 80);
			int y = random(0, image.getHeight());
			gfx.drawLine(x, y, x + random(0, 30), y - random(0, 30));
		}

		if (drawGrid) // Draws the gray grid
		{
			gfx.setColor(Color.gray);
			gfx.drawLine(0, 13, image.getWidth(), 13);
			gfx.drawLine(0, 26, image.getWidth(), 26);
			gfx.drawLine(0, 39, image.getWidth(), 39);
			for (int i = 1; i < (int) (image.getWidth() / 10); i++) {
				gfx.drawLine(20 * i, 0, 20 * i, image.getHeight());
			}
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] returnVal = null;
		try {
			ImageIO.write(image, "PNG", baos);
			returnVal = baos.toByteArray();
			// DataOutputStream out = new DataOutputStream(new
			// FileOutputStream("newcaptchas/" + captcha + ".png"));
			// out.write(returnVal);
		} catch (IOException e) {
			e.printStackTrace();
		}
		gfx.dispose();
		p.setSleepword(captcha);
		return returnVal;
	}

	private char generateLetter() {
		char returnVal = '-';
		switch (random(0, 1)) {
		case 0:
			returnVal = (char) random(65, 89);
			break;
		case 1:
			returnVal = (char) random(97, 121);
			break;
		}
		return returnVal;
	}

	public void init() {
		loadFonts();
		colors.clear();
		colors.add(Color.BLUE);
		colors.add(Color.GREEN);
		colors.add(Color.GRAY);
		colors.add(Color.BLACK);
		colors.add(Color.RED);
		colors.add(Color.PINK);
		colors.add(Color.DARK_GRAY);
	}

	/**
	 * Loads fonts from a folder to a font array
	 */
	public void loadFonts() {
		File fontFolderFile = new File(fontFolder);
		String[] fonts = fontFolderFile.list();
		loadedFonts = new Font[fonts.length];
		for (int i = 0; i < fonts.length; i++) {
			try {
				FileInputStream fontStream = new FileInputStream(fontFolder
						+ fonts[i]);
				Font temp = java.awt.Font.createFont(
						java.awt.Font.TRUETYPE_FONT, fontStream);
				loadedFonts[i] = temp.deriveFont(Float.valueOf(random(35, 40)));
				if (verboseLoad) {
					Logger.println("Loaded font: "
							+ loadedFonts[i].getFontName());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Logger.println("Loaded " + fonts.length + " fonts.");
	}

	/**
	 * returns a random number within the given bounds
	 */
	public int random(int low, int high) {
		return low + rand.nextInt(high - low + 1);
	}
}
