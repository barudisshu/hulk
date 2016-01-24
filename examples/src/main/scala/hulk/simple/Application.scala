package hulk.simple

import akka.http.scaladsl.model.HttpMethods
import hulk.HulkHttpServer
import hulk.http._
import hulk.routing.{RouteDef, Router}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by reweber on 26/12/2015
  */
object Application extends App {

  val router = new SimpleRouter()
  HulkHttpServer(router).run()
}

class SimpleRouter() extends Router {
  val simpleController = new SimpleController()
  val notFoundController = new NotFoundController()

  override def router: Map[RouteDef, Action] = Map(
    (HttpMethods.GET, "/test") -> simpleController.testGet,
    (HttpMethods.POST, "/test") -> simpleController.testPost
  )
}

class SimpleController() {
  def testGet = Action { request =>
    Ok()
  }

  def testPost = AsyncAction { request =>
    request.body.asJson().map{ jsonOpt =>
      jsonOpt.map(Ok(_)).getOrElse(BadRequest())
    }
  }
}

class NotFoundController() {
  def testNotFound = Action { request =>
    NotFound()
  }
}