package org.moparscape.msc.gs.tools;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import org.moparscape.msc.gs.config.Config;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.util.Logger;

public class Captcha {

	private final static Font[] fonts = new Font[4];
	private final static Random random = new Random();
	private final static String[] words;

	static {

		// Load fonts
		try {
			String fontDir = Config.FONT_DIR;
			fonts[0] = Font.createFont(Font.TRUETYPE_FONT,
					new File(fontDir, "Harakiri.ttf")).deriveFont(30f);
			fonts[1] = Font.createFont(Font.TRUETYPE_FONT,
					new File(fontDir, "IntellectaBodoned Trash.ttf"))
					.deriveFont(30f);
			fonts[2] = Font.createFont(Font.TRUETYPE_FONT,
					new File(fontDir, "MiseryLovesCompanyDEMO.ttf"))
					.deriveFont(20f);
			fonts[3] = Font.createFont(Font.TRUETYPE_FONT,
					new File(fontDir, "ADDSBP__.TTF")).deriveFont(20f);
		} catch (Exception e) {
			Logger.error("Font loading error: " + e.getMessage());
			e.printStackTrace();
		}

		// Load word list
		List<String> wordList = new ArrayList<String>();
		File file = new File(Config.CAPTCHA_DICTIONARY);
		FileInputStream in = null;
		BufferedReader reader = null;
		FileChannel chan = null;
		try {
			in = new FileInputStream(file);
			chan = in.getChannel();
			reader = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = reader.readLine()) != null) {
				wordList.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				chan.close();
				if (reader != null) {
					reader.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		words = wordList.toArray(new String[0]);
	}

	public static byte[] generateCaptcha(Player p) {
		BufferedImage image = new BufferedImage(255, 40,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, 255, 40);
		g.setColor(Color.white);
		String word = getWord();
		p.setSleepword(word);
		int lastx = 5;
		for (char c : word.toCharArray()) {
			Font font = fonts[random.nextInt(fonts.length)];
			char chr = Character.toUpperCase(c);
			g.setFont(font);
			// y = 35 +- 5
			g.drawString(String.valueOf(chr), lastx, 35 + 5 * randomSignFlip());
			// Width +- an element of (0 through 5)
			lastx += g.getFontMetrics().charWidth(chr) + randomSignFlip()
					* random.nextInt(6);
		}
		g.dispose();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte bytes[] = null;
		try {
			ImageIO.write(image, "png", baos);
			bytes = baos.toByteArray();
		} catch (IOException ioe) {
			Logger.error("Error generating sleep word: " + ioe.getMessage());
			ioe.printStackTrace();
		}
		return bytes;
	}

	private static int randomSignFlip() {
		return random.nextBoolean() ? 1 : -1;
	}

	public static String getWord() {
		int row = random.nextInt(words.length);
		return words[row];
	}
}