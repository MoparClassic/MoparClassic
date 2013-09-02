package org.moparscape.msc.ls.service

import scala.collection.mutable.ListBuffer
import scala.util.Random
import org.moparscape.msc.ls.util.DataConversions

/**
 * A transient code tracking service.
 *
 * @author CodeForFame
 */
object CodeService {

	private val activeCodes = ListBuffer[(Long, String)]()
	private val rand = new Random
	private var alphanumeric = rand.alphanumeric

	def newCode = {

		def next = {
			var s = ""
			val c = rand.nextInt(4) + 6
			alphanumeric.take(c).foreach(s += _)
			alphanumeric = alphanumeric.drop(c)
			DataConversions.formatString(s, 20).trim
		}
		var code = next
		while (isCodeActive(code)) code = next
		code
	}

	def activateCode(username : Long, code : String) {
		activeCodes += username -> code
	}

	def deactivateCode(code : String) {
		activeCodes.find(_._2 == code) match {
			case Some(x) => activeCodes -= x
			case None =>
		}
	}

	def contains(username : Long, code : String) = activeCodes.contains(username -> code)

	def isCodeActive(code : String) = activeCodes.find(_._2 == code) match {
		case Some(x) => true
		case None => false
	}

}