package org.moparscape.msc.gs.util

class HexString(bytes: Array[Byte]) {
	lazy val string = bytes.map(0xFF & _).map { "%02x".format(_) }.foldLeft("") { _ + _ }
	
	override def toString = string
}