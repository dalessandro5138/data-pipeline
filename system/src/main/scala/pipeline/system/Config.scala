package pipeline.system
import scala.io.Source
import scala.util.Try
import java.util.Properties

object Config {

  private def loadPropsfromFile(path: String): Try[Properties] = Try {
    val f = Source.fromFile(path)
    val p = new Properties()
    p.load(f.reader())
    p
  }

  def loadConfig[A](key: String)(f: Properties => Try[A]): Try[A] =
    for {
      propsFile <- Try(System.getenv("-D" + key))
      props     <- loadPropsfromFile(propsFile)
      config    <- f(props)
    } yield config

}
