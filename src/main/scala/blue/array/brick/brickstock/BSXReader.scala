package blue.array.brick.brickstock

import java.io.File

import scala.util.Try
import scala.xml.{Elem, XML}


object BSXReader {

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

  def load(file: String): Seq[Try[BrickStockEntry]] = load(XML.loadFile(file))
  def load(file: File): Seq[Try[BrickStockEntry]] = load(XML.loadFile(file))

  private def load(elem: Elem): Seq[Try[BrickStockEntry]] = {
    (elem \\ "BrickStockXML" \\ "Inventory" \\ "Item").map { node =>
      for {
        itemType <- ItemType.fromXMLString((node \ "ItemTypeID").text)
        quantity <- Try((node \ "Qty").text.toInt)
        price <- Try(BigDecimal((node \ "Price").text))
        condition <- ItemCondition.fromXMLString((node \ "Condition").text)
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
    }
  }

}
