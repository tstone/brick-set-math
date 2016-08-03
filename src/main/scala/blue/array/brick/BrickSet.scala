package blue.array.brick

import akka.stream.Materializer
import akka.stream.scaladsl.Source

import scala.concurrent.{ExecutionContext, Future}


object BrickSet {

  type BrickSet[E] = Map[ItemSignature, E]
  type BrickSetRow[E] = (ItemSignature, E)

  def empty[E <: ItemEntry] = Map.empty[ItemSignature, E]

  def fromSource[E <: ItemEntry](a: Source[E, Any])(implicit mat: Materializer): Future[BrickSet[E]] = {
    a.runFold(empty[E]) { case (acc, entry) => mergeOp(acc, entry) }
  }

  def fromSeq[E <: ItemEntry](a: Seq[E]): BrickSet[E] = {
    a.foldLeft(empty[E]) { case (acc, entry) => mergeOp(acc, entry) }
  }

  def union[E <: ItemEntry](a: BrickSet[E], b: BrickSet[E]): BrickSet[E] = {
    a.foldLeft(empty[E]) { case (acc, (k, entry)) => mergeOp(acc, entry) }
  }

  def union[E <: ItemEntry](sets: Iterable[BrickSet[E]]): BrickSet[E] = {
    sets.foldLeft(empty[E]) { case (acc, e) => union(acc, e) }
  }

  def flatten(set: BrickSet[ItemEntry], provider: PartsProvider)(implicit ec: ExecutionContext): Future[BrickSet[SimplePartEntry]] = {
    Future.traverse(set.values)(flatten(_: ItemEntry, provider)).map { entries =>
      union(entries)
    }
  }

  def flatten(entry: ItemEntry, provider: PartsProvider)(implicit ec: ExecutionContext): Future[BrickSet[SimplePartEntry]] = {
    entry match {
      case simple: SimplePartEntry => Future.successful(fromSeq(Seq(simple)))
      case assembly =>
        provider(assembly.uniqueIdentifier).flatMap { entries =>
          Future.traverse(entries)(flatten(_: ItemEntry, provider)).map(es => union(es))
        }
    }
  }

  def relativeComplement(a: BrickSet[SimplePartEntry], b: BrickSet[SimplePartEntry]): BrickSet[SimplePartEntry] = {
    a.foldLeft(empty[SimplePartEntry]) { case (acc, (_, entry)) => relativeComplementOp(acc, entry, b) }
  }

  def relativeComplement[E <: ItemEntry](a: Source[E, Any], b: BrickSet[SimplePartEntry], provider: PartsProvider)
                                        (implicit ec: ExecutionContext, mat: Materializer): Future[BrickSet[SimplePartEntry]] = {
    // Because the Source can't be proven to be of all SimplePartEntry
    // AND because the Source can't be proven to not contain duplicates
    // in addition to folding over the source and flattening out the parts,
    // the results must be union'ed together
    a
      .flatMapConcat { entry =>
        Source.fromFuture(flatten(entry, provider).map { parts =>
          relativeComplement(parts, b)
        })
      }
      .runFold(empty[SimplePartEntry]) { case (acc, complement) =>
        union(acc, complement)
      }
  }

  private def mergeOp[E <: ItemEntry, S <: E#Self =:= E](acc: BrickSet[E], entry: E): BrickSet[E] = {
    acc.get(entry.uniqueIdentifier) match {
      case Some(dupe) =>
        // Weird type problem here in that we can't convince the compiler in advance
        // that E =:= E#Self.  This match is a weakly typed hack-around...
        dupe.addQuantity(entry.quantity) match {
          case e: E => acc + (entry.uniqueIdentifier -> e)
          case _ => ???
        }
      case None => acc + (entry.uniqueIdentifier -> entry)
    }
  }

  private def relativeComplementOp(acc: BrickSet[SimplePartEntry], entryA: SimplePartEntry, b: BrickSet[SimplePartEntry]) = {
    val key = entryA.uniqueIdentifier
    b.get(key) match {
      // B has the item and enough quantity
      case Some(entryB) if entryB.quantity >= entryA.quantity => acc
      // B has the item but not enough
      case Some(entryB) => acc + (key -> entryA.updateQuantity(entryA.quantity - entryB.quantity))
      // B has none of the item
      case None => acc + (key -> entryA)
    }
  }

}
