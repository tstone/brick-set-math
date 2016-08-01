package blue.array.brick

import akka.stream.Materializer
import akka.stream.scaladsl.Source

import scala.concurrent.Future
import scala.language.higherKinds


object BrickSet {

  type BrickSet[A] = Map[ItemSignature, A]
  type BrickSetRow[A] = (ItemSignature, A)

  type BrickSetFold[A,B] = (BrickSet[A], A) => BrickSet[A]
  implicit class BrickSetFoldOps[A,B](fold: BrickSetFold[A,B]) {
    def +(other: BrickSetFold[A,B]) =
      (acc: BrickSet[A], row: A) => fold(acc, row).fold(other)
  }


  def empty[A : ItemEntry] = Map.empty[ItemSignature, A]

  def fromSource[A : ItemEntry](a: Source[A, Any])(implicit mat: Materializer): Future[BrickSet[A]] = {
    val entry = implicitly[ItemEntry[A]]
    a.runFold(empty) { case (acc, row) =>
      // check if there's dupes
      acc + (entry.signature(row) -> row)
    }
  }

  def flattenToParts[A : ItemEntry, B : ItemEntry](set: BrickSet[A], provider: PartsProvider[B]): BrickSet[B] = {
    val entry = implicitly[ItemEntry[A]]
    set.foldLeft(empty[B]) { case (acc, (sig, row)) =>
      provider(sig.id)
    }
  }



  def relativeComplement[A : PartEntry, B : PartEntry](a: BrickSet[A], b: BrickSet[B]): BrickSet[A] = {
    val op = relativeComplementOp[A, B](b)
    a.foldLeft(empty[A]) { case (acc, (_, row)) => op(acc, row) }
  }

  def relativeComplement[A : PartEntry, B : PartEntry](a: Source[A, Any], b: BrickSet[B]) =
    a.fold(empty[A])(relativeComplementOp[A,B](b))



  private def foldOp[A,B,Entry[_]](fold: (Entry[A], Entry[B], BrickSet[A], A) => BrickSet[A])(implicit entryA: Entry[A], entryB: Entry[B]): BrickSetFold[A,B] = {
    (acc: BrickSet[A], row: A) => fold(entryA, entryB, acc, row)
  }

  private def mergeOp[A : PartEntry, B : PartEntry](b: BrickSet[B]): BrickSetFold[A,B] =
    foldOp[A,B,PartEntry] { (entryA, entryB, acc, rowA) =>
      val key = entryA.signature(rowA)
      b.get(key) match {
        case Some(rowB) => acc + (key -> entryA.applyQuantity(rowA, _ + entryB.quantity(rowB)))
        case None => acc + (key -> rowA)
      }
    }

  private def relativeComplementOp[A : PartEntry, B : PartEntry](b: BrickSet[B]): BrickSetFold[A,B] = {
    foldOp[A,B,PartEntry] { (entryA, entryB, acc, rowA) =>
      val key = entryA.signature(rowA)
      b.get(key) match {
        // B has the item and enough quantity
        case Some(rowB) if entryB.quantity(rowB) >= entryA.quantity(rowA) => acc
        // B has the item but not enough
        case Some(rowB) => acc + (key -> entryA.applyQuantity(rowA, _ - entryB.quantity(rowB)))
        // B has none of the item
        case None => acc + (key -> rowA)
      }
    }

}
