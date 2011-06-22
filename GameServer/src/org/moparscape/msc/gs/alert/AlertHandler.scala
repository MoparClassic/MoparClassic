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

/**
 * This is for out-of-game alerts.
 */
object AlertHandler extends Application {

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
          Medium.send(m._2, msg)
        }
      }
    })
  }

  def sendAlert(msg: String, priority: Int) {
    for (u <- users)
      sendAlert(msg, u, priority)
  }

  def load {
    val config = XML.loadFile("alert-config.xml")
    val users1 = (config \\ "user")
    val list = new ListBuffer[User];
    for (u <- users1) {
      list += parseUser(u)
    }
    users = list.toList
  }

  private def parseUser(u: Node) = {
    val name = (u \ "name").text
    val credentials = {
      val map = new HashMap[Int, Medium]
      val creds = u \ "email"
      for (c <- creds) {
        map.put(Integer.parseInt((c \ "priority").text), new Medium("email", (c \ "address").text))
      }
      map.toMap
    }
    new User(name, credentials)
  }
}

private class User(name_ : String, data_ : Map[Int, Medium]) {
  def name = name_
  def data = data_
}

private object Medium {

  var meds = new HashMap[String, (String, String) => Unit]

  {
    meds += (("email", EMail.send _))
  }

  def send(m: Medium, msg: String) {
    val pf = meds.get(m.identifier).get
    pf(msg, m.recip)
  }
}

private class Medium(identifier_ : String, recip_ : String) {
  def identifier = identifier_
  def recip = recip_
}

private trait Protocol {
  def send(msg: String, recip: String)
}

private object EMail extends Protocol {
  override def send(msg: String, recip: String) = {
    val props = new Properties()
    val config = XML.loadFile("alert-config.xml") \\ "credentials"
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