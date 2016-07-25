package blue.array

import enumeratum.{Enum, EnumEntry}


package object brick {

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
