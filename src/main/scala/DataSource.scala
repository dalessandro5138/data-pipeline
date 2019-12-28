import java.nio.file.Path
import java.util.UUID
import scala.io.Source
import scala.util.{ Failure, Success, Try }

trait DataSource[A] {
  def streamAllData: Try[Stream[A]]
}

object DataSource {

  def fileDataSource(files: List[Path]): DataSource[UserAdView] = new DataSource[UserAdView] {

    object DataHeaders {

      val GUID    = "GUID"
      val AD_ID   = "Ad_ID"
      val SITE_ID = "Site_ID"

      val DATA_HEADERS = Seq(
        "Campaign_ID",
        "GUID",
        "Timestamp",
        "Ad_ID",
        "Site_ID",
        "Site_URL",
        "Ad_Type",
        "Tag_Type",
        "Placement_ID",
        "Wild_Card",
        "Custom_1",
        "Custom_2",
        "Custom_3",
        "Custom_4",
        "Custom_5",
        "Custom_6",
        "Custom_7",
        "Custom_8",
        "Custom_9",
        "Custom_10",
        "Pass",
        "Opt_Out",
        "Iframe",
        "Weight",
        "Size",
        "Tactic",
        "Visible",
        "Exposure_Time",
        "Pre_Ad_Exposure",
        "Post_Ad_Exposure",
        "Campaign_Imp_Count",
        "Referrer",
        "User_Agent",
        "City",
        "State",
        "Country",
        "Zip_Code",
        "Language",
        "IP_Address",
        "guidsource",
        "Ad_Server_Cd"
      )

    }

    private def sequence[T](l: List[Option[T]]): Try[List[T]] =
      if (l.exists(_.isEmpty)) Failure(new Exception("error loading file(s)")) else Success(l.flatten)

    private def streamFiles(files: List[Path]): Try[Stream[String]] =
      for {
        fs    <- sequence(files.map(f => Try(Source.fromFile(f.toUri)).toOption))
        lines <- Success(fs.flatMap(_.getLines))
      } yield lines.toStream

    private def isValidLine: String => Boolean = s => !s.startsWith("#")

    private def parseRow(s: String): Option[UserAdView] = {
      import DataHeaders.{ AD_ID, GUID, SITE_ID }
      val table = (DataHeaders.DATA_HEADERS zip s.split("\t")).toMap

      for {
        guid   <- table.get(GUID)
        uuid   <- Try(UUID.fromString(guid)).toOption
        adId   <- table.get(AD_ID)
        siteId <- table.get(SITE_ID)
      } yield UserAdView(uuid, adId, siteId)
    }

    def streamAllData: Try[Stream[UserAdView]] =
      streamFiles(files)
        .flatMap(
          s =>
            Success(
              s.filter(isValidLine)
                .map(parseRow)
                .collect { case Some(a) => a }
            )
        )
  }
}
