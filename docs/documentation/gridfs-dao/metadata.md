# Metadata

Metadata can be updated by the [GridFSDAO](index.md) object.

## Update complete Metadata

UpdateMetadata function will replace the whole metadata for one file.

```scala
val value = Map("index"->11, "category"->"templates")
ImageFilesDAO.updateMetadata(oid, value)
```

## Update Metadata elements

UpdateMetadataElement/s update some part of the metadata by a given filter.


```scala

 val elements =  Map("category"->"logos")
 val filter = Map() // all files
 ImageFilesDAO.updateMetadataElements(filter, elements)
```




