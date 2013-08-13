package org.moparscape.msc.gs

import org.moparscape.msc.gs.model.World
import org.moparscape.msc.gs.config.Config
import org.moparscape.msc.gs.persistence.impl.DataStoreFactory
import akka.actor.ActorSystem
import org.moparscape.msc.gs.persistence.DataStore
import akka.actor.Props
import org.moparscape.msc.gs.log.LoggingService
import org.moparscape.msc.gs.log.BatchedLogger
import org.moparscape.msc.gs.service.IOService

object Instance {
	def getServer = getWorld.getServer
	def getWorld = World.getWorld
	def getDelayedEventHandler = getWorld.getDelayedEventHandler

	lazy val dataStore = try {
		DataStoreFactory.create(Config.DATA_STORE)
	} catch {
		case e : Throwable => {
			e.printStackTrace
			println("Could not create DataStore - " + Config.DATA_STORE)
			System.exit(0)
			null
		}
	}

	val loggingSystem = ActorSystem("LoggingSystem")
	val loggingService = loggingSystem.actorOf(
		Props(new LoggingService(new BatchedLogger)),
		name = "LoggingService")
	val IOService = ActorSystem("IO").actorOf(Props[IOService], name = "IOService")
}