package controllers


import java.util.UUID

import controllers.PlayAutowire.{AutowireContext, AutowirePlayServer}
import models.Rect
import webapp.shared.MainShared._
import play.api.mvc.{Action, Controller}
import java.io.File
import scala.concurrent.ExecutionContext.Implicits.global

class Application extends Controller  {

  val apiService = new ApiService()

  def index(photo1:String = "", photo2: String) = Action { implicit request =>
    Ok(views.html.HomePage("app", photo1, photo2))
  }

  def ajaxAPI = PlayAutowire.api(AjaxServer)_


  object AjaxServer extends AutowirePlayServer[MyApi] {

    override def routes(target: MyApi) = route[MyApi](apiService)
    override def createImpl(autowireContext: AutowireContext): MyApi = new AjaxServerImpl(autowireContext)


    class AjaxServerImpl(context: AutowireContext) extends MyApi {
      def getRows(i: Int) = apiService.getRows(i)
      def getTodos() = apiService.getTodos()
      def getRect(rectMap: Map[String,Rect], photo1_num: String, photo_name: String) = apiService.getRect(rectMap, photo1_num, photo_name)
      def getResult(eye_width: Int, photo1: String, photo2: String, rectMap: Map[String,Rect]) = apiService.getResult(eye_width, photo1, photo2, rectMap)
    }
  }


  def uploadAction = Action(parse.multipartFormData) { request =>
    var result = ""
    var extension = ""
    var photo1 =""
    var photo2 =""
    var rootPath = System.getProperty("user.dir")

    try {
      val filesList = request.body.files.toList

      if (filesList.nonEmpty) {

        var i = 1
        for (image <- filesList) {
          var inputName: String = image.filename
          var uniqueName = generateAnUniqueName(inputName)
          photo1 = if(i==1) uniqueName else photo1
          photo2 = if(i==2) uniqueName else photo2
          image.ref.moveTo(new File(rootPath+ "/src/server/public/images" + File.separator + uniqueName))
          extension = "."+getExtension(inputName)
          i+=1
        }
      }

    } catch {
      case e: Throwable => result = "Erreur durant le chargement."
    }

    Redirect(controllers.routes.Application.index(photo1, photo2))
  }


  def getExtension(inputName: String): String = {
    var extension = ""
    if (inputName.lastIndexOf('.') > 0) {
      extension = inputName.substring(inputName.lastIndexOf('.') + 1);
    }
    return extension
  }


  def generateAnUniqueName(imageName: String): String = {

    var extension = getExtension(imageName)
    var name = UUID.randomUUID().toString().replaceAll("-", "") + "." + extension

    return name
  }



}
