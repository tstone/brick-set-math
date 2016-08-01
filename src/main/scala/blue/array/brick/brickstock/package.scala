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

  object BrickStockEntry {
    implicit val ItemEntryOfBrickStockEntry: ItemEntry[BrickStockEntry] =
      new ItemEntry[BrickStockEntry] {
        override def price(a: BrickStockEntry): BigDecimal = a.price
        override def categoryId(a: BrickStockEntry): String = a.categoryId
        override def colorId(a: BrickStockEntry): String = a.colorId
        override def colorName(a: BrickStockEntry): String = a.colorName
        override def applyQuantity(a: BrickStockEntry, f: (Int) => Int): BrickStockEntry = a.copy(quantity = f(a.quantity))
        override def condition(a: BrickStockEntry): ItemCondition = a.condition
        override def itemId(a: BrickStockEntry): String = a.itemId
        override def categoryName(a: BrickStockEntry): String = a.categoryName
        override def quantity(a: BrickStockEntry): Int = a.quantity
        override def typ(a: BrickStockEntry): ItemType = a.typ
        override def signature(a: BrickStockEntry): ItemSignature = a.signature
      }
  }

}
