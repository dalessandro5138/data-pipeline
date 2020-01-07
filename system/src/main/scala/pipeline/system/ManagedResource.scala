package pipeline.system

import java.io.{ BufferedWriter, FileWriter }
import java.nio.file.Path
import scala.io.{ BufferedSource, Source }
import cats.effect.{ Bracket, Resource }
import cats.implicits._

object ManagedResource {
  def bufferedWriter[F[_]](dest: Path)(implicit B: Bracket[F, Throwable]): Resource[F, BufferedWriter] =
    Resource(B.catchNonFatal {
      val destFile = dest.toFile
      if (destFile.exists) destFile.delete();
      val bw = new BufferedWriter(new FileWriter(destFile))
      (bw, B.point { bw.flush(); bw.close() })
    })

  def bufferedSource[F[_]](
    sourceFiles: List[Path]
  )(implicit B: Bracket[F, Throwable]): Resource[F, List[BufferedSource]] = {

    val acquire = (sourceFiles
      .map(f => B.catchNonFatal(Source.fromFile(f.toFile)))
      .sequence)
      .map(as => (as, B.point(as.foreach(_.close))))

    Resource(acquire)
  }
}
