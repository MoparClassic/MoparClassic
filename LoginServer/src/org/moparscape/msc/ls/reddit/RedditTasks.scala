package org.moparscape.msc.ls.reddit

import scala.collection.JavaConversions._
import scala.io.Source
import org.apache.http.Consts
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.params.ClientPNames
import org.apache.http.client.params.CookiePolicy
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import org.apache.http.params.HttpProtocolParams
import org.apache.http.util.EntityUtils
import org.moparscape.msc.ls.service.CodeService
import org.apache.http.client.HttpClient
import scala.collection.mutable.Queue
import java.util.TimerTask
import java.util.Timer

object RedditTasks {

	val queue = Queue[Request]()

	val timer = new Timer
	def getTask : TimerTask = new TimerTask {
		override def run {
			try {
				val r = queue.dequeue
				r.run
				timer.schedule(getTask, 2000 * r.requests)
			} catch {
				case e : Exception =>
					timer.schedule(getTask, 2000)
			}

		}
	}

	timer.schedule(getTask, 2000)

	val creds = RedditCredentials.credentials
	val reddit = "http://www.reddit.com/"
	val client = new DefaultHttpClient

	def start {
		client.getParams.setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY)
		HttpProtocolParams.setUserAgent(client.getParams, creds.agent)

		queue += new Request(() => {
			val post = new HttpPost(reddit + "api/login")
			post.setEntity(new UrlEncodedFormEntity(List(
				new BasicNameValuePair("api_type", "json"),
				new BasicNameValuePair("rem", "True"),
				new BasicNameValuePair("user", creds.user),
				new BasicNameValuePair("passwd", creds.pass)
			), Consts.UTF_8))
			EntityUtils.consume(client.execute(post).getEntity)
		})

		val threadChecker = new ThreadChecker

		val responder = new MessageResponder

		timer.scheduleAtFixedRate(new TimerTask {
			override def run = threadChecker.run
		}, 0, 60000)

		timer.scheduleAtFixedRate(new TimerTask {
			override def run = responder.run
		}, 0, 30000)

	}

}

class Request(r : () => Unit, val requests : Int = 1) {
	def run = r()
}

class PrivateMessage(modHash : String, name : String, msg : String) extends Task(RedditTasks.client) {
	override def run {
		RedditTasks.queue += new Request(() => {
			var post = new HttpPost(RedditTasks.reddit + "api/comment")
			post.setEntity(new UrlEncodedFormEntity(List(
				new BasicNameValuePair("api_type", "json"),
				new BasicNameValuePair("text", msg),
				new BasicNameValuePair("thing_id", name),
				new BasicNameValuePair("uh", modHash)
			), Consts.UTF_8))
			EntityUtils.consume(client.execute(post).getEntity)
		})
	}
}

abstract class Task(val client : HttpClient) {
	def run
}

private class ThreadChecker extends Task(RedditTasks.client) {
	override def run {
		RedditTasks.queue += new Request(() => {
			val get = new HttpGet(RedditTasks.reddit + "r/moparclassic/comments/1kdp1q/get_ready_for_action/.json")
			val resp = client.execute(get)
			val p = new Page(Source.fromInputStream(resp.getEntity.getContent).getLines.mkString)

			p.getTopLevelComment("t1").foreach {
				a =>
					println(a.getTag("author") + " - " + (a.getTag("ups").toInt - a.getTag("downs").toInt))
			}

		})

	}
}

private class MessageResponder extends Task(RedditTasks.client) {
	override def run {
		RedditTasks.queue += new Request(() => {
			var get = new HttpGet(RedditTasks.reddit + "message/unread/.json")
			val p = new Page(Source.fromInputStream(client.execute(get).getEntity.getContent).getLines.mkString)
			get = new HttpGet(RedditTasks.reddit + "message/inbox/")
			EntityUtils.consume(client.execute(get).getEntity)
			val modHash = p.getModHash

			p.getTopLevelComment("t4").foreach {
				a =>
					RedditMessageProcessor.process(modHash, a.getTag("name"), a.getTag("body"), a.getTag("author"))
			}
		}, 2)
	}
}