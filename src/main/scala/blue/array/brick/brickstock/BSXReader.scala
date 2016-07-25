package blue.array.brick.brickstock

import java.io.File

import blue.array.brick.{ItemSignature, _}
import blue.array.brick.ItemType._
import blue.array.brick.ItemCondition._

import scala.util.{Failure, Success, Try}
import scala.xml.{Elem, Node, XML}


/**
  * BSXReader
  * A non-lazy reader which takes an XML file, read the whole thing, then returns a Seq.
  * This is useful in cases where simplicity is preferred and the BSX files are small.
  */
object BSXReader {

  type ParsedBSXDocument = Seq[Try[BrickStockEntry]]

  case class BSXParsingException(description: String, node: Node) extends Exception(s"$description\n\ton:\n\t$node")
  case class ConformationException(description: String) extends Exception(description)

  // Take a raw sequence of entries and conform them to a unique
  // mapping of item signatures
  def conform(parsedDoc: ParsedBSXDocument): (Seq[Throwable], Map[ItemSignature, BrickStockEntry]) = {
    val zero = (List.empty[Throwable], Map.empty[ItemSignature, BrickStockEntry])

    parsedDoc.foldLeft(zero) {
      case ((exceptions, doc), Failure(ex)) =>
        (ex :: exceptions, doc)

      case ((exceptions, doc), Success(entry)) =>
        doc.get(entry.signature) match {
          // Merge quantity if item already exists.  Ambiguous: what if the price is different?
          case Some(existing) if existing.price != entry.price =>
            val conflatedEntry = existing.copy(quantity = existing.quantity + entry.quantity)
            (ConformationException(s"Found duplicate entries with differing prices for item ${entry.itemId}. Keeping price ${existing.price}") :: exceptions,
              doc + (existing.signature -> conflatedEntry))
          case Some(existing) =>
            val conflatedEntry = existing.copy(quantity = existing.quantity + entry.quantity)
            (exceptions, doc + (existing.signature -> conflatedEntry))
          case None =>
            (exceptions, doc + (entry.signature -> entry))
        }
    }
  }

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
        case Failure(ex) => Failure(BSXParsingException(ex.getMessage, node))
      }
    }
  }

}
