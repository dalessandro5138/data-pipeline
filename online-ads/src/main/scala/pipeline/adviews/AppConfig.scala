package pipeline.adviews

import java.nio.file.{ Path, Paths }
import java.util.Properties
import cats.MonadError
import cats.implicits._

case class AppConfig(sourceFiles: List[Path], reportDestination: Path)

object AppConfig {
  def fromProps[F[_]](props: Properties)(implicit ME: MonadError[F, Throwable]): F[AppConfig] =
    for {
      srcs <- ME.catchNonFatal(props.getProperty("sourcefiles").split(",").toList.map(Paths.get(_)))
      dest <- ME.catchNonFatal(Paths.get(props.getProperty("report.destination")))
    } yield AppConfig(srcs, dest)
}
