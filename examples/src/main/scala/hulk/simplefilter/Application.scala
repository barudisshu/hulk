package hulk.simplefilter

import akka.http.scaladsl.model.HttpMethods
import hulk.HulkHttpServer
import hulk.filtering.Filter.Next
import hulk.filtering.{Filter, Filters}
import hulk.http._
import hulk.routing._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by reweber on 26/12/2015
  */
object Application extends App {

  val router = new SimpleRouter()
  HulkHttpServer(router).run()
}

class SimpleRouter() extends Router with Filters {
  val simpleController = new SimpleController()

  override def router: Map[RouteDef, Action] = Map(
    (HttpMethods.GET, "/test") -> simpleController.testGet,
    (HttpMethods.POST, "/test") -> simpleController.testPost
  )

  override def filters: Seq[Filter] = Seq(new SimpleLogRequestTimeFilter, new SimpleAuthFilter)
}

class SimpleAuthFilter extends Filter {
  def isAuthenticated = {
    //Authentication stuff happening
    false //We just default to false here
  }

  override def apply(next: Next): (HulkHttpRequest) => Future[HulkHttpResponse] = {
    case req : HulkHttpRequest if req.method == HttpMethods.POST && req.path == "/needsAuth" =>
      if(isAuthenticated) next(req) else Future(Unauthorized())
    case req: HulkHttpRequest => next(req)
  }
}

class SimpleLogRequestTimeFilter extends Filter {
  override def apply(next: Next): (HulkHttpRequest) => Future[HulkHttpResponse] = {
    case req: HulkHttpRequest =>
      val currTime = System.currentTimeMillis()
      next andThen { response =>

        val t = System.currentTimeMillis() - currTime
        println(s"$t ms")

        response
      } apply req
  }
}

class SimpleController() {
  def testGet = Action { request =>
    Future.successful(Ok())
  }

  def testPost = Action { request =>
    request.body.asJson().map{ jsonOpt =>
      jsonOpt.map(Ok(_)).getOrElse(BadRequest())
    }
  }
}