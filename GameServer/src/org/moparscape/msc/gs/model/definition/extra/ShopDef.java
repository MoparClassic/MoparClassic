package org.moparscape.msc.gs.model.definition.extra;

import java.util.ArrayList;
import java.util.List;

import org.moparscape.msc.gs.model.InvItem;
import org.moparscape.msc.gs.model.Point;
import org.moparscape.msc.gs.model.container.Shop;

public class ShopDef {
	private int buyModifier;
	private boolean general;
	private String greeting;
	private ArrayList<InvItem> items;
	private int minX, maxX, minY, maxY;
	private String name;
	private String[] options;
	private int respawnRate;
	private int sellModifier;

	public Shop toShop() {
		List<String> opts = new ArrayList<String>();
		for (String s : options) {
			opts.add(s);
		}
		Shop shop = new Shop(name, greeting, opts, new Point(minX, minY),
				new Point(maxX, maxY), general, respawnRate, buyModifier,
				sellModifier);
		for (InvItem i : items) {
			shop.add(i.id, i.amount, false);
		}
		return shop;
	}
}
