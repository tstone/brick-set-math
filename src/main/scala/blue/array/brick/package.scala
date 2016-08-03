package blue.array

import enumeratum.{Enum, EnumEntry}

import scala.concurrent.Future


package object brick {

  /** "ToParts" refers to the process of taking something that is a collection of parts,
    * e.g. a minifig or assembly, and turning it into the smallest unit available, a part */
  type PartsProvider = ItemSignature => Future[Seq[ItemEntry]]

  sealed trait ItemCondition extends EnumEntry
  object ItemCondition extends Enum[ItemCondition] {
    val values = findValues
    case object New extends ItemCondition
    case object Used extends ItemCondition
  }

  case class ItemSignature(id: String, typ: String, condition: ItemCondition) {
    override def toString: String = s"${typ.toLowerCase}/${condition.toString.toLowerCase}/$id"
  }

}
