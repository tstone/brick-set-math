package blue.array.brick.brickstock

import blue.array.brick.{ItemCondition, ItemType}
import blue.array.brick.generators._
import org.specs2.mutable.SpecificationLike

import scala.util._


class BSXSetSpec extends SpecificationLike {

  "#conform" should {
    "return a mapping of entries" in {
      val parsedDoc = Seq(
        Success(genBrickStockEntry.generate),
        Success(genBrickStockEntry.generate),
        Failure(new Exception("Didn't work"))
      )

      val (exceptions, conformed) = BSXSet.conform(parsedDoc)

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
        val (_, conformed) = BSXSet.conform(parsedDoc)
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
        val (exceptions, conformed) = BSXSet.conform(parsedDoc)
        val (_, item) = conformed.toSeq.head

        item.quantity mustEqual 6
        exceptions must haveLength(2)
      }
    }
  }

}
