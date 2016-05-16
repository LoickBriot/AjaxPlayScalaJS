package webapp.client


import models.Rect
import webapp.shared.MainShared.MyApi
import scala.scalajs.js.JSApp
import org.scalajs.dom
import dom.document

import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom

import scala.concurrent.Future
import scalajs.concurrent.JSExecutionContext.Implicits.queue
import upickle.default._
import upickle.Js
import autowire._
import org.scalajs.jquery.jQuery

object MainJS extends JSApp {

  @JSExport
  def main(): Unit = {

  }


  @JSExport
  def cropSelection(photo_num : String): Unit ={
    jQuery(
      s"""
         <script type="text/javascript">
            document.write('<input type="hidden" id="result${photo_num}_x" value="0">');
            document.write('<input type="hidden" id="result${photo_num}_y" value="0">');
            document.write('<input type="hidden" id="result${photo_num}_w" value="0">');
            document.write('<input type="hidden" id="result${photo_num}_h" value="0">');


         var d = document, ge = 'getElementById';

            $$('#interface${photo_num}').on('cropmove cropend',function(e,s,c){
             document.getElementById("result${photo_num}_x").value = c.x;
             document.getElementById("result${photo_num}_y").value = c.y;
             document.getElementById("result${photo_num}_w").value = c.w;
             document.getElementById("result${photo_num}_h").value = c.h;
           });
            jQuery(function($$){
              $$('#img${photo_num}').Jcrop({
                setSelect: [ 175, 100, 400, 300 ]
              },function(){
                var jcrop_api = this;
                $$('#button${photo_num}').click(function() {
                  document.getElementById("result${photo_num}_x").value=jcrop_api.ui.selection.get().x;
                  document.getElementById("result${photo_num}_y").value=jcrop_api.ui.selection.get().y;
                  document.getElementById("result${photo_num}_w").value=jcrop_api.ui.selection.get().w;
                  document.getElementById("result${photo_num}_h").value=jcrop_api.ui.selection.get().h;
                  jcrop_api.setOptions({canDrag :false});
                  jcrop_api.setOptions({canResize :false});
                  jcrop_api.setOptions({canDelete :false});
                  });
              });
            });
         </script>
        """).appendTo(document.body)
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

  }


  @JSExport
  def getRect(photo1_num:String, photo2_num:String, photo_name:String): Unit = {

    var rectMap = Map[String,Rect]()

    for(i <- 1 to 6){
      var element = document.getElementById("result"+i+"_x")
      if(element!=null) {
        var x = document.getElementById("result" + i + "_x").getAttribute("value").toInt
        var y = document.getElementById("result" + i + "_y").getAttribute("value").toInt
        var w = document.getElementById("result" + i + "_w").getAttribute("value").toInt
        var h = document.getElementById("result" + i + "_h").getAttribute("value").toInt
        rectMap += (i.toString -> new Rect(x, y, w, h))
      }
    }


    AjaxClient[MyApi].getRect(rectMap, photo1_num, photo_name).call().foreach { todos =>
        document.getElementById("img"+photo2_num).setAttribute("src", s"data:image/jpg;base64,$todos")
        //cropSelection("interface4","img4","img4","button4")
    }

  }




  @JSExport
  def getResult(eye_width: String): Unit = {

    var photo1 = "test1.jpg"
    var photo2 = "test2.jpg"
    var rectMap = Map[String, Rect]()

    for (i <- 1 to 6) {
      var element = document.getElementById("result" + i + "_x")
      if (element != null) {
        var x = document.getElementById("result" + i + "_x").getAttribute("value").toInt
        var y = document.getElementById("result" + i + "_y").getAttribute("value").toInt
        var w = document.getElementById("result" + i + "_w").getAttribute("value").toInt
        var h = document.getElementById("result" + i + "_h").getAttribute("value").toInt
        rectMap += (i.toString -> new Rect(x, y, w, h))
      }
    }

    AjaxClient[MyApi].getResult(eye_width.toInt,photo1,photo2,rectMap).call().foreach { todos =>
      appendPar(document.getElementById("final_result"), "La distance est de "+todos.toString+"mm.")
    }
  }



}