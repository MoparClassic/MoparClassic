package org.moparscape.msc.gs.model;

public class Property<T> {
	
	public final T value;
	
	// Used behind the scenes for saving/loading
	@SuppressWarnings("unused")
	private final String className;
	
	public Property(T value) {
		this.value = value;
		className = value.getClass().getCanonicalName();
	}
	
	public Class<?> getPropertyClass() {
		return value.getClass();
	}
	
}
