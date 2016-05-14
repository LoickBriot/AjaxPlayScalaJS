package controllers

import play.api.http.{ContentTypes, ContentTypeOf}
import play.api.mvc.{RequestHeader, Action, Controller}
import upickle.Js
import upickle.default._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object PlayAutowire extends Controller {
  implicit def contentTypeOf_upickle_Js_Value(implicit jsValue: Js.Value) =
    ContentTypeOf[Js.Value](Some(ContentTypes.JSON))

  case class AutowireContext(playRequest: RequestHeader)

  trait AutowirePlayServer[T] extends autowire.Server[Js.Value, Reader, Writer] {
    def write[Result: Writer](r: Result) = writeJs(r)
    def read[Result: Reader](p: Js.Value) = readJs[Result](p)

    def routes(target: T): Router
    def createImpl(autowireContext: AutowireContext): T
  }

  def api[T](server: AutowirePlayServer[T])(s: String) = Action.async { implicit request =>
    val path = s.split("/").toSeq

    request.body.asJson.map { json =>
      val args = json.toString()

      upickle.json.read(args) match {
        case Js.Obj(objArgs @ _*) =>
          val req = autowire.Core.Request(path, objArgs.toMap)
          val requestSession = server.createImpl(
            AutowireContext(playRequest = request)
          )
          server.routes(requestSession)(req).flatMap { responseData =>
            Future.successful(Ok(upickle.json.write(responseData)))
          }
        case _ =>
          Future.failed(new Exception("Arguments need to be a valid JSON object"))
      }
    }.getOrElse {
      println("Got bad request: " + request.body.asRaw.toString)
      Future.successful(BadRequest)
    }
  }
}
