package blue.array

import blue.array.brick.BrickSet.BrickSet
import enumeratum.{Enum, EnumEntry}


package object brick {

  /** "ToParts" refers to the process of taking something that is a collection of parts,
    * e.g. a minifig or assembly, and turning it into the smallest unit available, a part */
  type PartsProvider[A] = String => BrickSet[A]

  sealed trait ItemType extends EnumEntry
  object ItemType extends Enum[ItemType] {
    val values = findValues
    case object Book extends ItemType
    case object Box extends ItemType
    case object Catalog extends ItemType
    case object Gear extends ItemType
    case object Instruction extends ItemType
    case object Minifig extends ItemType
    case object Part extends ItemType
    case object Set extends ItemType
  }

  sealed trait ItemCondition extends EnumEntry
  object ItemCondition extends Enum[ItemCondition] {
    val values = findValues
    case object New extends ItemCondition
    case object Used extends ItemCondition
  }

  case class ItemSignature(id: String, typ: ItemType, condition: ItemCondition) {
    override def toString: String = s"${typ.toString.toLowerCase}/${condition.toString.toLowerCase}/$id"
  }

}
