package org.moparscape.msc.gs.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.config.Config;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.model.landscape.ActiveTile;
import org.moparscape.msc.gs.model.landscape.TileValue;

public class MapGenerator {
	private static final int BLACK = new Color(0, 0, 0).getRGB();
	private static final int BLUE = new Color(0, 0, 255).getRGB();
	private static final int HEIGHT = World.MAX_HEIGHT * 2;
	private static final String[] labels = { "Ground", "Level-1", "Level-2",
			"Underground" };

	private static final int PURPLE = new Color(150, 0, 255).getRGB();
	private static final int RED = new Color(255, 0, 0).getRGB();
	private static final int WIDTH = World.MAX_WIDTH * 2;
	private static final World world = Instance.getWorld();

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			Logger.println("Invalid args");
			return;
		}
		String configFile = "conf/world.xml";
		if (args.length > 1) {
			File f = new File(args[1]);
			if (f.exists()) {
				configFile = f.getName();
			}
		}
		Config.initConfig(configFile);
		MapGenerator mapGen = new MapGenerator();
		mapGen.generate();
		mapGen.save(args[0]);
	}

	private Graphics gfx;

	private BufferedImage image;

	public MapGenerator() {
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		gfx = image.getGraphics();
	}

	private void drawDot(int xCoord, int yCoord, int colour) {
		image.setRGB(WIDTH - xCoord - 1, yCoord, colour);
	}

	private void fillTile(int xCoord, int yCoord, int colour) {
		for (int xOff = 0; xOff < 2; xOff++) {
			for (int yOff = 0; yOff < 2; yOff++) {
				drawDot(xCoord + xOff, yCoord + yOff, colour);
			}
		}
	}

	public void generate() {
		gfx.fillRect(0, 0, WIDTH, HEIGHT);
		int label = 0;
		for (int x = 0; x < WIDTH; x += 2) {
			for (int y = 0; y < HEIGHT; y += 2) {
				if (y % 1888 == 0) {
					if (x == 0) {
						gfx.setColor(Color.GREEN);
						gfx.drawLine(0, y, WIDTH, y);
						gfx.drawLine(0, y + 1, WIDTH, y + 1);
						gfx.drawString(labels[label++], x + 10, y + 20);
					}
					continue;
				}
				handleTile(x, y, world.getTileValue(x / 2, y / 2));
				ActiveTile t = world.tiles[x / 2][y / 2];
				if (t != null) {
					if (t.hasNpcs()) {
						fillTile(x, y, RED);
					}
					if (t.hasItems()) {
						fillTile(x, y, PURPLE);
					}
					if (t.hasGameObject()) {
						fillTile(x, y, BLACK);
					}
				}
			}
		}
	}

	private void handleTile(int xImg, int yImg, byte type) {
		if ((type & 1) != 0) { // Top Wall
			drawDot(xImg, yImg, BLACK);
			drawDot(xImg + 1, yImg, BLACK);
		}
		if ((type & 2) != 0) { // Right wall
			drawDot(xImg, yImg, BLACK);
			drawDot(xImg, yImg + 1, BLACK);
		}
		if ((type & 4) != 0) { // Bottom Wall
			drawDot(xImg, yImg + 1, BLACK);
			drawDot(xImg + 1, yImg + 1, BLACK);
		}
		if ((type & 8) != 0) { // Left Wall
			drawDot(xImg + 1, yImg, BLACK);
			drawDot(xImg + 1, yImg + 1, BLACK);
		}
		if ((type & 16) != 0) { // Diagonal Wall \
			drawDot(xImg + 1, yImg, BLACK);
			drawDot(xImg, yImg + 1, BLACK);
		}
		if ((type & 32) != 0) { // Diagonal Wall /
			drawDot(xImg, yImg, BLACK);
			drawDot(xImg + 1, yImg + 1, BLACK);
		}
		if ((type & 64) != 0) { // Unwalkable/Object
			fillTile(xImg, yImg, BLUE);
		}
	}

	private void handleTile(int xImg, int yImg, TileValue tile) {
		handleTile(xImg, yImg, tile.mapValue);
		handleTile(xImg, yImg, tile.objectValue);
	}

	public boolean save(String filename) {
		try {
			File file = new File(filename);
			ImageIO.write(image, "png", file);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
