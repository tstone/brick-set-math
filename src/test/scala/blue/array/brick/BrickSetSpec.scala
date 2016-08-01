package blue.array.brick

import java.io.File

import blue.array.brick.brickstock.BSXSource
import org.specs2.concurrent.ExecutionEnv
import org.specs2.matcher.FutureMatchers
import org.specs2.mutable.SpecificationLike


class BrickSetSpec extends SpecificationLike with FutureMatchers {

  "#fromSource" should {
    "flatten a Source[A] out into a BrickSet[A]" in { implicit ee: ExecutionEnv =>
      val fixture = new File("src/test/resources/fixtures/bsx/example1.bsx")
      val source = BSXSource.fromFile(fixture)

      BrickSet.fromSource(source).map { set =>
        set must contain(ItemSignature("99009", ItemType.Part, ItemCondition.Used))
        set must contain(ItemSignature("3403c01", ItemType.Part, ItemCondition.Used))
      }.await
    }
  }

}
