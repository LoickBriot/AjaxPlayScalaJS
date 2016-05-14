package webapp.client


import webapp.shared.MainShared.MyApi
import scala.scalajs.js.JSApp
import org.scalajs.dom
import dom.document

import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom

import scala.concurrent.Future
import scalajs.concurrent.JSExecutionContext.Implicits.queue
import scalatags.JsDom.all._
import upickle.default._
import upickle.Js
import autowire._

object MainJS extends JSApp {

  def main(): Unit = {

    println("Hello world!")

    appendPar(document.body, "Hello World")
  }


  def appendPar(targetNode: dom.Node, text: String): Unit = {
    val parNode = document.createElement("p")
    val textNode = document.createTextNode(text)
    parNode.appendChild(textNode)
    targetNode.appendChild(parNode)
  }




  object AjaxClient extends autowire.Client[Js.Value, Reader, Writer] {
    override def doCall(req: Request): Future[Js.Value] = {

      println(req.args.toSeq)
      println(Js.Obj(req.args.toSeq: _*))
      println(upickle.json.write(Js.Obj(req.args.toSeq: _*)))
      dom.ext.Ajax.post(
        url = "/api/" + req.path.mkString("/"),
        data = upickle.json.write(Js.Obj(req.args.toSeq: _*)),
        headers = Map("Content-Type" -> "application/json")
      ).map(_.responseText)
        .map(upickle.json.read)
    }

    def read[Result: Reader](p: Js.Value) = readJs[Result](p)
    def write[Result: Writer](r: Result) = writeJs(r)
  }


  @JSExport
  def getTodos(): Unit = {
    AjaxClient[MyApi].getTodos().call().foreach { todos =>
      todos.foreach { todo =>
        appendPar(document.body, todo)

      }
    }
    appendPar(document.body, "You clicked the button!")
  }

  @JSExport
  def getRows(i : Int): Unit = {
    AjaxClient[MyApi].getRows(i).call().foreach { todos =>
      todos.foreach { todo =>
        appendPar(document.body, todo.id.toString)
        appendPar(document.body, todo.name.toString)
        appendPar(document.body, todo.location.toString)
        appendPar(document.body, todo.phone.toString)
      }
    }
    appendPar(document.body, "You clicked the button!")
  }




}