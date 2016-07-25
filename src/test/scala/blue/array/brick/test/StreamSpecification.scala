package blue.array.brick.test

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Supervision}
import akka.testkit.TestKit
import org.specs2.mutable.SpecificationLike

import scala.concurrent.ExecutionContext


abstract class StreamSpecification extends TestKit(ActorSystem("test")) with SpecificationLike {

  // Alias `Seq` to the immutable version for stream testing
  // which fits much nicer with what Akka Stream expects
  type Seq[+A] = scala.collection.immutable.Seq[A]
  val Seq = scala.collection.immutable.Seq

  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val mat: ActorMaterializer = ActorMaterializer(
    ActorMaterializerSettings(system).withSupervisionStrategy { th: Throwable =>
      println("************* STREAM ERROR *************")
      println(th.getMessage)
      println(th.getStackTraceString)

      Supervision.Resume
    }
  )

}
