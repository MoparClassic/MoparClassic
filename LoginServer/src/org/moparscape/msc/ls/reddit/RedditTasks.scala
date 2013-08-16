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
	timer.schedule(new TimerTask {
		override def run {
			if (queue.size > 0) {
				val r = queue.dequeue
				r.run
				timer.schedule(this, 2000 * r.requests)
			} else {
				timer.schedule(this, 2000)
			}

		}
	}, 2000)

	val creds = RedditCredentials.credentials
	val api = "http://www.reddit.com/api/"

	def start {
		val client = new DefaultHttpClient()
		client.getParams.setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY)
		HttpProtocolParams.setUserAgent(client.getParams, creds.agent)

		queue += new Request(() => {
			val post = new HttpPost(api + "login")
			post.setEntity(new UrlEncodedFormEntity(List(
				new BasicNameValuePair("api_type", "json"),
				new BasicNameValuePair("rem", "True"),
				new BasicNameValuePair("user", creds.user),
				new BasicNameValuePair("passwd", creds.pass)
			), Consts.UTF_8))
			EntityUtils.consume(client.execute(post).getEntity)
		})

		new ThreadChecker(client).run
		new MessageResponder(client).run

	}

}

class Request(r : () => Unit, val requests : Int = 1) {
	def run = r()
}

private abstract class Task(client : HttpClient) {
	def run
}

private class ThreadChecker(client : HttpClient) extends Task(client) {
	override def run {
		RedditTasks.queue += new Request(() => {
			val get = new HttpGet("http://www.reddit.com/r/moparclassic/comments/1kdp1q/get_ready_for_action/.json")
			val resp = client.execute(get)
			val p = new Page(Source.fromInputStream(resp.getEntity.getContent).getLines.mkString)

			p.getTopLevelComment("t1").foreach {
				a =>
					println(a.getTag("author") + " - " + (a.getTag("ups").toInt - a.getTag("downs").toInt))
			}
		})

	}
}

private class MessageResponder(client : HttpClient) extends Task(client) {
	override def run {
		RedditTasks.queue += new Request(() => {
			var get = new HttpGet("http://www.reddit.com/message/unread/.json")
			val p = new Page(Source.fromInputStream(client.execute(get).getEntity.getContent).getLines.mkString)
			val modHash = p.getModHash

			p.getTopLevelComment("t4").foreach {
				a =>
					RedditTasks.queue += new Request(() => {
						var post = new HttpPost(RedditTasks.api + "comment")
						post.setEntity(new UrlEncodedFormEntity(List(
							new BasicNameValuePair("api_type", "json"),
							new BasicNameValuePair("text", "Here is a unique code: " + CodeService.newCode),
							new BasicNameValuePair("thing_id", a.getTag("name")),
							new BasicNameValuePair("uh", modHash)
						), Consts.UTF_8))
						EntityUtils.consume(client.execute(post).getEntity)
					})
			}

			get = new HttpGet("http://www.reddit.com/message/inbox/")
			EntityUtils.consume(client.execute(get).getEntity)
		}, 2)
	}
}