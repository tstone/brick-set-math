package blue.array.brick


trait ItemEntry {
  type Self <: ItemEntry

  def itemId: String
  def colorId: String
  def colorName: String
  def categoryId: String
  def categoryName: String
  def quantity: Int
  def price: BigDecimal
  def condition: ItemCondition
  def uniqueIdentifier: ItemSignature

  def updateQuantity(value: Int): Self
  def addQuantity(value: Int): Self = updateQuantity(quantity + value)
}

object ItemEntry {
  type Constructor = (String, String, String, String, String, Int, BigDecimal, ItemCondition) => ItemEntry
}

case class SimplePartEntry(itemId: String, colorId: String, colorName: String, categoryId: String, categoryName: String,
                     quantity: Int, price: BigDecimal, condition: ItemCondition) extends ItemEntry {

  type Self = SimplePartEntry
  lazy val uniqueIdentifier: ItemSignature = ItemSignature(itemId, "simple-part", condition)
  override def updateQuantity(value: Int) = this.copy(quantity = value)
}

case class ComplexPartEntry(itemId: String, colorId: String, colorName: String, categoryId: String, categoryName: String,
                     quantity: Int, price: BigDecimal, condition: ItemCondition) extends ItemEntry {

  type Self = ComplexPartEntry
  lazy val uniqueIdentifier: ItemSignature = ItemSignature(itemId, "complex-part", condition)
  override def updateQuantity(value: Int): ComplexPartEntry = this.copy(quantity = value)
}

case class RetailSet(itemId: String, colorId: String, colorName: String, categoryId: String, categoryName: String,
                     quantity: Int, price: BigDecimal, condition: ItemCondition) extends ItemEntry {

  type Self = RetailSet
  lazy val uniqueIdentifier: ItemSignature = ItemSignature(itemId, "set", condition)
  override def updateQuantity(value: Int): RetailSet = this.copy(quantity = value)
}

case class Book(itemId: String, colorId: String, colorName: String, categoryId: String, categoryName: String,
                     quantity: Int, price: BigDecimal, condition: ItemCondition) extends ItemEntry {

  type Self = Book
  lazy val uniqueIdentifier: ItemSignature = ItemSignature(itemId, "book", condition)
  override def updateQuantity(value: Int): Book = this.copy(quantity = value)
}

case class Catalog(itemId: String, colorId: String, colorName: String, categoryId: String, categoryName: String,
                     quantity: Int, price: BigDecimal, condition: ItemCondition) extends ItemEntry {

  type Self = Catalog
  lazy val uniqueIdentifier: ItemSignature = ItemSignature(itemId, "catalog", condition)
  override def updateQuantity(value: Int): Catalog = this.copy(quantity = value)
}

case class Gear(itemId: String, colorId: String, colorName: String, categoryId: String, categoryName: String,
                     quantity: Int, price: BigDecimal, condition: ItemCondition) extends ItemEntry {

  type Self = Gear
  lazy val uniqueIdentifier: ItemSignature = ItemSignature(itemId, "gear", condition)
  override def updateQuantity(value: Int): Gear = this.copy(quantity = value)
}

case class Box(itemId: String, colorId: String, colorName: String, categoryId: String, categoryName: String,
                     quantity: Int, price: BigDecimal, condition: ItemCondition) extends ItemEntry {

  type Self = Box
  lazy val uniqueIdentifier: ItemSignature = ItemSignature(itemId, "box", condition)
  override def updateQuantity(value: Int): Box = this.copy(quantity = value)
}