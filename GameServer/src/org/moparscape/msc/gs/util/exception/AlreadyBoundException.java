package org.moparscape.msc.gs.util.exception;

public class AlreadyBoundException extends Exception {

	private static final long serialVersionUID = -3134294984075687992L;

	public AlreadyBoundException(Object id) {
		super(id + " is already bound.");
	}
}
