package dev.mongocamp.driver.mongodb.utils
import better.files.File
import java.net.URI
import java.net.URL

object FileUtils {

  def getFileByUrl(url: URL): File = {
    if (url.getProtocol == "file") {
      File(url)
    }
    else {
      val tempFile = File.newTemporaryFile("mongodb-driver-import", ".json")
      tempFile.deleteOnExit()
      val inputStream = url.openStream()
      try
        tempFile.writeBytes(inputStream.readAllBytes().iterator)
      finally
        inputStream.close()
    }
  }

  def getFileByUrl(uri: URI): File = getFileByUrl(uri.toURL)

}
