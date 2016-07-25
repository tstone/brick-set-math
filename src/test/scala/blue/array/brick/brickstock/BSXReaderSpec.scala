package blue.array.brick.brickstock

import org.specs2.mutable.SpecificationLike
import java.io.File

import blue.array.brick.generators._
import blue.array.brick.{ItemCondition, ItemType}

import scala.util.{Failure, Success}


class BSXReaderSpec extends SpecificationLike {

  "#load" should {
    "read a simple BSX file" in {
      val fixture = new File("src/test/resources/fixtures/bsx/example1.bsx")
      val entries = BSXReader.load(fixture)

      entries must haveLength(2)

      val first = entries.head.get
      val second = entries.last.get

      first.itemId mustEqual "99009"
      first.typ mustEqual ItemType.Part
      first.colorId mustEqual "86"
      first.colorName mustEqual "Light Bluish Gray"
      first.categoryId mustEqual "36"
      first.categoryName mustEqual "Technic"
      first.quantity mustEqual 3
      first.price mustEqual BigDecimal(0.055d)

      second.itemId mustEqual "3403c01"
      second.condition mustEqual ItemCondition.New
    }
  }

  
  "#conform" should {
    "return a mapping of entries" in {
      val parsedDoc = Seq(
        Success(genBrickStockEntry.generate),
        Success(genBrickStockEntry.generate),
        Failure(new Exception("Didn't work"))
      )

      val (exceptions, conformed) = BSXReader.conform(parsedDoc)

      exceptions must haveLength(1)
      conformed must haveLength(2)
    }

    "when the price matches" >> {
      "combine the quantity of items that are the same" in {
        val parsedDoc = Seq(
          Success(genBrickStockEntry.generate.copy(itemId = "4444", price = BigDecimal(0.010), quantity = 1, typ = ItemType.Part, condition = ItemCondition.New)),
          Success(genBrickStockEntry.generate.copy(itemId = "4444", price = BigDecimal(0.010), quantity = 2, typ = ItemType.Part, condition = ItemCondition.New)),
          Success(genBrickStockEntry.generate.copy(itemId = "4444", price = BigDecimal(0.010), quantity = 3, typ = ItemType.Part, condition = ItemCondition.New))
        )
        val (_, conformed) = BSXReader.conform(parsedDoc)
        val (_, item) = conformed.toSeq.head

        item.quantity mustEqual 6
      }
    }

    "when the price matches" >> {
      "combine the quantity of items that are the same but add an exception about the price" in {
        val parsedDoc = Seq(
          Success(genBrickStockEntry.generate.copy(itemId = "4444", price = BigDecimal(0.010), quantity = 1, typ = ItemType.Part, condition = ItemCondition.New)),
          Success(genBrickStockEntry.generate.copy(itemId = "4444", price = BigDecimal(0.020), quantity = 2, typ = ItemType.Part, condition = ItemCondition.New)),
          Success(genBrickStockEntry.generate.copy(itemId = "4444", price = BigDecimal(0.030), quantity = 3, typ = ItemType.Part, condition = ItemCondition.New))
        )
        val (exceptions, conformed) = BSXReader.conform(parsedDoc)
        val (_, item) = conformed.toSeq.head

        item.quantity mustEqual 6
        exceptions must haveLength(2)
      }
    }
  }

}
