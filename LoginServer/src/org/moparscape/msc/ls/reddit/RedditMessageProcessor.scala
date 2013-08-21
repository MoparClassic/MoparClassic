package org.moparscape.msc.ls.reddit

import org.moparscape.msc.ls.service.CodeService
import org.moparscape.msc.ls.service.WhitelistService
import org.moparscape.msc.ls.Server
import org.moparscape.msc.ls.util.DataConversions
import org.moparscape.msc.ls.util.Hash

object RedditMessageProcessor {
	def process(modHash : String, name : String, msg : String, sender : String) {
		val msgs = msg.split(" ")
		msgs.head match {
			case "register" => register(modHash, name, msgs.tail, sender)
			case "whitelist" => whitelist(sender, msgs.tail)
			case "password" => changePass(modHash, name, msgs.tail, sender)
		}
	}

	private def register(modHash : String, name : String, args : Array[String], sender : String) {
		val user = DataConversions.usernameToHash(args(0))
		if (Server.storage.loadPlayer(user) == null) {
			val pass = DataConversions.formatString(CodeService.newCode, 20)
			val p = Server.storage.registerPlayer(user, hash(pass), sender)
			Server.storage.savePlayer(p)
			val msg = "Thank you for registering. Your password is: " + pass.trim
			new PrivateMessage(modHash, name, msg).run
			WhitelistService.add(sender)
			whitelist(sender, args.tail)
		} else {
			new PrivateMessage(modHash, name, "That username is already taken.").run
		}
	}

	private def hash(pass : String) = new Hash(new Hash(pass.getBytes).value).value

	private def whitelist(sender : String, args : Array[String]) {
		WhitelistService.whitelist(sender, args(0))
	}

	private def changePass(modHash : String, name : String, args : Array[String], sender : String) {
		val p = Server.storage.loadPlayer(DataConversions.usernameToHash(args(0)))
		if (p != null) {
			if (p.identifier == sender) {
				val pass = DataConversions.formatString(args.tail.foldLeft("")(_ + " " + _).drop(1), 20)
				p.pass = hash(pass)
				Server.storage.savePlayer(p)
				new PrivateMessage(modHash, name, "Your password is now: \"" + pass.trim + "\"\n\n" + "You can use spaces in place of underscores when entering it into the client.").run
			} else {
				new PrivateMessage(modHash, name, "This account belongs to " + p.identifier + " not " + sender).run
			}
		} else {
			new PrivateMessage(modHash, name, DataConversions.hashToUsername(DataConversions.usernameToHash(args(0))) + " does not exist.").run
		}
	}
}