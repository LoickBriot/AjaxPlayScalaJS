package files

import java.io.{File, FileWriter}

/**
  * Created by loick on 3/7/16.
  */
object FileManager {


  def getListOfFiles(dir: String): List[String] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter(_.isFile).toList.map( x => x.getAbsolutePath())
    } else {
      List[String]()
    }
  }


  def getListOfSubfolder(dir: String): List[String] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter(_.isDirectory).toList.map( x => x.getAbsolutePath())
    } else {
      List[String]()
    }
  }


  def getExtension(inputName: String): String = {
    var extension = ""
    if (inputName.lastIndexOf('.') > 0) {
      extension = inputName.substring(inputName.lastIndexOf('.') + 1);
    }
    return extension
  }


  def addTextBeforeExtension(filename: String, addedStr : String): String = {
    var extension = getExtension(filename)
    var filenameWithoutExtension = filename.replace(s".$extension","")

    return filenameWithoutExtension + addedStr + s".$extension"
  }

  def addTextAfterParent(filename: String, addedStr : String): String = {

    return  new File(filename).getParent + addedStr + filename
  }

  def writeFile(path: String, content: String) {
    var fw = new FileWriter(path, false);
    fw.write(content);
    fw.close()
  }

  def getSubfoldersContainingAFileName(rootFolder: File, fileToFind: String): List[File] = {
    var files = rootFolder.listFiles()
    var res = List[File]()
    for (file <- files) {
      if (file.isDirectory()) {
        res++:=getSubfoldersContainingAFileName(file,fileToFind); // Calls same method again.
      } else {
        if (file.getName==fileToFind){
          res+:=rootFolder
        }
      }
    }
    return res
  }

  def readFile(path: String) : String ={
   return  scala.io.Source.fromFile(path, "UTF-8").mkString
  }


  def zip(outName:String, outFolder: String, files: List[File]) : File = {
    import java.io.{ BufferedInputStream, FileInputStream, FileOutputStream }
    import java.util.zip.{ ZipEntry, ZipOutputStream }
    val zip = new ZipOutputStream(new FileOutputStream(outFolder+"/"+outName))
    files.foreach { name =>
      zip.putNextEntry(new ZipEntry(outName.replaceAll(".zip","/")+name.getName))
      val in = new BufferedInputStream(new FileInputStream(name.getAbsolutePath))
      var b = in.read()
      while (b > -1) {
        zip.write(b)
        b = in.read()
      }
      in.close()
      zip.closeEntry()
    }
    zip.close()
    return new File(outFolder+"/"+outName)
  }
}
