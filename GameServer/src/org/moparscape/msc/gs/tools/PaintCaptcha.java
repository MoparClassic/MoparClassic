package org.moparscape.msc.gs.tools;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.ImageIcon;

public class PaintCaptcha {

	private final static boolean GRAPH = true;
	private final static Color GRAPH_COLOR = Color.GRAY;
	private final static int LINES = 10;
	/**
	 * @author xEnt
	 * @info Paints over an Image(Captcha) to make it more secure, with Lines,
	 *       Squares and a Graph.
	 */

	private final static int SQUARES_MAX = 25;
	private final static int SQUARES_MIN = 10;

	/*
	 * public static void main(String [] args) throws Exception { Image img =
	 * Toolkit.getDefaultToolkit().getImage("C:\\heh.png");
	 * ImageIO.write(Img.toBufferedImage(secure(img)), "png", new
	 * File("C:\\roflrofl.png")); }
	 */

	public static long randomNumber(int min, int max) {
		return Math.round((Math.random() * (max - min)) + min);
	}

	public static BufferedImage secure(BufferedImage b) {
		try {

			// BufferedImage b = toBufferedImage(img);
			Graphics2D gfx = b.createGraphics();
			gfx.setPaintMode();

			// Draw the Squares.
			Random random = new Random();
			for (int i = 0; i < (int) randomNumber(SQUARES_MIN, SQUARES_MAX); i++) {
				Color randomColor = Color.getHSBColor(random.nextFloat(), 1.0F,
						1.0F);
				gfx.setColor(randomColor);
				gfx.drawRect((int) randomNumber(0, b.getWidth() - 80),
						(int) randomNumber(0, b.getHeight()), 3, 3);
			}

			// Draw the Lines.
			gfx.setColor(Color.ORANGE);
			for (int i = 0; i < LINES; i++) {
				int x = (int) randomNumber(0, b.getWidth() - 80);
				int y = (int) randomNumber(0, b.getHeight());
				gfx.drawLine(x, y, x + (int) randomNumber(0, 30), y
						- (int) randomNumber(0, 30));
			}

			if (GRAPH) {
				// Graph, Lines from Top to Bottom.
				gfx.setColor(GRAPH_COLOR);
				for (int i = 0; i < 17; i++) {
					gfx.drawLine(5 + i * 15, b.getHeight(), 5 + i * 15, 0);
				}
				for (int i = 0; i < 3; i++) {
					gfx.drawLine(b.getWidth(), i * 14, 0, i * 14);
				}
			}

			gfx.dispose();
			return b;

		} catch (Exception e) {
			return null;
		}
	}

	public static BufferedImage toBufferedImage(Image image) {
		if (image instanceof BufferedImage) {
			return (BufferedImage) image;
		}

		// This code ensures that all the pixels in the image are loaded
		image = new ImageIcon(image).getImage();

		// Determine if the image has transparent pixels; for this method's
		// implementation, see e661 Determining If an Image Has Transparent
		// Pixels
		boolean hasAlpha = false;

		// Create a buffered image with a format that's compatible with the
		// screen
		BufferedImage bimage = null;
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		try {
			// Determine the type of transparency of the new buffered image
			int transparency = Transparency.OPAQUE;
			if (hasAlpha) {
				transparency = Transparency.BITMASK;
			}

			// Create the buffered image
			GraphicsDevice gs = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gs.getDefaultConfiguration();
			bimage = gc.createCompatibleImage(image.getWidth(null),
					image.getHeight(null), transparency);
		} catch (HeadlessException e) {
			// The system does not have a screen
		}

		if (bimage == null) {
			// Create a buffered image using the default color model
			int type = BufferedImage.TYPE_INT_RGB;
			if (hasAlpha) {
				type = BufferedImage.TYPE_INT_ARGB;
			}
			bimage = new BufferedImage(image.getWidth(null),
					image.getHeight(null), type);
		}

		// Copy image to buffered image
		Graphics g = bimage.createGraphics();

		// Paint the image onto the buffered image
		g.drawImage(image, 0, 0, null);
		g.dispose();

		return bimage;
	}
}
