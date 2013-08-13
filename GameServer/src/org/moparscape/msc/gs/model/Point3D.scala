package org.moparscape.msc.gs.model

class Point3D(val x : Int, val y : Int, val z : Int) {
	if (x < 0 || y < 0 || z > 4 || z < 0)
		throw new IllegalArgumentException("Invalid coordinates: " + (x, y, z))

	override def equals(other : Any) : Boolean =
		other match {
			case that : Point3D => x == that.x && that.y == y && that.z == z
			case _ => false
		}
}