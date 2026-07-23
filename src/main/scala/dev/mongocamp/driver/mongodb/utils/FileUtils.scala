package dev.mongocamp.driver.mongodb.utils
import better.files.File
import java.net.URI
import java.net.URL
import java.nio.file.FileSystem
import java.nio.file.FileSystemNotFoundException
import java.nio.file.FileSystems
import java.nio.file.Path

object FileUtils {

  def getFile(url: URL): File = {
    if (url.getProtocol == "file") {
      File(url)
    }
    else {
      resolveResourcePath(url.toURI)
      val tempFile = File.newTemporaryFile("mongodb-driver-import", ".json")
      tempFile.deleteOnExit()
      val inputStream = url.openStream()
      try
        tempFile.writeBytes(inputStream.readAllBytes().iterator)
      finally
        inputStream.close()
    }
  }

  def getFile(uri: URI): File = getFile(uri.toURL)

  def getFile(string: String): File = getFile(new URI(string))

  def getFile(path: Path): File = getFile(path.toString)

  def resolveResourcePath(uri: URI): (Path, Option[FileSystem]) = {
    if (uri.getScheme == "jar") {
      val fs =
        try
          FileSystems.getFileSystem(uri)
        catch {
          case _: FileSystemNotFoundException =>
            FileSystems.newFileSystem(uri, java.util.Collections.emptyMap[String, Any]())
        }
      (fs.getPath(uri.getSchemeSpecificPart.substring(uri.getSchemeSpecificPart.indexOf('!') + 1)), Some(fs))
    }
    else {
      (Path.of(uri), None)
    }
  }
}
