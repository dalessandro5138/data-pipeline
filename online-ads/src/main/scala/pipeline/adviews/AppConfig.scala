package pipeline.adviews
import java.nio.file.{ Path, Paths }
import java.util.Properties
import scala.util.Try

case class AppConfig(sourceFiles: List[Path], reportDestination: Path)

object AppConfig {
  def fromProps: Properties => Try[AppConfig] =
    p =>
      for {
        srcs <- Try(p.getProperty("sourcefiles").split(",").toList.map(Paths.get(_)))
        dest <- Try(Paths.get(p.getProperty("report.destination")))
      } yield AppConfig(srcs, dest)
}
