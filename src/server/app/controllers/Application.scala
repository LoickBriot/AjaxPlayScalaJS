package controllers

import java.nio.ByteBuffer

import controllers.PlayAutowire.{AutowireContext, AutowirePlayServer}
import webapp.shared.MainShared._
import play.api.mvc.{Action, Controller}
import autowire._
import upickle._
import upickle.default._
import boopickle.Default._
import scala.concurrent.ExecutionContext.Implicits.global
import controllers.PlayAutowire

import scala.reflect.ClassTag

class Application extends Controller  {

  val apiService = new ApiService()

  def index = Action { implicit request =>
    Ok(views.html.HomePage("app", "It's my app!"))
  }


  def ajaxAPI = PlayAutowire.api(AjaxServer)_



  object AjaxServer extends AutowirePlayServer[MyApi] {

    override def routes(target: MyApi) = route[MyApi](apiService)
    override def createImpl(autowireContext: AutowireContext): MyApi = new AjaxServerImpl(autowireContext)


    class AjaxServerImpl(context: AutowireContext) extends MyApi {
      def getRows(i: Int) = apiService.getRows(i)
      def getTodos() = apiService.getTodos()
    }
  }







}