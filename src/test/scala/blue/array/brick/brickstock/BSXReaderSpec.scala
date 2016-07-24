package blue.array.brick.brickstock

import org.specs2.mutable.SpecificationLike

import java.io.File


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

}
