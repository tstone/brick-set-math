package blue.array.brick.brickstock

import java.io.File

import blue.array.brick.BrickSet.BrickSet
import blue.array.brick._
import blue.array.brick.ItemCondition._

import scala.util.{Failure, Success, Try}
import scala.xml.{Elem, Node, XML}


/**
  * BSXReader
  * A non-lazy reader which takes an XML file, read the whole thing, then returns a Seq.
  * This is useful in cases where simplicity is preferred and the BSX files are small.
  */
object BSXReader {

  type ParsedBSXDocument = Seq[Try[ItemEntry]]

  case class BSXParsingException(description: String, node: Node) extends Exception(s"$description\n\ton:\n\t$node")
  case class ConformationException(description: String) extends Exception(description)

  // Take a raw sequence of entries and conform them to a unique
  // mapping of item uniqueIdentifiers
  def conform(parsedDoc: ParsedBSXDocument): (Seq[Throwable], BrickSet[ItemEntry]) = {
    val zero = (List.empty[Throwable], BrickSet.empty[ItemEntry])

    parsedDoc.foldLeft(zero) {
      case ((exceptions, doc), Failure(ex)) =>
        (ex :: exceptions, doc)

      case ((exceptions, doc), Success(entry)) =>
        doc.get(entry.uniqueIdentifier) match {
          // Merge quantity if item already exists.  Ambiguous: what if the price is different?
          case Some(existing) if existing.price != entry.price =>
            val conflatedEntry = existing.addQuantity(entry.quantity)
            (ConformationException(s"Found duplicate entries with differing prices for item ${entry.itemId}. Keeping price ${existing.price}") :: exceptions,
              doc + (existing.uniqueIdentifier -> conflatedEntry))
          case Some(existing) =>
            val conflatedEntry = existing.addQuantity(entry.quantity)
            (exceptions, doc + (existing.uniqueIdentifier -> conflatedEntry))
          case None =>
            (exceptions, doc + (entry.uniqueIdentifier -> entry))
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

  private def constructorFromString(value: String, itemId: String): Try[ItemEntry.Constructor] =
    // As best I can tell, complex parts or assemblies have a "c" in their ID, like "3403c01"
    (value, itemId.contains("c")) match {
      case ("P", false) => Success(SimplePartEntry.apply)
      case ("P", true) => Success(ComplexPartEntry.apply)
      case (v, _)   => Failure(new Exception(s"Unknown item type `$v`"))
    }

  private def conditionFromString(value: String): Try[ItemCondition] = value match {
    case "N" => Success(New)
    case "U" => Success(Used)
    case v   => Failure(new Exception(s"Unknown item condition `$v`"))
  }

  def load(document: Elem): ParsedBSXDocument =
    (document \\ "BrickStockXML" \\ "Inventory" \\ "Item").map(loadNode)

  def loadNode(node: Node): Try[ItemEntry] = {
    val itemId = (node \ "ItemID").text
    val tryElement = for {
      constructor <- constructorFromString((node \ "ItemTypeID").text, itemId)
      quantity <- Try((node \ "Qty").text.toInt)
      price <- Try(BigDecimal((node \ "Price").text))
      condition <- conditionFromString((node \ "Condition").text)
    } yield {
      val colorId = (node \ "ColorID").text
      val colorName = (node \ "ColorName").text
      val categoryId = (node \ "CategoryID").text
      val categoryName = (node \ "CategoryName").text

      constructor(itemId, colorId, colorName, categoryId, categoryName, quantity, price, condition)
    }

    // Wrap all exceptions in a higher level exception that includes the node that failed
    tryElement match {
      case Success(r) => Success(r)
      case Failure(ex) => Failure(BSXParsingException(ex.getMessage, node))
    }
  }

}
