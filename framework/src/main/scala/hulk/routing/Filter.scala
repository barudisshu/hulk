package hulk.routing

import hulk.http.{HulkHttpResponse, HulkHttpRequest}
import hulk.routing.Filter.Next

import scala.concurrent.Future

/**
  * Created by reweber on 18/12/2015
  */
trait Filter {
  def filter(next: Next): HulkHttpRequest => FilterResult
}

object Filter {
  type Next = (HulkHttpResponse => Future[HulkHttpResponse])
}