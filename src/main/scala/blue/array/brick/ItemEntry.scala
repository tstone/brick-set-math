package blue.array.brick

trait ItemEntry[A] {
  def itemId(a: A): String
  def typ(a: A): ItemType
  def colorId(a: A): String
  def colorName(a: A): String
  def categoryId(a: A): String
  def categoryName(a: A): String
  def quantity(a: A): Int
  def price(a: A): BigDecimal
  def condition(a: A): ItemCondition

  def signature(a: A): ItemSignature =
    ItemSignature(itemId(a), typ(a), condition(a))
}
