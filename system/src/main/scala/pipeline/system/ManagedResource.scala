package pipeline.system

import java.io.{ BufferedWriter, FileWriter }
import java.nio.file.Path
import cats.MonadError
import scala.io.{ BufferedSource, Source }
import cats.effect.Resource
import cats.implicits._

object ManagedResource {
  def bufferedWriter[F[_]](dest: Path)(implicit ME: MonadError[F, Throwable]): Resource[F, BufferedWriter] =
    Resource(ME.catchNonFatal {
      val destFile = dest.toFile
      if (destFile.exists) destFile.delete();
      val bw = new BufferedWriter(new FileWriter(destFile))
      (bw, ME.point { bw.flush(); bw.close() })
    })

  def bufferedSource[F[_]](
    sourceFiles: List[Path]
  )(implicit ME: MonadError[F, Throwable]): Resource[F, List[BufferedSource]] = {

    val acquire = (sourceFiles
      .map(f => ME.catchNonFatal(Source.fromFile(f.toFile)))
      .sequence)
      .map(as => (as, ME.point(as.foreach(_.close))))

    Resource(acquire)
  }
}
