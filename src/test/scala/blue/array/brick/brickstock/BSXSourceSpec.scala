package blue.array.brick.brickstock

import java.io.File

import blue.array.brick.test.StreamSpecification
import blue.array.brick.{ItemCondition, ItemType}

import scala.concurrent.Await
import scala.concurrent.duration._


class BSXSourceSpec extends StreamSpecification {

  "#fromFile" should {
    "read a small BSX file" in {
      val fixture = new File("src/test/resources/fixtures/bsx/example1.bsx")
      val source = BSXSource.fromFile(fixture)
      val entries = Await.result(source.runFold(Seq.empty[BrickStockEntry])(_ :+ _), 5.seconds)

      entries must haveLength(2)

      val first = entries.head
      val second = entries.last

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

}
