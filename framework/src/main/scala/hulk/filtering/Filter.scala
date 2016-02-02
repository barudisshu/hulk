package hulk.filtering

import hulk.filtering.Filter.Next
import hulk.http.{HulkHttpRequest, HulkHttpResponse}

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