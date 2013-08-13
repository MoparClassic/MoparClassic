package org.moparscape.msc.gs.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.config.Config;
import org.moparscape.msc.gs.model.Point3D;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.model.definition.EntityHandler;
import org.moparscape.msc.gs.model.definition.extra.ShopDef;
import org.moparscape.msc.gs.model.landscape.MutableTileValue;
import org.moparscape.msc.gs.model.landscape.Sector;
import org.moparscape.msc.gs.service.WorldPopulationService;
import org.moparscape.msc.gs.tools.DataConversions;
import org.moparscape.msc.gs.util.Logger;

public class WorldLoader {
	private ZipFile tileArchive;

	// private ZipOutputStream out;

	private boolean loadSection(int sectionX, int sectionY, int height,
			World world, int bigX, int bigY) {
		// Logging.debug(1);
		Sector s = null;
		try {
			String filename = "h" + height + "x" + sectionX + "y" + sectionY;
			ZipEntry e = tileArchive.getEntry(filename);
			if (e == null) {
				// throw new Exception("Missing tile: " + filename);
				System.out.println("Missing tile: " + filename + " ex: "
						+ (bigX + 10) + ", " + (bigY + 10));
				return false;
			}
			ByteBuffer data = DataConversions
					.streamToBuffer(new BufferedInputStream(tileArchive
							.getInputStream(e)));
			s = Sector.unpack(data);
			// s = modifyAndSave(filename, s, bigX, bigY);
		} catch (Exception e) {
			Logger.error(e);
		}
		// Logging.debug(2);
		for (int y = 0; y < Sector.HEIGHT; y++) {
			for (int x = 0; x < Sector.WIDTH; x++) {
				int bx = bigX + x;
				int by = bigY + y;
				if (!world.withinWorld(bx, by)) {
					continue;
				}

				MutableTileValue t = new MutableTileValue(world.getTileValue(
						bx, by));
				t.overlay = s.getTile(x, y).groundOverlay;
				t.diagWallVal = s.getTile(x, y).diagonalWalls;
				t.horizontalWallVal = s.getTile(x, y).horizontalWall;
				t.verticalWallVal = s.getTile(x, y).verticalWall;
				t.elevation = s.getTile(x, y).groundElevation;
				/** start of shit **/
				if ((s.getTile(x, y).groundOverlay & 0xff) == 250) {
					s.getTile(x, y).groundOverlay = (byte) 2;
				}
				/** break in shit **/
				int groundOverlay = s.getTile(x, y).groundOverlay & 0xFF;
				if (groundOverlay > 0
						&& EntityHandler.getTileDef(groundOverlay - 1)
								.getObjectType() != 0) {
					t.mapValue |= 0x40; // 64
				}

				int verticalWall = s.getTile(x, y).verticalWall & 0xFF;
				if (verticalWall > 0
						&& EntityHandler.getDoorDef(verticalWall - 1)
								.getUnknown() == 0
						&& EntityHandler.getDoorDef(verticalWall - 1)
								.getDoorType() != 0) {
					t.mapValue |= 1; // 1
					MutableTileValue t1 = new MutableTileValue(
							world.getTileValue(bx, by - 1));
					t1.mapValue |= 4; // 4
					world.setTileValue(bx, by - 1, t1.toTileValue());
				}

				int horizontalWall = s.getTile(x, y).horizontalWall & 0xFF;
				if (horizontalWall > 0
						&& EntityHandler.getDoorDef(horizontalWall - 1)
								.getUnknown() == 0
						&& EntityHandler.getDoorDef(horizontalWall - 1)
								.getDoorType() != 0) {
					t.mapValue |= 2; // 2
					MutableTileValue t1 = new MutableTileValue(
							world.getTileValue(bx - 1, by));
					t1.mapValue |= 8;
					world.setTileValue(bx - 1, by, t1.toTileValue());
				}

				int diagonalWalls = s.getTile(x, y).diagonalWalls;
				if (diagonalWalls > 0
						&& diagonalWalls < 12000
						&& EntityHandler.getDoorDef(diagonalWalls - 1)
								.getUnknown() == 0
						&& EntityHandler.getDoorDef(diagonalWalls - 1)
								.getDoorType() != 0) {
					t.mapValue |= 0x20; // 32
				}
				if (diagonalWalls > 12000
						&& diagonalWalls < 24000
						&& EntityHandler.getDoorDef(diagonalWalls - 12001)
								.getUnknown() == 0
						&& EntityHandler.getDoorDef(diagonalWalls - 12001)
								.getDoorType() != 0) {
					t.mapValue |= 0x10; // 16
				}
				world.setTileValue(bx, by, t.toTileValue());
			}
		}
		return true;
	}

	/*
	 * private Sector modifyAndSave(String filename, Sector s, int bigX, int
	 * bigY) { for(int y = 0;y < Sector.HEIGHT;y++) { for(int x = 0;x <
	 * Sector.WIDTH;x++) { int bx = bigX + x; int by = bigY + y;
	 * if(!Instance.getWorld().withinWorld(bx, by)) { continue; } } } try {
	 * out.putNextEntry(new ZipEntry(filename));
	 * 
	 * byte[] data = s.pack().array(); out.write(data);
	 * 
	 * out.closeEntry(); } catch(Exception e) { e.printStackTrace(); } return s;
	 * }
	 */

	public List<Point3D> loadWorld(World world) throws Exception {
		Logger.println("Loading world.");
		try {
			tileArchive = new ZipFile(new File(Config.CONF_DIR,
					"data/Landscape.rscd"));
			// out = new ZipOutputStream(new FileOutputStream(new
			// File(Config.CONF_DIR, "data/new_Landscape.rscd")));
			// out.setLevel(9);
		} catch (Exception e) {
			Logger.error(e);
		}
		List<Point3D> sections = new ArrayList<>();
		long now = System.currentTimeMillis();
		for (int lvl = 0; lvl < 4; lvl++) {
			int wildX = 2304;
			int wildY = 1776 - (lvl * 944);
			for (int sx = 0; sx < 1000; sx += 48) {
				for (int sy = 0; sy < 1000; sy += 48) {
					int x = (sx + wildX) / 48;
					int y = (sy + (lvl * 944) + wildY) / 48;
					if (loadSection(x, y, lvl, world, sx, sy + (944 * lvl))) {
						sections.add(new Point3D(x, y, lvl));
					}
				}
			}
		}
		Logger.println(((System.currentTimeMillis() - now) / 1000)
				+ "s to load landscape");
		for (ShopDef shop : Instance.dataStore().loadShops()) {
			world.registerShop(shop.toShop());
		}
		return sections;
	}

	public void loadObjects(List<Point3D> sections) throws Exception {
		WorldPopulationService.run(sections);
	}

}
