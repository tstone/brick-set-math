package blue.array.brick


package object brickstock {

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
  ) {
    lazy val signature = ItemSignature(itemId, typ, condition)
  }

}
