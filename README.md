#Hulk#

Hulk is a web framework to create RESTful API's. It is built on top of Akka HTTP.

Features include:

- Basics (Routing, Filtering, Controller Actions)
- Simple templating engine
- Rate limiting
- Versioning
- Swagger
- Built in metrics

Features yet to come:

- HAL/ Json LD support
- Performance Testing support
- OAuth client/server

## Simple usage example ##

```scala
object Application extends App {

  val action = Action(request => Ok())
  val router = new Router {
    override def router: Map[RouteDef, Action] = Map((HttpMethods.GET, "/test") -> action)
  }

  HulkHttpServer(router).run()
}
```

More examples: https://github.com/reneweb/hulk/tree/master/examples/src/main/scala/hulk