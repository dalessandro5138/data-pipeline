package pipeline.system

import scala.io.Source
import java.util.Properties
import cats.MonadError
import cats.implicits._

object Config {

  private def loadPropsfromFile[F[_]](path: String)(implicit ME: MonadError[F, Throwable]): F[Properties] =
    ME.catchNonFatal {
      val f = Source.fromFile(path)
      val p = new Properties()
      p.load(f.reader())
      p
    }

  def loadConfig[F[_], A](key: String)(f: Properties => F[A])(implicit ME: MonadError[F, Throwable]): F[A] =
    for {
      propsFile <- ME.catchNonFatal(System.getenv("-D" + key))
      props     <- loadPropsfromFile[F](propsFile)
      config    <- f(props)
    } yield config

}
