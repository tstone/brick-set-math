package blue.array.brick

import enumeratum._

import scala.util._


package object brickstock {

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

    def fromXMLString(value: String): Try[ItemType] = value match {
      case "P" => Success(Part)
      case v   => Failure(new Exception(s"Unknown item type `$v`"))
    }
  }


  sealed trait ItemCondition extends EnumEntry

  object ItemCondition extends Enum[ItemCondition] {
    val values = findValues
    case object New extends ItemCondition
    case object Used extends ItemCondition

    def fromXMLString(value: String): Try[ItemCondition] = value match {
      case "N" => Success(New)
      case "U" => Success(Used)
      case v   => Failure(new Exception(s"Unknown item condition `$v`"))
    }
  }

  case class BrickStockEntry(
    itemId: String,
    typ: ItemType,
    colorId: String,
    colorName: String,
    categoryId: String,
    categoryName: String,
    quantity: Int,
    price: BigDecimal,
    condition: ItemCondition
  )

}
