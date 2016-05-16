package webapp.shared

import files.FileManager
import images.ImageManager
import models.Rect


/**
  * Created by loick on 5/13/16.
  */
object MainShared {
  trait MyApi{
    def getTodos() : Seq[String]

    def getRows(i: Int) : Seq[TableRow]

    def getRect(rectMap: Map[String,Rect], photo1_num: String, photo_name: String): String

    def getResult(eye_width: Int, photo1: String, photo2: String, rectMap: Map[String,Rect]): Double
  }


  case class TableRow(var id: Int, var name : String, var location: String, var phone: String)

  class ApiService extends MyApi {
    var todos = Seq(
      "Wear shirt that says 'Life'. Hand out lemons on street corner.",
      "Make vanilla pudding. Put in mayo jar. Eat in public.",
      "Walk away slowly from an explosion without looking back.",
      "Sneeze in front of the pope. Get blessed."
    )

    var rows = Seq(
      new TableRow(1,"loick", "eclaron", "aa"),
      new TableRow(2,"mathias", "eclaron", "bb"),
      new TableRow(3,"gwladys", "eclaron", "cc"),
      new TableRow(4,"michael", "eclaron", "dd")
    )




    override def getTodos(): Seq[String] = {
      return todos
    }

    override def getRows(i: Int): Seq[TableRow] = {
      var new_rows = Seq[TableRow]()
      for(i <- 0 until i){
        new_rows++=rows
      }
      return new_rows
    }

    override def getRect(rectMap: Map[String,Rect], photo1_num: String, photo_name: String): String= {
      var rootPath = System.getProperty("user.dir")
      var image = ImageManager.loadImage(rootPath+"/src/server/public/images/"+photo_name)

      var str_image = ""
      photo1_num match {

        case "1" =>{
          str_image = ImageManager.imageToString(ImageManager.getSubimage(image, rectMap("1")), photo_name)
        }

        case "2" =>{
          str_image = ImageManager.imageToString(ImageManager.getSubimage(image, rectMap("2")), photo_name)
        }

        case "3" =>{

          var rect1 = rectMap("1")
          var rect3 = rectMap("3")
          var new_rect = new Rect(rect1.x+rect3.x,rect1.y+rect3.y,rect3.width, rect3.height)
          str_image = ImageManager.imageToString(ImageManager.getSubimage(image, new_rect), photo_name)
        }

        case "4" =>{
          var rect2 = rectMap("2")
          var rect4 = rectMap("4")
          var new_rect = new Rect(rect2.x+rect4.x,rect2.y+rect4.y,rect4.width, rect4.height)
          str_image = ImageManager.imageToString(ImageManager.getSubimage(image, new_rect), photo_name)
        }

        case "5" =>{
          var rect1 = rectMap("1")
          var rect3 = rectMap("3")
          var rect5 = rectMap("5")
          var new_rect = new Rect(rect1.x+rect3.x+rect5.x,rect1.y+rect3.y+rect5.y,rect5.width, rect5.height)
          str_image = ImageManager.imageToString(ImageManager.getSubimage(image, new_rect), photo_name)
        }

        case "6" =>{
          var rect2 = rectMap("2")
          var rect4 = rectMap("4")
          var rect6 = rectMap("6")
          var new_rect = new Rect(rect2.x+rect4.x+rect6.x,rect2.y+rect4.y+rect6.y,rect6.width, rect6.height)
          str_image = ImageManager.imageToString(ImageManager.getSubimage(image, new_rect), photo_name)
        }

      }

      return str_image
    }


    /*

    rect1: Rect(138,182,458,156)
    rect2: Rect(119,122,476,159)
    rect3: Rect(253,42,43,44)
    rect4: Rect(212,41,52,50)
    rect5: Rect(2,15,14,12)
    rect6: Rect(22,20,13,14)
    pix1: 11.45
    pix2: 11.9
    new rect4: Rect(212,41,52,50)
    new rect6: Rect(212,41,52,50)
    center3: (274,64)
    center4: (238,66)
    center5: (9,21)
    center6: (28,27)
    vector1: (265,43)
    vector2: (210,39)
    final: (55,4)

     */
    def getResult(eye_width: Int, photo1: String, photo2: String, rectMap: Map[String,Rect]): Double ={
      var res= 0

      var rect1 = rectMap("1")
      println("rect1: " + rect1)
      var rect2 = rectMap("2")
      println("rect2: " + rect2)
      var rect3 = rectMap("3")
      println("rect3: " + rect3)
      var rect4 = rectMap("4")
      println("rect4: " + rect4)
      var rect5 = rectMap("5")
      println("rect5: " + rect5)
      var rect6 = rectMap("6")
      println("rect6: " + rect6)

      var pix_by_mm1 = (rect1.width.toFloat / eye_width)
      println("pix1: " + pix_by_mm1)
      var pix_by_mm2 = (rect2.width.toFloat / eye_width)
      println("pix2: " + pix_by_mm2)

      // tout par rapport à pix_by_mm1
      var new_rect4 = new Rect(rect4.x, rect4.y, (rect4.width*(pix_by_mm2/pix_by_mm1)).toInt, (rect4.height*(pix_by_mm2/pix_by_mm1)).toInt)
      println("new rect4: " + new_rect4)
      var new_rect6 = new Rect((rect6.x*(pix_by_mm2/pix_by_mm1)).toInt, (rect6.y*(pix_by_mm2/pix_by_mm1)).toInt, (rect6.width*(pix_by_mm2/pix_by_mm1)).toInt, (rect6.height*(pix_by_mm2/pix_by_mm1)).toInt)
      println("new rect6: " + new_rect6)


      var center3= ( ((rect3.width)/2).toInt, ((rect3.height)/2).toInt)
      println("center3: " + center3)

      var center4= ( ((new_rect4.width)/2).toInt, ((new_rect4.height)/2).toInt)
      println("center4: " + center4)

      var center5= ( ((2*rect5.x + rect5.width)/2).toInt, ((2*rect5.y + rect5.height)/2).toInt)
      println("center5: " + center5)

      var center6= ( ((2*new_rect6.x + new_rect6.width)/2).toInt, ((2*new_rect6.y + new_rect6.height)/2).toInt)
      println("center6: " + center6)

      var vector1 = ( center3._1 - center5._1, center3._2 - center5._2 )
      println("vector1: " + vector1)
      var vector2 = ( center4._1 - center6._1, center4._2 - center6._2 )
      println("vector2: " + vector2)

      var final_vector = ( vector1._1 - vector2._1, vector1._2 - vector2._2)
      println("final: " + final_vector)
      var res_in_mm =(Math.sqrt( (final_vector._1*final_vector._1 + final_vector._2*final_vector._2).toDouble ) / pix_by_mm1)
      var res_with_one_number_after_coma = ((res_in_mm *10).toInt).toDouble/10
      return res_with_one_number_after_coma
    }

  }

}

