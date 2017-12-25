import java.io.{File, FileInputStream, FileOutputStream, IOException}
import java.nio.file.Paths
import java.util.zip.{ZipEntry, ZipInputStream}

/**
  * Created by anquegi on 04/06/15.
  */
object Unzip1 {

  def unZipIt(zipFile: String, outputFolder: String): Unit = {
    import java.io.{FileOutputStream, InputStream}
    import java.nio.file.Path
    import java.util.zip.ZipInputStream

    def unzip(zipFile: InputStream, destination: Path): Unit = {
      val zis = new ZipInputStream(zipFile)

      Stream.continually(zis.getNextEntry).takeWhile(_ != null).foreach { file =>
        if (!file.isDirectory) {
          val outPath = destination.resolve(file.getName)
          val outPathParent = outPath.getParent
          if (!outPathParent.toFile.exists()) {
            outPathParent.toFile.mkdirs()
          }

          val outFile = outPath.toFile
          val out = new FileOutputStream(outFile)
          val buffer = new Array[Byte](4096)
          Stream.continually(zis.read(buffer)).takeWhile(_ != -1).foreach(out.write(buffer, 0, _))
        }
      }
    }
    val is = new FileInputStream(zipFile)
    val pth:Path = Paths.get(outputFolder)
    unzip(is,pth)
  }

  //Unzip1.unZipIt(INPUT_ZIP_FILE, OUTPUT_FOLDER)

}