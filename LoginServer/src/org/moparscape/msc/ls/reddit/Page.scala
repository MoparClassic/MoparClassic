package org.moparscape.msc.ls.reddit

import scala.collection.mutable.ListBuffer

class Page(val contents : String) {
	def getTopic = {
		var c = contents.drop(contents.indexOf("\"kind\":\"t3\","))
		c = c.dropRight(c.indexOf("\"after\":null,") + 1)
		new Page(c)
	}

	def getTopLevelComment(kind : String) = {
		val list = ListBuffer[Page]()
		var co = contents
		def getIdx = co.indexOf("\"kind\": \"" + kind + "\",")
		def getIdx1 = co.indexOf("replies\": {")

		var idx = getIdx1
		while (idx > -1) {
			val end = getMatch(co.drop(idx)) + idx
			co = co.substring(0, idx) + "replies\":\"\"," + co.substring(end)
			idx = getIdx1
		}

		idx = getIdx
		while (idx > 0) {
			val end = getMatch(co.drop(idx + 1)) + idx
			list += new Page(co.substring(idx + 14, end + 1))
			co = co.substring(end - 14)
			idx = getIdx
		}

		list.toList
	}

	private def getMatch(s : String) = {
		var count = 1
		var i = s.indexOf("{") + 1
		var escaped = false
		var inQuote = false
		while (count > 0) {
			count += (s.charAt(i) match {
				case '{' => if (!inQuote) 1 else 0
				case '}' => if (!inQuote) -1 else 0
				case '\\' => {
					escaped = true
					0
				}
				case '\"' => {
					inQuote = !inQuote
					0
				}
				case _ => {
					escaped = false
					0
				}
			})
			i += 1
		}
		i
	}

	def getTag(tag : String) = {
		var c = contents.drop(contents.indexOf(tag) + tag.length + 3)
		if (c.startsWith(" ")) c = c.drop(1)
		if (c.startsWith("null,")) "null"
		else if (c.startsWith("true,")) "true"
		else if (c.startsWith("false,")) "false"
		else if (c.startsWith("\"")) {
			val end = {
				var count = 1
				var idx = 2
				var escaped = false
				while (count != 0) {
					idx += 1
					c.charAt(idx) match {
						case '\"' => count -= 1
						case '\\' => escaped = true
						case _ => escaped = false
					}
				}
				idx
			}
			c.substring(1, end)
		} else {
			c.substring(0, c.indexOf(","))
		}
	}

	def getModHash = {
		if (contents.indexOf("modhash") == -1) {
			throw new RuntimeException("No modhash in page.")
		}
		val c = contents.drop(contents.indexOf("modhash") + 11)

		c.take(c.indexOf("\""))
	}

	override def toString = contents
}