package com.sfxcode.nosql.mongo.model

case class CodecTest(id: Long = 1,
                     bd: BigDecimal = BigDecimal(BigDecimal.getClass.getSimpleName.length.toDouble),
                     bi: BigInt = BigInt(BigInt.getClass.getSimpleName.length))
