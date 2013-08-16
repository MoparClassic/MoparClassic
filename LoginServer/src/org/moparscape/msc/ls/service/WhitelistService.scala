package org.moparscape.msc.ls.service

import scala.io.Source
import java.io.File
import java.io.FileWriter

object WhitelistService {

	private val file = "whitelist.txt"
	private def isRegistered(identifier : String) = Source.fromFile(new File(file)).getLines.contains(identifier)

	def add(identifier : String) {
		if (!isRegistered(identifier)) {
			val fw = new FileWriter(file, true)
			try fw.write(identifier + '\n') finally fw.close
		}
	}

	def whitelist(identifier : String, ip : String) {
		if (isRegistered(identifier)) {
			println(ip)
		}
	}
}