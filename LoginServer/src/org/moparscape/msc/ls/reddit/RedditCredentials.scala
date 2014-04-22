package org.moparscape.msc.ls.reddit

import scala.xml.XML
import java.io.File
import scala.language._

object RedditCredentials {

	val credentials = {
		val file = "conf" + File.separator + "reddit.xml"
		if (!new File(file).exists) throw new RuntimeException(file + " does not exist!")
		val config = XML.loadFile(file) \\ "credentials"
		new RedditCredentials(config \ "user" text, config \ "pass" text, config \ "agent" text)
	}
}

class RedditCredentials(val user : String, val pass : String, val agent : String)