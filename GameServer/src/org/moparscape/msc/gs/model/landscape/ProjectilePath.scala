package org.moparscape.msc.gs.model.landscape

import org.moparscape.msc.gs.Instance

/**
 * Verifies the path of a projectile.
 *
 * @author Joe Pritzel
 */
class ProjectilePath(xi : Int, yi : Int, xf : Int, yf : Int) {
	private val allowedWallTypes = List(
		List(4, 5, 6, 42, 14), // Normal
		List(229, 5)) // Diag  

	def isValid : Boolean = {
		if (xi == xf && yf == yi) {
			return true
		}

		val V = (xf - xi, yf - yi)
		val dir = if (math.abs(V._1) > math.abs(V._2)) 1 else if (math.abs(V._1) < math.abs(V._2)) 2 else 3
		// The minimum step allowed to ensure we don't miss a square
		val tStep = if (dir == 1 || dir == 3) {
			math.abs(0.5 / V._1) // Comes from r(t) = <Vx * t + xi, Vy * t + yi>
		} else {
			math.abs(0.5 / V._2) // Comes from r(t) = <Vx * t + xi, Vy * t + yi>
		}
		var prev = xi -> yi
		var t = tStep
		while (t <= 1 + tStep) {
			val curr = ((V._1 * t + xi).toInt, (V._2 * t + yi).toInt) // r(t) for integers
			if (prev != curr) {
				var cont = true

				if (prev._1 > curr._1) {
					cont &&= checkEast(prev._1, prev._2, dir)
				}
				if (prev._1 < curr._1) {
					cont &&= checkWest(prev._1, prev._2, dir)
				}
				if (prev._2 < curr._2) {
					cont &&= checkSouth(prev._1, prev._2, dir)
				}
				if (prev._2 > curr._2) {
					cont &&= checkNorth(prev._1, prev._2, dir)
				}

				if (!cont) return false
			}

			if (prev == xf -> yf) return true
			prev = curr
			t += tStep
		}
		return true
	}

	private def checkNorth(x : Int, y : Int, dir : Int) = {
		checkTile(1, x, y, dir)
	}

	private def checkSouth(x : Int, y : Int, dir : Int) = {
		checkTile(4, x, y, dir)
	}
	private def checkEast(x : Int, y : Int, dir : Int) = {
		checkTile(2, x, y, dir)
	}
	private def checkWest(x : Int, y : Int, dir : Int) = {
		checkTile(8, x, y, dir)
	}

	private def checkTile(face : Byte, x : Int, y : Int, dir : Int) : Boolean = {
		val t = Instance.getWorld.getTileValue(x, y)
		val b = t.mapValue

		if ((face & b) != 0) {
			if ((dir == 1 || dir == 3) && !allowedWallTypes(0).contains(t.horizontalWallVal & 0xFF)) return false
			if ((dir == 2 || dir == 3) && !allowedWallTypes(0).contains(t.verticalWallVal & 0xFF)) return false
		}

		if (((face & 32) != 0 || (face & 16) != 0) && !allowedWallTypes(1).contains(t.diagWallVal)) return false

		if ((face & 64) != 0) return false

		val obj = Instance.getWorld.getTile(x, y).getGameObject
		if (obj != null) {
			val d = obj.getDoorDef
			if (d != null && ((face == 1 || face == 4) && obj.getDirection == 0) || ((face == 2 || face == 8) && obj.getDirection == 1)) {
				if (d.getCommand1.equalsIgnoreCase("open") || d.getCommand2.equalsIgnoreCase("open")) return false
			}
		}

		return true
	}

}