package blue.array.brick.brickstock

import java.io.File

import blue.array.brick.{ItemCondition, ItemType}
import blue.array.brick.ItemType._
import blue.array.brick.ItemCondition._

import scala.util.{Failure, Success, Try}
import scala.xml.{Elem, Node, XML}


object BSXReader {

  type ParsedBSXDocument = Seq[Try[BrickStockEntry]]
  case class BSXParsingException(description: String, node: Node) extends Exception(s"$description\n\ton:\n\t$node")

  /*
    <ItemID>2456</ItemID>
    <ItemTypeID>P</ItemTypeID>
    <ColorID>11</ColorID>
    <ItemName>Brick 2 x 6</ItemName>
    <ItemTypeName>Part</ItemTypeName>
    <ColorName>Black</ColorName>
    <CategoryID>5</CategoryID>
    <CategoryName>Brick</CategoryName>
    <Status>I</Status>
    <Qty>2</Qty>
    <Price>0.000</Price>
    <Condition>N</Condition>
    <OrigQty>0</OrigQty>
  */

  def load(file: String): ParsedBSXDocument = load(XML.loadFile(file))
  def load(file: File): ParsedBSXDocument = load(XML.loadFile(file))

  private def typeFromString(value: String): Try[ItemType] = value match {
    case "P" => Success(Part)
    case v   => Failure(new Exception(s"Unknown item type `$v`"))
  }

  private def conditionFromString(value: String): Try[ItemCondition] = value match {
    case "N" => Success(New)
    case "U" => Success(Used)
    case v   => Failure(new Exception(s"Unknown item condition `$v`"))
  }

  private def load(elem: Elem): ParsedBSXDocument = {
    (elem \\ "BrickStockXML" \\ "Inventory" \\ "Item").map { node =>
      val tryElement = for {
        itemType <- typeFromString((node \ "ItemTypeID").text)
        quantity <- Try((node \ "Qty").text.toInt)
        price <- Try(BigDecimal((node \ "Price").text))
        condition <- conditionFromString((node \ "Condition").text)
      } yield BrickStockEntry(
        itemId = (node \ "ItemID").text,
        typ = itemType,
        colorId = (node \ "ColorID").text,
        colorName = (node \ "ColorName").text,
        categoryId = (node \ "CategoryID").text,
        categoryName = (node \ "CategoryName").text,
        quantity = quantity,
        price = price,
        condition = condition
      )

      // Wrap all exceptions in a higher level exception that includes the node that failed
      tryElement match {
        case Success(r) => Success(r)
        case Failure(ex) => Failure(new BSXParsingException(ex.getMessage, node))
      }
    }
  }

}
