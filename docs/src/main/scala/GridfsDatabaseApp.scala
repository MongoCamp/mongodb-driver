//import dev.mongocamp.driver.mongodb.gridfs.GridfsDatabaseFunctions
//import dev.mongocamp.driver.mongodb.model.ImageMetadata
//import org.bson.types.ObjectId
//
//object GridfsDatabaseApp extends App with GridfsDatabaseFunctions {
//
//  dropImages
//
//  println(imagesCount)
//  createIndexOnImages("name")
//
//  val id: ObjectId = insertImage(SourcePath + "scala-logo.png", ImageMetadata("logo1"))
//
//  println(id)
//
//  val file = findImage(id.toString)
//  println(file)
//
//  val imageFile = findImage(file)
//
//  println(imageFile.getChunkSize)
//  println(imageFile.getMetadata.get("indexSet"))
//
//  downloadImage(imageFile, TargetPath + file.getFilename)
//
//  insertImage(SourcePath + "scala-logo.jpg", ImageMetadata("logo2", indexSet = Set(5, 6, 7)))
//  println(imagesCount)
//
//  val logos = findImages("group", "logos")
//
//  println(logos)
//
//}
