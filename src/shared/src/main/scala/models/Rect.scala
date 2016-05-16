package models

import derive.key

/**
  * Created by loick on 3/7/16.
  */
@key("Rect") case class Rect(var x: Int, var y: Int, var width: Int, var height: Int){

  def getArea(): Int ={
    return this.width*this.height
  }

  def isLocatedIn(rect:Rect): Boolean ={
    if (this.x >= rect.x & this.y >= rect.y & (this.x + this.width) <= (rect.x + rect.width) & (this.y + this.height) <= (rect.y + rect.height)){
      return true
    }
    return false
  }

  def isBiggerThan(rect: Rect): Boolean ={
    if ((this.getArea()) > (rect.getArea())){
      return true
    }
    return false
  }

  def isTheSameThan(rect: Rect, areaPercent: Int, gravCenter_dist:Int): Boolean ={
    if ((this.getArea()) > (1.0-(areaPercent/100.0))*(rect.getArea()) & (this.getArea()) < (1.0+(areaPercent/100.0))*(rect.getArea())){
      var x1 = (2*this.x + this.width)/2
      var y1 = (2*this.y + this.height)/2
      var x2 = (2*rect.x + rect.width)/2
      var y2 = (2*rect.y + rect.height)/2

      var dist = Math.sqrt( Math.pow(x1-x2,2) + Math.pow(y1-y2,2))

      if(dist < gravCenter_dist) {
        return true
      }
    }
    return false
  }

  override def toString(): String ={
    return s"Rect(${this.x},${this.y},${this.width},${this.height})"
  }


  def isIntersectedBy(rect: Rect): Boolean ={
    if(this.x + this.width < rect.x || (rect.x+rect.width)<(this.x) || this.y + this.height < rect.y || (rect.y+rect.height)<(this.y)){
      return false
    }
    if (this.isLocatedIn(rect) || rect.isLocatedIn(this)){
      return false
    }
    return true
  }

  def concatenateWith(rect: Rect): Rect ={
    return new Rect(Math.min(this.x,rect.x), Math.min(this.y,rect.y), Math.max(this.x+this.width, rect.x+rect.width)-Math.min(this.x,rect.x), Math.max(this.y+this.height, rect.y+rect.height) - Math.min(this.y,rect.y))
  }

  def restrictFrom(rectList: List[Rect]): List[Rect]={
    var res = List[Rect]()

    rectList.foreach{ el =>
      if (el.isLocatedIn(this)){
        res +:= el
      }
    }
    return res
  }


  def enlarge(x: Int, y: Int) : Rect = {
    return new Rect(this.x - x, this.y-y, this.width+2*x, this.height+2*y)
  }


}
