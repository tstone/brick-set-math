package blue.array.brick


trait PartEntry[A] {
  def itemId(a: A): String
  def colorId(a: A): String
  def colorName(a: A): String
  def categoryId(a: A): String
  def categoryName(a: A): String
  def quantity(a: A): Int
  def price(a: A): BigDecimal
  def condition(a: A): ItemCondition
  def applyQuantity(a: A, f: Int => Int): A

  def signature(a: A): ItemSignature = ItemSignature(itemId(a), ItemType.Part, condition(a))
}

object PartEntry {
  // Any time we have a `PartEntry` we can also have an `ItemEntry`
  implicit def PartEntryOfItemEntry[A : PartEntry]: ItemEntry[A] = new ItemEntry[A] {
    val part = implicitly[PartEntry[A]]
    override def itemId(a: A): String = part.itemId(a)
    override def typ(a: A): ItemType = ItemType.Part
    override def colorId(a: A): String = part.colorId(a)
    override def colorName(a: A): String = part.colorName(a)
    override def categoryId(a: A): String = part.categoryId(a)
    override def categoryName(a: A): String = part.categoryName(a)
    override def quantity(a: A): Int = part.quantity(a)
    override def price(a: A): BigDecimal = part.price(a)
    override def condition(a: A): ItemCondition = part.condition(a)
    override def applyQuantity(a: A, f: (Int) => Int): A = part.applyQuantity(a, f)
  }
}