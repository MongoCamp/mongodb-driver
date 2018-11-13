package com.sfxcode.nosql.mongo.relation

trait Relations {

  def relatedRecordForOneToOne[A <: AnyRef](
      relationship: OneToOneRelationship[A],
      value: Any): Option[A] = {
    relationship.relatedRecord(value)
  }

  def relatedRecordForOneToMany[A <: AnyRef](
      relationship: OneToManyRelationship[A],
      value: Any): List[A] = {
    relationship.relatedRecords(value)
  }

}
