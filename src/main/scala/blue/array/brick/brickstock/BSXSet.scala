package blue.array.brick.brickstock

import blue.array.brick.ItemSignature
import blue.array.brick.brickstock.BSXReader.ParsedBSXDocument

import scala.util.{Failure, Success}


object BSXSet {

  case class ConformationException(description: String) extends Exception(description)

  // Take a raw sequence of brickstock entries and conform them to a unique
  // mapping of item signatures
  def conform(parsedDoc: ParsedBSXDocument): (Seq[Throwable], BSXDocument) = {
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

}
