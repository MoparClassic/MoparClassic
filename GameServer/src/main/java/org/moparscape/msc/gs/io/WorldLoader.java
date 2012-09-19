package org.moparscape.msc.gs.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.moparscape.msc.config.Config;
import org.moparscape.msc.config.Formulae;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.external.EntityHandler;
import org.moparscape.msc.gs.external.GameObjectLoc;
import org.moparscape.msc.gs.external.ItemLoc;
import org.moparscape.msc.gs.external.NPCLoc;
import org.moparscape.msc.gs.model.GameObject;
import org.moparscape.msc.gs.model.Item;
import org.moparscape.msc.gs.model.Npc;
import org.moparscape.msc.gs.model.Sector;
import org.moparscape.msc.gs.model.Shop;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.tools.DataConversions;
import org.moparscape.msc.gs.util.Logger;

public class WorldLoader {
	private ZipFile tileArchive;

	// private ZipOutputStream out;

	private void loadSection(int sectionX, int sectionY, int height,
			World world, int bigX, int bigY) {
		// Logging.debug(1);
		Sector s = null;
		try {
			String filename = "h" + height + "x" + sectionX + "y" + sectionY;
			ZipEntry e = tileArchive.getEntry(filename);
			if (e == null) {
				throw new Exception("Missing tile: " + filename);
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

				world.getTileValue(bx, by).overlay = s.getTile(x, y).groundOverlay;
				world.getTileValue(bx, by).diagWallVal = s.getTile(x, y).diagonalWalls;
				world.getTileValue(bx, by).horizontalWallVal = s.getTile(x, y).horizontalWall;
				world.getTileValue(bx, by).verticalWallVal = s.getTile(x, y).verticalWall;
				world.getTileValue(bx, by).elevation = s.getTile(x, y).groundElevation;
				/** start of shit **/
				if ((s.getTile(x, y).groundOverlay & 0xff) == 250) {
					s.getTile(x, y).groundOverlay = (byte) 2;
				}
				/** break in shit **/
				int groundOverlay = s.getTile(x, y).groundOverlay & 0xFF;
				if (groundOverlay > 0
						&& EntityHandler.getTileDef(groundOverlay - 1)
								.getObjectType() != 0) {
					world.getTileValue(bx, by).mapValue |= 0x40; // 64
				}

				int verticalWall = s.getTile(x, y).verticalWall & 0xFF;
				if (verticalWall > 0
						&& EntityHandler.getDoorDef(verticalWall - 1)
								.getUnknown() == 0
						&& EntityHandler.getDoorDef(verticalWall - 1)
								.getDoorType() != 0) {
					world.getTileValue(bx, by).mapValue |= 1; // 1
					world.getTileValue(bx, by - 1).mapValue |= 4; // 4
				}

				int horizontalWall = s.getTile(x, y).horizontalWall & 0xFF;
				if (horizontalWall > 0
						&& EntityHandler.getDoorDef(horizontalWall - 1)
								.getUnknown() == 0
						&& EntityHandler.getDoorDef(horizontalWall - 1)
								.getDoorType() != 0) {
					world.getTileValue(bx, by).mapValue |= 2; // 2
					world.getTileValue(bx - 1, by).mapValue |= 8; // 8
				}

				int diagonalWalls = s.getTile(x, y).diagonalWalls;
				if (diagonalWalls > 0
						&& diagonalWalls < 12000
						&& EntityHandler.getDoorDef(diagonalWalls - 1)
								.getUnknown() == 0
						&& EntityHandler.getDoorDef(diagonalWalls - 1)
								.getDoorType() != 0) {
					world.getTileValue(bx, by).mapValue |= 0x20; // 32
				}
				if (diagonalWalls > 12000
						&& diagonalWalls < 24000
						&& EntityHandler.getDoorDef(diagonalWalls - 12001)
								.getUnknown() == 0
						&& EntityHandler.getDoorDef(diagonalWalls - 12001)
								.getDoorType() != 0) {
					world.getTileValue(bx, by).mapValue |= 0x10; // 16
				}
				/** end of shit **/
			}
		}
		// Logging.debug(3);
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

	public void loadWorld(World world) throws Exception {
		try {
			tileArchive = new ZipFile(new File(Config.CONF_DIR,
					"data/Landscape.rscd"));
			// out = new ZipOutputStream(new FileOutputStream(new
			// File(Config.CONF_DIR, "data/new_Landscape.rscd")));
			// out.setLevel(9);
		} catch (Exception e) {
			Logger.error(e);
		}
		long now = System.currentTimeMillis();
		for (int lvl = 0; lvl < 4; lvl++) {
			int wildX = 2304;
			int wildY = 1776 - (lvl * 944);
			for (int sx = 0; sx < 1000; sx += 48) {
				for (int sy = 0; sy < 1000; sy += 48) {
					int x = (sx + wildX) / 48;
					int y = (sy + (lvl * 944) + wildY) / 48;
					loadSection(x, y, lvl, world, sx, sy + (944 * lvl));
				}
			}
		}
		Logger.error((System.currentTimeMillis() - now) / 1000 + "s to parse");
		// try { out.close(); } catch(Exception e) { Logger.error(e); }
		for (Shop shop : Instance.getDataStore().loadShops()) {
			world.registerShop(shop);
		}
		System.gc();
	}

	public void loadObjects() throws Exception {
		World world = Instance.getWorld();
		for (GameObjectLoc gameObject : Instance.getDataStore().loadGameObjectLocs()) {
			if (Config.f2pWildy && Formulae.isP2P(true, gameObject))
				continue;
			if (Formulae.isP2P(gameObject) && !World.isMembers())
				continue;
			world.registerGameObject(new GameObject(gameObject));
		}
		for (ItemLoc item : Instance.getDataStore().loadItemLocs()) {
			if (Config.f2pWildy && Formulae.isP2P(true, item))
				continue;
			if (Formulae.isP2P(item) && !World.isMembers())
				continue;
			world.registerItem(new Item(item));
		}// ember

		for (NPCLoc npc : Instance.getDataStore().loadNPCLocs()) {
			if (Config.f2pWildy && Formulae.isP2P(true, npc))
				continue;
			if (Formulae.isP2P(npc) && !World.isMembers())
				continue;
			world.registerNpc(new Npc(npc));
		}
	}

}
