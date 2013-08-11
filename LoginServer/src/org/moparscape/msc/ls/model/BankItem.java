package org.moparscape.msc.ls.model;

import java.io.Serializable;

public class BankItem extends Item implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BankItem(int id, int amount) {
		super(id, amount);
	}
}