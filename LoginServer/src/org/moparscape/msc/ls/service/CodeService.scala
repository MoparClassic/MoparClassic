package org.moparscape.msc.ls.service

import java.util.UUID
import scala.collection.mutable.ListBuffer

/**
 * A transient code tracking service.
 *
 * @author CodeForFame
 */
object CodeService {

	private val activeCodes = ListBuffer[(Long, String)]()

	def newCode = UUID.randomUUID.toString

	def activateCode(uid : Long, code : String) {
		activeCodes += uid -> code
	}

	def deactivateCode(code : String) {
		activeCodes.find(_._2 == code) match {
			case Some(x) => activeCodes -= x
			case None =>
		}
	}

	def isCodeActive(code : String) = activeCodes.find(_._2 == code) match {
		case Some(x) => true
		case None => false
	}

}