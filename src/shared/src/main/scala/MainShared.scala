package webapp.shared


/**
  * Created by loick on 5/13/16.
  */
object MainShared {
  trait MyApi{
    def getTodos() : Seq[String]

    def getRows(i: Int) : Seq[TableRow]
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
  }

}
