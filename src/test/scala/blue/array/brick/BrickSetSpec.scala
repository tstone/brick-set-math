package blue.array.brick

import java.io.File

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Materializer}
import akka.testkit.TestKit
import blue.array.brick.brickstock.BSXSource
import org.specs2.concurrent.ExecutionEnv
import org.specs2.matcher.FutureMatchers
import org.specs2.mutable.SpecificationLike


class BrickSetSpec extends TestKit(ActorSystem("test-system")) with SpecificationLike with FutureMatchers {

  implicit val mat: Materializer = ActorMaterializer(ActorMaterializerSettings(system))


  "#fromSource" should {
    "flatten a Source[A] out into a BrickSet[A]" in { implicit ee: ExecutionEnv =>
      val fixture = new File("src/test/resources/fixtures/bsx/example1.bsx")
      val source = BSXSource.fromFile(fixture)

      BrickSet.fromSource(source).map { set =>
        println(set)
        set.contains(ItemSignature("99009", "simple-part", ItemCondition.Used)) must beTrue
        set.contains(ItemSignature("3403c01", "complex-part", ItemCondition.New)) must beTrue
      }.await
    }
  }

}
