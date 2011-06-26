package org.moparscape.msc.gs.phandler.client
import org.moparscape.msc.gs.phandler.PacketHandler
import org.apache.mina.common.IoSession
import org.moparscape.msc.gs.connection.Packet
import org.moparscape.msc.gs.model.Player
import org.moparscape.msc.gs.Instance
import org.moparscape.msc.gs.core.GameEngine
import org.moparscape.msc.gs.util.Logger
import org.moparscape.msc.gs.builders.ls.{ MiscPacketBuilder => LSMiscPacketBuilder }
import org.moparscape.msc.gs.tools.DataConversions
import org.moparscape.msc.gs.model.World
import org.moparscape.msc.gs.model.Point
import org.moparscape.msc.config.Config
import scala.collection.immutable.HashMap
import scala.xml.NodeSeq
import org.moparscape.msc.gs.connection.filter.IPBanManager

object CommandHandler {
  import scala.xml.XML
  import scala.xml.Node

  var COMMAND_DELAY = 2000
  var COMMAND_DELAY_MESSAGE_DELAY = 100
  var STUCK_LOGIN_WAIT_PERIOD = 30000
  var STUCK_STAND_STILL_TIME = 300000
  var STUCK_X = 122
  var STUCK_Y = 647

  val towns = List[String]("varrock", "falador", "draynor", "portsarim", "karamja", "alkharid", "lumbridge", "edgeville", "castle").toArray
  val townLocations = List[Point](Point.location(122, 509), Point.location(304, 542), Point.location(214, 632), Point.location(269, 643), Point.location(370, 685), Point.location(89, 693), Point.location(120, 648), Point.location(217, 449), Point.location(270, 352))

  var permissions: Map[String, Int] = new HashMap[String, Int]

  private def load {
    val config = XML.loadFile(Config.COMMAND_CONFIG)
    val permissions1 = config
    permissions = (permissions1 \ "permission" map { x => ((x \ "@name").text -> Integer.parseInt(x.text)) } toMap)

    val s = permissions1 \ "setting"
    COMMAND_DELAY = p(s \ "command-delay")
    COMMAND_DELAY_MESSAGE_DELAY = p(s \ "command-delay-message-delay")
    STUCK_LOGIN_WAIT_PERIOD = p(s \ "stuck-login-wait-period")
    STUCK_STAND_STILL_TIME = p(s \ "stuck-stand-still-time")
    STUCK_X = p(s \ "stuck-x")
    STUCK_Y = p(s \ "stuck-y")
    def p(n: NodeSeq) = {
      Integer.parseInt(n.text)
    }
  }

  load
}

class CommandHandler extends PacketHandler {

  import org.moparscape.msc.gs.phandler.client.{ CommandHandler => CH }

  @throws(classOf[Exception])
  def handleCommand(cmd: String, args: Array[String], p: Player) {

    val ls = Instance.getServer().getLoginConnector().getActionSender()
    val world = Instance.getWorld

    if (GameEngine.getTime - p.getLastCommandUsed < CH.COMMAND_DELAY) {
      if (GameEngine.getTime - p.getLastCommandUsed < CH.COMMAND_DELAY_MESSAGE_DELAY) { // incase spammers
        return ;
      }
      p.getActionSender().sendMessage("2 second delay on using a new command")
      return ;
    }

    val pm = CH.permissions.get(cmd)

    pm match {
      case Some(x) => if (p.getGroupID < x) return
      case None => return
    }

    var none = false

    cmd match {
      case "help" => help(p)
      case "time" => time(p)
      case "skull" => skull(p)
      case "fatigue" => fatigue(p)
      case "online" => online(p)
      case "nearby" | "inview" => inView(p)
      case "stuck" => stuck(p)
      case "info" => info(p, args, ls)
      case "info2" => info(p, args, world)
      case "info3" => info_(p, args, world)
      case "town" => town(p, args)
      case "ban" => ban(p, args, ls)
      case "unban" => unban(p, args, ls)
      case "quest" => quest(p, args)
      case "questpoints" => questPoints(p, args)
      case "dumpdata" => dumpData(p, args)
      case "shutdown" => shutdown(p)
      case "update" => update(p, args, world)
      case "dropall" => clearInv(p)
      case "thread" => enableMultiThreading(p)
      case "ipban" => ipban(p, args, world)
      case "unipban" => unipban(p, args)
      case "reloadipbans" => reloadIPBans(p)
      case _ => none = true
    }
    if (!none)
      p.setLastCommandUsed(GameEngine.getTime)
  }

  def help(p: Player) {
    alert(p, "A list of commands is shown on the forums.")
  }

  def time(p: Player) {
    import java.util.{ Calendar, GregorianCalendar, TimeZone }

    val cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"))
    val minutes = cal.get(Calendar.MINUTE)
    val hour24 = cal.get(Calendar.HOUR_OF_DAY)
    message(p, "The current time in UTC (24 hour) is: " + hour24 + ":" + minutes)
  }

  def skull(p: Player, minutes: Int = 20) {
    p.addSkull(minutes * 60000)
  }

  def fatigue(p: Player) {
    p.setFatigue(100)
    p.getActionSender().sendFatigue()
    message(p, "Your fatigue is now 100%")
  }

  def online(p: Player) {
    p.getActionSender.sendOnlinePlayers()
  }

  def inView(p: Player) {
    message(p, "@yel@Players In View: @whi@" +
      (p.getViewArea().getPlayersInView().size()) +
      " @yel@NPCs In View: @whi@" + p.getViewArea().getNpcsInView().size())
  }

  def stuck(p: Player) {
    if (GameEngine.getTime() - p.getCurrentLogin() < CH.STUCK_LOGIN_WAIT_PERIOD) {
      p.getActionSender().sendMessage("You cannot do this after you have recently logged in")
      return ;
    }
    if (p.getLocation().inModRoom() && !p.isMod()) {
      message(p, "You cannot use ::stuck here")
    } else if (!p.isMod() && GameEngine.getTime() - p.getLastMoved() < CH.STUCK_STAND_STILL_TIME && GameEngine.getTime() - p.getCastTimer() < 300000) {
      message(p, "There is a 5 minute delay on using ::stuck, please stand still for " + (CH.STUCK_STAND_STILL_TIME / 1000 / 60) + " minutes.")
      message(p, "This command is logged ONLY use it when you are REALLY stuck.")
    } else if (!p.inCombat() && GameEngine.getTime() - p.getCombatTimer() > CH.STUCK_STAND_STILL_TIME || p.isMod()) {
      Logger.mod(p.getUsername() + " used stuck at " + p.getX() + ":" + p.getY())
      p.setCastTimer()
      p.teleport(CH.STUCK_X, CH.STUCK_Y, true)
    } else {
      message(p, "You cannot use ::stuck for " + (CH.STUCK_STAND_STILL_TIME / 1000 / 60) + " minutes after combat")
    }
  }

  def info(p: Player, args: Array[String], ls: LSMiscPacketBuilder) {
    if (args.length != 1) {
      message(p, "Invalid args. Syntax: INFO name")
      return ;
    }
    ls.requestPlayerInfo(p, DataConversions.usernameToHash(args(0)))
  }

  def info(p: Player, args: Array[String], world: World) {
    val p1 = world.getPlayer(DataConversions.usernameToHash(args(0)))
    if (p1 == null) {
      message(p, args(0) + " is offline?")
      return ;
    }
    p.setLastPlayerInfo2(p.getUsername())
    p1.getActionSender().sendInfo2()
    message(p, "Requesting info.. please wait")
  }

  def info_(p: Player, args: Array[String], world: World) {
    if (args(0) != null) {
      val p = world.getPlayer(DataConversions.usernameToHash(args(0)))
      var line = "@red@DONT SHOW THIS TO PUBLIC! @gre@Last 75 (Casting) Intervals for @yel@ " + p.getUsername() + ": @whi@"
      for (i <- 0 to p.getIntervals.size()) {
        line += " - " + p.getIntervals.get(i)
      }
      alert(p, line)
    }
  }

  def town(p: Player, args: Array[String]) {
    val town = args(0);
    if (town != null) {
      for (i <- 0 to CH.towns.length)
        if (town.equalsIgnoreCase(CH.towns(i))) {
          p.teleport(CH.townLocations(i).getX(), CH.townLocations(i).getY(), false)
        }
    }
  }

  def ban(p: Player, args: Array[String], ls: LSMiscPacketBuilder) {
    if (args.length != 1) {
      message(p, "Invalid args. Syntax: ban name")
      return ;
    }
    ls.banPlayer(p, DataConversions.usernameToHash(args(0)), true)
  }

  def unban(p: Player, args: Array[String], ls: LSMiscPacketBuilder) {
    if (args.length != 1) {
      message(p, "Invalid args. Syntax: unban name")
      return ;
    }
    ls.banPlayer(p, DataConversions.usernameToHash(args(0)), false)
  }

  def quest(p: Player, args: Array[String]) {
    if (args.length < 2) {
      message(p, "Invalid syntax! ::quest INDEX STAGE")
      return ;
    }

    p.setQuestStage(Integer.parseInt(args(0)), Integer.parseInt(args(1)), false)
    p.getActionSender().sendQuestInfo()
  }

  def questPoints(p: Player, args: Array[String]) {
    if (args.length < 1) {
      message(p, "Invalid syntax! ::questpoints POINTS");
      return ;
    }

    p.setQuestPoints(Integer.parseInt(args(0)), false);
    p.getActionSender().sendQuestInfo();
  }

  def dumpData(p: Player, args: Array[String]) {
    import org.moparscape.msc.gs.db.DBConnection

    if (args.length != 1) {
      message(p, "Invalid args. Syntax: ::dumpdata name")
      return ;
    }
    val usernameHash = DataConversions.usernameToHash(args(0))
    val username = DataConversions.hashToUsername(usernameHash)
    DBConnection.getReport().submitDupeData(username, usernameHash)
  }

  def shutdown(p: Player) {
    Logger.mod(p.getUsername() + " shut down the server!");
    Instance.getServer().kill()
  }

  def update(p: Player, args: Array[String], world: World) {
    var reason = ""
    if (args.length > 0) {
      args foreach { s =>
        reason += (s + " ")
      }
      reason = reason.substring(0, reason.length() - 1)
    }
    if (Instance.getServer().shutdownForUpdate()) {
      Logger.mod(p.getUsername() + " updated the server: " + reason)
      val itr = world.getPlayers().iterator
      while (itr.hasNext) {
        val p1 = itr.next
        alert(p1, "The server will be shutting down in 60 seconds: " + reason, false)
        p1.getActionSender().startShutdown(60)
      }

    }
  }

  def clearInv(p: Player) {
    p.getInventory().getItems().clear()
    p.getActionSender().sendInventory()
  }

  def enableMultiThreading(p: Player) {
    import org.moparscape.msc.gs.core.ClientUpdater

    ClientUpdater.threaded = !ClientUpdater.threaded
    message(p, "Threaded client updater: " + ClientUpdater.threaded)
  }

  def ipban(p: Player, args: Array[String], world: World) {
    val hash = DataConversions.usernameToHash(args(0))

    val itr = world.getPlayers.iterator
    while (itr.hasNext) {
      val p1 = itr.next
      if (p1.getUsernameHash == hash) {
        message(p, "IP ban on " + args(0) + ' ' + {
          if (IPBanManager.block(p1.getCurrentIP))
            "succeeded"
          else
            "failed"
        }
          + '.')
        return
      }
    }
    message(p, "No user found with the name " + args(0))
  }

  def unipban(p: Player, args: Array[String]) {
    message(p, "Removal of IP ban on " + args(0) + ' ' + {
      if (IPBanManager.unblock(args(0)))
        "succeeded"
      else
        "failed"
    }
      + '.')
  }
  
  def reloadIPBans(p:Player) {
    IPBanManager.reloadIPBans
    message(p, "IP bans reloaded")
  }

  // Helper methods

  def message(p: Player, msg: String) {
    p.getActionSender.sendMessage(msg)
  }

  def alert(p: Player, msg: String) {
    p.getActionSender.sendAlert(msg, true)
  }

  def alert(p: Player, msg: String, big: Boolean) {
    p.getActionSender.sendAlert(msg, big)
  }

  // Overriden methods

  @throws(classOf[Exception])
  override def handlePacket(p: Packet, session: IoSession) {
    val player = session.getAttachment().asInstanceOf[Player]
    if (player.isBusy()) {
      player.resetPath()
      return ;
    }
    player.resetAll()
    val s = new String(p.getData()).trim()
    val firstSpace = s.indexOf(" ")
    var cmd = s
    var args = new Array[String](0)
    if (firstSpace != -1) {
      cmd = s.substring(0, firstSpace).trim();
      args = s.substring(firstSpace + 1).trim().split(" ")
    }
    try {
      handleCommand(cmd.toLowerCase(), args, player)
    } catch {
      case e: Exception => e.printStackTrace
    }
  }
}