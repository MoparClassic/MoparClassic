package org.moparscape.msc.gs.phandler.local

class CommandHandler {
	def handle(cmd : Command) {
		val cmdTxt = cmd.cmd.split(" ").head.toLowerCase
		val args = cmd.cmd.split(" ").drop(1)
		cmdTxt match {
			case "exit" | "quit" => System.exit(0)
		}
	}
}

class Command(val cmd : String)