package hulk.websocket

import akka.actor._
import akka.http.scaladsl.model.HttpMethods
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.actor.ActorPublisher
import akka.stream.scaladsl._
import hulk.HulkHttpServer
import hulk.http._
import hulk.http.ws.DefaultWebSocketSenderActor
import hulk.ratelimiting.{RateLimitBy, RateLimiter}
import hulk.routing.{RouteDef, Router}
import scala.concurrent.duration._

/**
  * Created by reweber on 24/02/2016
  */
object Application extends App {

  val router = new SimpleRouter()
  HulkHttpServer(router).run()
}

class SimpleRouter() extends Router {
  val simpleController = new SimpleController()

  override def router: Map[RouteDef, Action] = Map(
    (HttpMethods.GET, "/wsTest") -> simpleController.testGet,
    (HttpMethods.GET, "/wsTestFilter") -> simpleController.testGetFilter,
    (HttpMethods.GET, "/wsTestDifferentActor") -> simpleController.testGetDifferentActorEcho
  )
}

class SimpleController() {
  implicit val system = ActorSystem("system")
  val senderActor = system.actorOf(Props(classOf[DefaultWebSocketSenderActor], None))

  def testGet = WebSocketAction { request =>
    (Source.fromPublisher(ActorPublisher(senderActor)).initialTimeout(10 minutes), {
      case TextMessage.Strict(txt) => senderActor ! TextMessage.Strict(s"Response: $txt")
      case _ => //ignore
    })
  }

  def testGetFilter = WebSocketAction(Seq(RateLimiter(RateLimitBy.ip, 5, 5 seconds)), { request =>
    (Source.fromPublisher(ActorPublisher(senderActor)).initialTimeout(10 minutes), {
      case TextMessage.Strict(txt) => senderActor ! TextMessage.Strict(s"Response: $txt")
      case _ => //ignore
    })
  })

  def testGetDifferentActorEcho = WebSocketAction(Seq(RateLimiter(RateLimitBy.ip, 5, 5 seconds)), { request =>

    val currActor = system.actorOf(Props(classOf[DefaultWebSocketSenderActor], None))

    (Source.fromPublisher(ActorPublisher(currActor)).initialTimeout(10 minutes), {
      case TextMessage.Strict(txt) => currActor ! TextMessage.Strict(s"Response: $txt")
      case _ => //ignore
    })
  })
}
