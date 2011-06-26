package org.moparscape.msc.gs.alert

import scala.xml.XML
import scala.xml.Node
import scala.collection.mutable.HashMap
import java.util.ArrayList
import scala.collection.mutable.ListBuffer
import java.util.Properties
import javax.mail.Session
import javax.mail.internet.MimeMessage
import javax.mail.Message
import javax.mail.internet.InternetAddress
import java.util.concurrent.Executors
import org.moparscape.msc.config.Config

/**
 * This is for out-of-game alerts.
 *
 * @author CodeForFame
 */
object AlertHandler {

  private val executor = Executors.newSingleThreadExecutor()

  private var users: List[User] = Nil

  load

  def sendAlert(msg: String, recip: String, priority: Int) {
    for (u <- users; if (u.name == recip))
      sendAlert(msg, u, priority)
  }

  private def sendAlert(msg: String, recip: User, priority: Int) {
    executor.execute(new Runnable() {
      override def run() {
        val meds = recip.data.filter(p => p._1 <= priority)
        for (m <- meds) {
          Service.send(m._2, msg)
        }
      }
    })
  }

  /**
   * Sends an alert to all users.
   */
  def sendAlert(msg: String, priority: Int) {
    for (u <- users)
      sendAlert(msg, u, priority)
  }

  /**
   * Loads the config file.
   */
  private def load {
    val config = XML.loadFile(Config.ALERT_CONFIG)
    val users1 = (config \\ "user")
    val list = new ListBuffer[User];
    for (u <- users1) {
      list += parseUser(u)
    }
    users = list.toList
  }

  /**
   * Parses the XML and creates a User from it.
   */
  private def parseUser(u: Node) = {
    val name = (u \ "name").text
    val credentials = {
      val map = new HashMap[Int, Service]
      val creds = u \ "email"
      for (c <- creds) {
        map.put(Integer.parseInt((c \ "priority").text), new Service("email", (c \ "address").text))
      }
      map.toMap
    }
    new User(name, credentials)
  }
}

/**
 * This class contains information for the user, such as name, and preferences for Services.
 *
 * @author CodeForFame
 */
private class User(name_ : String, data_ : Map[Int, Service]) {
  def name = name_
  def data = data_
}

/**
 * The companion object for the Service class.
 * This is where you 'register' services.
 *
 * @author CodeForFame
 */
private object Service {

  var services = new HashMap[String, (String, String) => Unit]

  {
    services += (("email", EMail.send _))
  }

  /**
   * Sends a message via the specified service.
   */
  def send(s: Service, msg: String) {
    val pf = services.get(s.identifier).get
    pf(msg, s.recip)
  }
}

/**
 * A class that is for defining a service.
 *
 * @author CodeForFame
 */
private class Service(identifier_ : String, recip_ : String) {
  def identifier = identifier_
  def recip = recip_
}

/**
 * Services should have this trait, you should override the send method.
 *
 * @author CodeForFame
 */
private trait ServiceTrait {
  def send(msg: String, recip: String)
}

/**
 * This Service sends an alert via e-mail.
 *
 * @author CodeForFame
 */
private object EMail extends ServiceTrait {
  override def send(msg: String, recip: String) = {
    val props = new Properties()
    val config = XML.loadFile(Config.ALERT_CONFIG) \\ "credentials"
    val sender = config \ "user" text
    val pass = config \ "pass" text
    val host = config \ "host" text
    val port = Integer.parseInt(config \ "port" text);
    props.put("mail.transport.protocol", config \ "protocol" text)
    props.put("mail.smtps.host", host)
    props.put("mail.smtps.auth", config \ "auth" text)

    val mailSession = Session.getDefaultInstance(props)
    mailSession.setDebug(false)
    val transport = mailSession.getTransport()
    val message = new MimeMessage(mailSession)
    message.setSubject("MoparRSC Alert")
    message.setContent(msg, "text/plain")
    message.addRecipient(Message.RecipientType.TO, new InternetAddress(recip))
    transport.connect(host, port, sender, pass)
    transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO))
    transport.close()
  }
}