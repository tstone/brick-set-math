package blue.array.brick.brickstock

import java.io.File

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.stream.actor.ActorPublisher
import akka.stream.actor.ActorPublisherMessage.Request
import akka.stream.scaladsl._
import blue.array.brick._

import scala.annotation.tailrec
import scala.io.{Source => FileSource}
import scala.util.{Failure, Success, Try}
import scala.xml.XML


object BSXSource {

  def fromFile(file: File)(implicit system: ActorSystem): Source[ItemEntry, ActorRef] =
    Source.actorPublisher(Props(new BSXSourceActor(file)))


  private class BSXSourceActor(file: File) extends ActorPublisher[ItemEntry] {

    lazy val fileIter = FileSource.fromFile(file).iter

    override def postStop = onCompleteThenStop()

    def receive = {
      case Request(count) =>
        // `find` in this case is finding the end of the file.
        // false = don't stop, true = at end
        (1l to count).find { _ =>
          readOne match {
            case Some(Success(entry)) => onNext(entry); false
            case Some(Failure(ex)) => false // Silently dropping read errors right now !!
            case None => context stop self; true
          }
        }
    }

    def readOne: Option[Try[ItemEntry]] = {
      // Since XML isn't really a line-by-line format, read each character into a buffer
      // until have both an <Item> and a </Item>.
      for {
        head <- bufferFor("<Item>")
        tail <- bufferFor("</Item>")
      } yield {
        // Drop everything to the left of <Item>
        val cleanHead = head.substring(head.indexOf("<Item>"))
        val xml = XML.loadString(cleanHead + tail)
        BSXReader.loadNode(xml)
      }
    }

    @tailrec
    private def bufferFor(target: String, buffer: String = ""): Option[String] = {
      if (!fileIter.hasNext) None
      else {
        val incr = buffer + fileIter.next()
        if (incr.contains(target)) Some(incr)
        else bufferFor(target, incr)
      }
    }
  }


}
