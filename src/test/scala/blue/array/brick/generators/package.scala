package blue.array.brick

import org.scalacheck.Gen


package object generators {

  implicit class genOps[A](gen: Gen[A]) {
    implicit def generate: A = gen.sample.getOrElse(gen.generate)
  }

  lazy val genItemId: Gen[String] = for {
    baseId <- Gen.choose(100, 10000)
    combination <- Gen.option(Gen.choose(10, 1000))
  } yield baseId + combination.map("c" + _.toString).getOrElse("")

  lazy val genItemEntry: Gen[ItemEntry] =
    Gen.oneOf(genSimplePartEntry, genComplexPartEntry)

  lazy val genSimplePartEntry: Gen[SimplePartEntry] = for {
    itemId <- genItemId
    colorId <- Gen.choose(1, 200).map(_.toString)
    colorName <- Gen.oneOf("White", "Trans-Neon Green", "Dark Blue")
    categoryId <- Gen.choose(1, 300).map(_.toString)
    categoryName <- Gen.oneOf("Castle", "Pirates", "Agents")
    quantity <- Gen.oneOf(1, 150)
    price <- Gen.oneOf(BigDecimal(0.00), BigDecimal(20.00))
    condition <- Gen.oneOf(ItemCondition.values)
  } yield SimplePartEntry(
    itemId = itemId,
    colorId = colorId,
    colorName = colorName,
    categoryId = categoryId,
    categoryName = categoryName,
    quantity = quantity,
    price = price,
    condition = condition
  )

  lazy val genComplexPartEntry: Gen[ComplexPartEntry] = for {
    itemId <- genItemId
    colorId <- Gen.choose(1, 200).map(_.toString)
    colorName <- Gen.oneOf("White", "Trans-Neon Green", "Dark Blue")
    categoryId <- Gen.choose(1, 300).map(_.toString)
    categoryName <- Gen.oneOf("Castle", "Pirates", "Agents")
    quantity <- Gen.oneOf(1, 150)
    price <- Gen.oneOf(BigDecimal(0.00), BigDecimal(20.00))
    condition <- Gen.oneOf(ItemCondition.values)
  } yield ComplexPartEntry(
    itemId = itemId,
    colorId = colorId,
    colorName = colorName,
    categoryId = categoryId,
    categoryName = categoryName,
    quantity = quantity,
    price = price,
    condition = condition
  )

}
