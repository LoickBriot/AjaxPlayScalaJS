package images

import java.awt.image.BufferedImage
import java.awt.{Color, FlowLayout, GraphicsEnvironment, RenderingHints}
import java.io.{ByteArrayInputStream, ByteArrayOutputStream, File}
import javax.imageio.ImageIO
import javax.swing.{ImageIcon, JFrame, JLabel}
import files.FileManager
import models.Rect
import sun.misc.{BASE64Decoder, BASE64Encoder}


/**
  * Created by loick on 3/7/16.
  */
object ImageManager {

  val colorArray = Array(Color.BLACK,Color.RED, Color.GREEN, Color.BLUE,Color.PINK ,Color.YELLOW , Color.CYAN, Color.DARK_GRAY, Color.GRAY, Color.LIGHT_GRAY, Color.MAGENTA, Color.ORANGE)

  def loadImage(filePath: String): BufferedImage = {
    return ImageIO.read(new File(filePath))
  }

  def saveImage(image: BufferedImage, fileName: String): Unit ={
    ImageIO.write(image, FileManager.getExtension(fileName), new File(fileName));
  }

  def displayImage(img: BufferedImage, size: Int = 500) {
    val frame = new JFrame();
    frame.setSize(size, size)
    val width = Math.min(img.getWidth, size)
    var height = 0
    if (width == img.getWidth) {
      height = img.getHeight
    } else {
      height = (img.getHeight * (width / img.getWidth.toFloat)).toInt
    }
    frame.getContentPane().setLayout(new FlowLayout());
    frame.getContentPane().add(new JLabel(new ImageIcon(img.getScaledInstance(width, height, 1))));
    frame.pack();
    frame.setVisible(true);
  }

  def deepCopy(bi: BufferedImage): BufferedImage = {
    var cm = bi.getColorModel();
    var isAlphaPremultiplied = cm.isAlphaPremultiplied();
    var raster = bi.copyData(null);
    return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
  }


  def resizeImage(image: BufferedImage, nbMaxPixel: Int): BufferedImage = {
    var bImage = image
    if (bImage.getWidth * bImage.getHeight > nbMaxPixel) {
      var factor: Double = Math.min(Math.sqrt(nbMaxPixel / (bImage.getHeight * bImage.getWidth).toFloat), 1)
      var destWidth = (bImage.getWidth() * factor).toInt;
      var destHeight = (bImage.getHeight() * factor).toInt;
      var configuration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
      var bImageNew = configuration.createCompatibleImage(destWidth, destHeight);
      var graphics = bImageNew.createGraphics();
      graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
      graphics.drawImage(bImage, 0, 0, destWidth, destHeight, 0, 0, bImage.getWidth(), bImage.getHeight(), null);
      graphics.dispose();
      bImage=bImageNew
    }
    return bImage
  }

  def euclidianDistance(image1: BufferedImage, image2: BufferedImage): Int ={
    var minH = Math.min(image1.getHeight,image2.getHeight)
    var minW = Math.min(image1.getWidth,image2.getWidth)

    var dist = 0
    for (x<-0 until minW; y <- 0 until minH){
      var pixel1 = image1.getRGB(x, y)
      var red1 = (pixel1 >> 16) & 0xff;
      var green1 = (pixel1 >> 8) & 0xff;
      var blue1 = (pixel1) & 0xff;

      var pixel2 = image2.getRGB(x, y)
      var red2 = (pixel2 >> 16) & 0xff;
      var green2 = (pixel2 >> 8) & 0xff;
      var blue2 = (pixel2) & 0xff;

      dist += Math.sqrt( scala.math.pow(red2-red1,2) + scala.math.pow(blue2-blue1,2) + scala.math.pow(green2-green1,2)  ).toInt
    }

    return dist
  }

  def getColorPixel(image: BufferedImage, x:Int, y:Int): String ={
    var pixel = image.getRGB(x, y)
    var red = (pixel >> 16) & 0xff;
    var green = (pixel >> 8) & 0xff;
    var blue = (pixel) & 0xff;
    return s"$red;$green;$blue"
  }

  def colorDist(color1: String, color2: String): Int ={
    var list1 = color1.split(";")
    var list2 = color2.split(";")
    return (scala.math.pow(list1(0).toInt-list2(0).toInt,2) + scala.math.pow(list1(1).toInt-list2(1).toInt,2) + scala.math.pow(list1(2).toInt-list1(2).toInt,2)).toInt
  }



  def imageToString(bImage: BufferedImage, path: String)  : String = {
    var imageString = "";
    var formatName = path.substring(path.lastIndexOf('.')+1, path.length());

    //image to bytes
    var baos = new ByteArrayOutputStream();
    try {
      ImageIO.write(bImage, formatName, baos);
      baos.flush();
      var imageAsRawBytes = baos.toByteArray();
      baos.close();
      //bytes to string
      var b64enc = new BASE64Encoder();
      imageString = b64enc.encode(imageAsRawBytes);
    } catch {
      case e: Throwable => println(e)
    }
    return imageString;
  }


  def stringToImage(imageString: String)  : BufferedImage =  {
    //string to ByteArrayInputStream
    var bImage : BufferedImage= null;
    var b64dec = new BASE64Decoder();
    try {
      var output = b64dec.decodeBuffer(imageString);
      var bais = new ByteArrayInputStream(output);
      bImage = ImageIO.read(bais);
    } catch {
      case e: Throwable => println(e)
    }
    return bImage;
  }


  def getSubimage(image: BufferedImage, rect1: Rect): BufferedImage ={
    return image.getSubimage(Math.max(0, rect1.x), Math.max(0, rect1.y), Math.min(rect1.width, image.getWidth - rect1.x), Math.min(rect1.height,image.getHeight - rect1.y))
  }


}
