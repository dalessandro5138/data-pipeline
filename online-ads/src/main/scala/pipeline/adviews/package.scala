package pipeline

import java.util.UUID
import pipeline.system.{ Parser, StringFormatter }
import scala.util.Try
import cats.effect.ExitCase.Completed
import cats.effect.{ Bracket, ExitCase }
import zio.Task

package object adviews {

  val REPORT_HEADERS = Seq("Ad Id", "Site Id", "Frequency", "Total users that viewed ad this frequently")

  implicit val userAdViewParser: Parser[UserAdView] = new Parser[UserAdView] {
    import UserAdView.{ AD_ID, GUID, SITE_ID }

    override def parseRow(s: String): Option[UserAdView] =
      parseFromTable[UserAdView](DataHeaders.DATA_HEADERS) { table =>
        for {
          guid   <- table.get(GUID)
          uuid   <- Try(UUID.fromString(guid)).toOption
          adId   <- table.get(AD_ID)
          siteId <- table.get(SITE_ID)
        } yield UserAdView(uuid, adId, siteId)
      }(s)
  }

  implicit val adViewFreqStringFormatter: StringFormatter[(AdViewFrequency, BigInt)] =
    new StringFormatter[(AdViewFrequency, BigInt)] {
      override def tokenize(
        a: (AdViewFrequency, BigInt)
      ): Seq[String] = {
        val (avf, i) = a
        Seq(avf.adId.toString, avf.siteId.toString, avf.frequency.toString, i.toString)
      }
    }

  implicit val reportOrdering: Ordering[(AdViewFrequency, BigInt)] =
    Ordering.by[(AdViewFrequency, BigInt), BigInt] { case (freq, i) => -freq.frequency }

  implicit val taskBracket: Bracket[Task, Throwable] = new Bracket[Task, Throwable] {
    override def bracketCase[A, B](acquire: Task[A])(use: A => Task[B])(
      release: (A, ExitCase[Throwable]) => Task[Unit]
    ): Task[B]                                                                     = acquire.bracket(a => release(a, Completed).orDie)(use)
    override def raiseError[A](e: Throwable): Task[A]                              = Task.fail(e)
    override def handleErrorWith[A](fa: Task[A])(f: Throwable => Task[A]): Task[A] = fa.absorb
    override def pure[A](x: A): Task[A]                                            = Task(x)
    override def flatMap[A, B](fa: Task[A])(f: A => Task[B]): Task[B]              = fa.flatMap(f)
    override def tailRecM[A, B](a: A)(f: A => Task[Either[A, B]]): Task[B] =
      f(a) flatMap {
        case Right(b) => Task.succeed(b)
        case Left(a)  => tailRecM(a)(f)
      }
  }

}
