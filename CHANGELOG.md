## [v3.0.9] - 2025-09-24
### :wrench: Chores
- [`e6d7da6`](https://github.com/MongoCamp/mongodb-driver/commit/e6d7da634dde8fa86f43867fd7f93c6c5215ceef) - update library dependencies to latest versions *(commit by [@QuadStingray](https://github.com/QuadStingray))*


## [v3.0.7] - 2025-09-05
### :recycle: Refactors
- [`0b657ae`](https://github.com/MongoCamp/mongodb-driver/commit/0b657aef3efc26981991c0ee951859448fca97d9) - add customizable error logging in JSON conversion *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`33503ab`](https://github.com/MongoCamp/mongodb-driver/commit/33503abd3cccb1d4ff96e7e9fcc80b23e0a46063) - add overload for `toObject` method in `JsonConverter` to binary compatible *(commit by [@QuadStingray](https://github.com/QuadStingray))*


## [v3.0.8] - 2025-09-04
### :recycle: Refactors
- [`ff4b750`](https://github.com/MongoCamp/mongodb-driver/commit/ff4b75048a65cf43b41ab31b9283d6cfda5b5c04) - improve `JsonConverter` and `CirceSchema` methods *(commit by [@QuadStingray](https://github.com/QuadStingray))*


## [v3.0.3] - 2025-03-31
### :recycle: Refactors
- [`28dc13e`](https://github.com/MongoCamp/mongodb-driver/commit/28dc13e220e344aa258f31ac131bc323916404e9) - package object json extends AutoDerivation to not need double import *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`271d212`](https://github.com/MongoCamp/mongodb-driver/commit/271d21281195c39b1f32075d57c5debc94a1683b) - package object json extends AutoDerivation to not need double import *(commit by [@QuadStingray](https://github.com/QuadStingray))*


## [v3.0.2] - 2025-03-30
### :wrench: Chores
- [`76dcbff`](https://github.com/MongoCamp/mongodb-driver/commit/76dcbffb58ae8cb3333a59628860f4e26c392850) - 8 dependency updates for mongodb-driver *(commit by [@QuadStingray](https://github.com/QuadStingray))*


## [v3.0.1] - 2025-03-21
### :bug: Bug Fixes
- [`fe480df`](https://github.com/MongoCamp/mongodb-driver/commit/fe480df58cd74d17e8591a023244ed90390bf083) - jdbc access to functional named fields *(commit by [@QuadStingray](https://github.com/QuadStingray))*

### :white_check_mark: Tests
- [`20f1a47`](https://github.com/MongoCamp/mongodb-driver/commit/20f1a4742b70fd28fccea4c04ee40c094e21bab4) - added `OR` and `AND` with negation test for lucene query *(commit by [@QuadStingray](https://github.com/QuadStingray))*


## [v3.0.0] - 2025-02-07
### :sparkles: New Features
- [`968c4b5`](https://github.com/MongoCamp/mongodb-driver/commit/968c4b5dfb93dd562bef51987bce278ab6eea4cd) - try to implement conversion from document to object with circe *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`b1f5d78`](https://github.com/MongoCamp/mongodb-driver/commit/b1f5d789d50d74b30eaf00bd8692973557ac1a14) - use circe scheme to convert documents to objects *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`22f09e8`](https://github.com/MongoCamp/mongodb-driver/commit/22f09e8b2944cb0b5ac0b50c5005f92d05923300) - added skip to find method *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`73b7e40`](https://github.com/MongoCamp/mongodb-driver/commit/73b7e4009699244f462ede31fe2ce8d11b0bd641) - more converter for net.sf.jsqlparser expressions *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`986aa14`](https://github.com/MongoCamp/mongodb-driver/commit/986aa140ba6a266697a09d63dbca9aac2c467634) - Migration Plugin needed *(commit by [@QuadStingray](https://github.com/QuadStingray))*

### :bug: Bug Fixes
- [`76d4bc6`](https://github.com/MongoCamp/mongodb-driver/commit/76d4bc6221c0b68c690d8467507b2be3c3d46bda) - Field order in JDBC Driver closes[#72](https://github.com/MongoCamp/mongodb-driver/pull/72) *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`3d3b60a`](https://github.com/MongoCamp/mongodb-driver/commit/3d3b60ab5fc1976b9209eec245763d11aa0e7ef7) - lucene query some cases not negates closes[#71](https://github.com/MongoCamp/mongodb-driver/pull/71) *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`d1ce373`](https://github.com/MongoCamp/mongodb-driver/commit/d1ce3739a5dd9ce6bc0ff683eca85494e1ce9148) - scala 2 compatibility *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`b4982df`](https://github.com/MongoCamp/mongodb-driver/commit/b4982df67cdbd2cc0703f6aac2d30a77c6959443) - scala 2 compatibility *(commit by [@QuadStingray](https://github.com/QuadStingray))*

### :recycle: Refactors
- [`319babd`](https://github.com/MongoCamp/mongodb-driver/commit/319babd7d74eef89d1a2846e12ab77a0c76efc90) - use sbt-scala3-migrate plugin to optimize code *(commit by [@sfxcode](https://github.com/sfxcode))*
- [`3caf2e6`](https://github.com/MongoCamp/mongodb-driver/commit/3caf2e6a91b0742239d0ebde4187be89240159ec) - remove compilation errors for source *(commit by [@sfxcode](https://github.com/sfxcode))*
- [`ec72741`](https://github.com/MongoCamp/mongodb-driver/commit/ec72741285c76d506e40190dd698a37d62faf2b6) - first try to refactor DAO Pattern *(commit by [@sfxcode](https://github.com/sfxcode))*
- [`1e90836`](https://github.com/MongoCamp/mongodb-driver/commit/1e90836e5541bf3539781e148428a878a178831a) - return scala BigDecimal *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`d2760e2`](https://github.com/MongoCamp/mongodb-driver/commit/d2760e25901702b65f3e3aa7d23583c53c2e67e9) - collection only can use document *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`7bc6716`](https://github.com/MongoCamp/mongodb-driver/commit/7bc6716462cb56e5d7f74406eeecd455846ae042) - remove not longer needed bson codecs *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`7b95ad0`](https://github.com/MongoCamp/mongodb-driver/commit/7b95ad0ef25a3d03a28fa050d0436941b050c9db) - remove duplicate conversion to circe.JSON *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`bc44de2`](https://github.com/MongoCamp/mongodb-driver/commit/bc44de26c16f8ad34642b108b184adcd3ae66435) - moved json conversion to own package *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`6104bd4`](https://github.com/MongoCamp/mongodb-driver/commit/6104bd499b42755d6e2cb58d974f793a2b1d4d93) - code cleanup on jdbc package *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`b73cf96`](https://github.com/MongoCamp/mongodb-driver/commit/b73cf96a55c9788543ca9cf927eb2fe81d2a6469) - Remove not working methode updateValue on document *(commit by [@QuadStingray](https://github.com/QuadStingray))*

### :white_check_mark: Tests
- [`7107c59`](https://github.com/MongoCamp/mongodb-driver/commit/7107c5949e428e35499219736325359d296e3701) - revert all test *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`05cd13e`](https://github.com/MongoCamp/mongodb-driver/commit/05cd13e5d6c797c00cbb7c00bb3a506412d4e0f2) - try to fix ci test problem *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`9cd33ce`](https://github.com/MongoCamp/mongodb-driver/commit/9cd33cebdc7e2aeff338f0d9397a08d2dd9cf6e1) - changed all test from spec2 to munit *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`5f60784`](https://github.com/MongoCamp/mongodb-driver/commit/5f6078476908a1e36e72c8c0016a24f66183b4fe) - drop database before checking liquibase *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`6bdd907`](https://github.com/MongoCamp/mongodb-driver/commit/6bdd907d04afce28f83f79d5a9fc8770c1490446) - drop database before SchemaSuite *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`2a32bad`](https://github.com/MongoCamp/mongodb-driver/commit/2a32bad5ecbe716103a212657cc606e9c4c496ff) - added more tests for jdbc package *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`c047fbb`](https://github.com/MongoCamp/mongodb-driver/commit/c047fbb0fd12209da5ef08327de13aca6f7cba6c) - added more tests for jdbc package *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`8d28c81`](https://github.com/MongoCamp/mongodb-driver/commit/8d28c81c3651a3c5837824ce1a57054d358bb42c) - added more tests for jdbc package *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`37a07f4`](https://github.com/MongoCamp/mongodb-driver/commit/37a07f405376a5da2af3bca6639d423777a0aa62) - fix to have space between key and value *(commit by [@QuadStingray](https://github.com/QuadStingray))*

### :wrench: Chores
- [`91ed0be`](https://github.com/MongoCamp/mongodb-driver/commit/91ed0be2adce3fe38ea180f74b74278153c62601) - 9 dependency updates for mongodb-driver *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`60e1350`](https://github.com/MongoCamp/mongodb-driver/commit/60e1350cbdbc3f6abb1f8e304ad3e57db356d843) - plugin updates *(commit by [@QuadStingray](https://github.com/QuadStingray))*


## [v2.8.1] - 2024-11-05
### :sparkles: New Features
- [`df5a348`](https://github.com/MongoCamp/mongodb-driver/commit/df5a348515cf60f7ff419efde4e619a5013b2fab) - joins without on statement in oracle style *(commit by [@QuadStingray](https://github.com/QuadStingray))*

### :bug: Bug Fixes
- [`0e64c07`](https://github.com/MongoCamp/mongodb-driver/commit/0e64c07a7cae391e90bc18b757abc5a2e4e4cb34) - use double to read number values from document *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`23fae6e`](https://github.com/MongoCamp/mongodb-driver/commit/23fae6e36b0d2a5f9f92b333942730dd9a215892) - added bytes read und write from and to mongodb *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`90f867b`](https://github.com/MongoCamp/mongodb-driver/commit/90f867b8eb34a34dd44530fa7ce33242ed43c538) - parameter replacement needs to keep order *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`242ee8e`](https://github.com/MongoCamp/mongodb-driver/commit/242ee8eaa570c59db110fe9364b3bb5306be6b78) - executeUpdate response sum from matched, delete and inserted count *(commit by [@QuadStingray](https://github.com/QuadStingray))*

### :recycle: Refactors
- [`43ee50b`](https://github.com/MongoCamp/mongodb-driver/commit/43ee50b5f197ee2488636f323c7a1e18036be83e) - conversion is now scala 2.12.x conform *(commit by [@QuadStingray](https://github.com/QuadStingray))*

### :wrench: Chores
- [`323dc0a`](https://github.com/MongoCamp/mongodb-driver/commit/323dc0a1d265f5df8da5489d724d8881b545221f) - 3 dependency updates for mongodb-driver *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`5a12245`](https://github.com/MongoCamp/mongodb-driver/commit/5a122458ed2959f7fad029e49e7abd8fad0a0e0d) - 3 plugins and sbt versions updated *(commit by [@QuadStingray](https://github.com/QuadStingray))*


## [v2.8.0] - 2024-10-22
### :boom: BREAKING CHANGES
- due to [`51aa927`](https://github.com/MongoCamp/mongodb-driver/commit/51aa92708c5aa5585c1925b6c5d97f5adb9bcbd7) - drop java 11 and 19 support because `org.apache.lucene.queryparser` has dropped it *(commit by [@QuadStingray](https://github.com/QuadStingray))*:

  Lucene Query Parser has dropped Java 11 and 19 and only support Java 21 Support


### :sparkles: New Features
- [`9e73ce1`](https://github.com/MongoCamp/mongodb-driver/commit/9e73ce18a4c4fe5b4354da51962365db303f63cf) - support lucene query converter support search with long value on string field *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`c6fe36b`](https://github.com/MongoCamp/mongodb-driver/commit/c6fe36b5c41e356d9540b9e85d30ca61f50dc9aa) - **jdbc**: start implementing jdbc driver for mongodb *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`36a4307`](https://github.com/MongoCamp/mongodb-driver/commit/36a43071d15a0b5ad9dc4a26c94fe06be215265f) - sql converter with execution *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`0408c7e`](https://github.com/MongoCamp/mongodb-driver/commit/0408c7e2cd9d581775697f7bc30cf1856c2d938a) - implemented jdbc driver methods *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`0a7a7de`](https://github.com/MongoCamp/mongodb-driver/commit/0a7a7de71071c4f6eaeda16fd6528b0fdf578df0) - added show databases and show tables implementation *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`cf5aa46`](https://github.com/MongoCamp/mongodb-driver/commit/cf5aa462ab64554bf96aaf3380451841436c1971) - execute sql with database selection *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`8e24215`](https://github.com/MongoCamp/mongodb-driver/commit/8e242155af024145d0cb7c378f051a9149bb336e) - detect schema *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`2ecc276`](https://github.com/MongoCamp/mongodb-driver/commit/2ecc2769539fc111a24dd8da5a7f1fe99c52a016) - implemented jdbc driver methods *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`8a39da8`](https://github.com/MongoCamp/mongodb-driver/commit/8a39da85d512f133ad861e979e038a75961b81c6) - compatibility for scala 2.12 *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`022cc63`](https://github.com/MongoCamp/mongodb-driver/commit/022cc63ed9fa5eaae628c2502b25c140bda7705e) - refactor for jsqlparser 5.0 *(commit by [@QuadStingray](https://github.com/QuadStingray))*

### :bug: Bug Fixes
- [`d2f9727`](https://github.com/MongoCamp/mongodb-driver/commit/d2f9727f78d74409ab2a2ea0d84df87b96bdc4d0) - fix not compiling test for schema *(commit by [@QuadStingray](https://github.com/QuadStingray))*

### :recycle: Refactors
- [`0c8861a`](https://github.com/MongoCamp/mongodb-driver/commit/0c8861a677416d972240d6ac30c6635ab73d1333) - added serverAddressList as connection option *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`51aa927`](https://github.com/MongoCamp/mongodb-driver/commit/51aa92708c5aa5585c1925b6c5d97f5adb9bcbd7) - **java**: drop java 11 and 19 support because `org.apache.lucene.queryparser` has dropped it *(commit by [@QuadStingray](https://github.com/QuadStingray))*

### :white_check_mark: Tests
- [`08e247f`](https://github.com/MongoCamp/mongodb-driver/commit/08e247f60108a1c868309b965f6e35b75ad23f98) - some errors on rerun tests *(commit by [@QuadStingray](https://github.com/QuadStingray))*

### :wrench: Chores
- [`4d18469`](https://github.com/MongoCamp/mongodb-driver/commit/4d1846930ba430da4872c7d41b3b8e4bf8283d62) - Dependency Update and changes for mongo-scala-driver 5.0.0 *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`094f50d`](https://github.com/MongoCamp/mongodb-driver/commit/094f50d4afb2cbf9774c18c4a0fb998000c62221) - plugins updated 5 dependency updates for project *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`5f5c1ab`](https://github.com/MongoCamp/mongodb-driver/commit/5f5c1ab1ca04359cdac37dbc1c6f5a879c4347be) - 7 dependency updates for mongodb-driver *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`3948d48`](https://github.com/MongoCamp/mongodb-driver/commit/3948d48029f9389e7b13f0e9961657ef9f4d1fb0) - 13 dependency updates for mongodb-driver *(commit by [@QuadStingray](https://github.com/QuadStingray))*


## [v2.7.0] - 2024-02-24
### :wrench: Chores
- [`0970eec`](https://github.com/MongoCamp/mongodb-driver/commit/0970eec2d2e99439cd0d9c864a20b193e0e58df2) - Prepare next 2.7.x versions *(commit by [@sfxcode](https://github.com/sfxcode))*


## [v2.6.9] - 2024-02-24
### :wrench: Chores
- [`f7b3fbc`](https://github.com/MongoCamp/mongodb-driver/commit/f7b3fbc319ad1918a1f214a1769c96b4277473de) - **Dependencies**: use last scala versions *(commit by [@sfxcode](https://github.com/sfxcode))*


## [v2.6.8] - 2023-11-21
### :recycle: Refactors
- [`292355c`](https://github.com/MongoCamp/mongodb-driver/commit/292355c1ca47fb8e79f98aadd01018eecf0e4451) - Use MaxWait also Pagination and other calls *(commit by [@QuadStingray](https://github.com/QuadStingray))*

### :wrench: Chores
- [`63c3b46`](https://github.com/MongoCamp/mongodb-driver/commit/63c3b466c1fafdf52eef0475027f883bd4387d9e) - dependency updates *(commit by [@QuadStingray](https://github.com/QuadStingray))*


## [v2.6.7] - 2023-11-04
### :white_check_mark: Tests
- [`bdd695b`](https://github.com/MongoCamp/mongodb-driver/commit/bdd695b956e30450be7a478539e4b5c3bf5b6852) - Added Java 21 to test *(commit by [@QuadStingray](https://github.com/QuadStingray))*

### :wrench: Chores
- [`a679168`](https://github.com/MongoCamp/mongodb-driver/commit/a6791682e8d7cf780a46a8d42ef7170122be4bd3) - fix build pipeline *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`89484d4`](https://github.com/MongoCamp/mongodb-driver/commit/89484d41c613cbc65dd61320460d1da8125bb4ef) - update sbt to 1.9.7 *(commit by [@QuadStingray](https://github.com/QuadStingray))*


## [v2.6.5] - 2023-10-22
### :recycle: Refactors
- [`96203b8`](https://github.com/MongoCamp/mongodb-driver/commit/96203b88658b167bd30a3e006ca8a2aab89d0432) - GraalVm native configuration *(commit by [@QuadStingray](https://github.com/QuadStingray))*

### :wrench: Chores
- [`331b556`](https://github.com/MongoCamp/mongodb-driver/commit/331b5567d50a01b3c6093ebbd76334c4b5f929d8) - Found 11 dependency updates for mongodb-driver *(commit by [@QuadStingray](https://github.com/QuadStingray))*


## [2.6.4]() (2023-07-29)


### Maintenance

* **Dependencies:** mongo scala driver 4.10.2 ([faa22b0](https://github.com/MongoCamp/mongodb-driver/commit/faa22b041f84b290b72eae509ed6993ed571926e))

## [2.6.3]() (2023-05-12)


### Bug Fixes

* paginated aggregation with empty response ([0acf10c](https://github.com/MongoCamp/mongodb-driver/commit/0acf10cc1089da7f7f9411dfc44972609c3cab8e))


### Maintenance

* dependency update `lucene-queryparser` 9.5.0 -> 9.6.0 ([a71a599](https://github.com/MongoCamp/mongodb-driver/commit/a71a599071ae889a5df5f3b5ab5c254c7b29e0bf))

## [2.6.2]() (2023-04-29)


### Bug Fixes

* deploy_ghpages.sh wrong cname ([42bdf99](https://github.com/MongoCamp/mongodb-driver/commit/42bdf994c25940e717662d2b3671acd53fa6c797))


### Maintenance

* **Dependencies:** mongo scala driver 4.9.1 ([c1795d8](https://github.com/MongoCamp/mongodb-driver/commit/c1795d8374941ebc7ae4b91d12dd092e1651e4ef))

## [2.6.1]() (2023-04-01)


### Bug Fixes

* provided removed and docs fixed ([c18592d](https://github.com/MongoCamp/mongodb-driver/commit/c18592da3a53a5bf6098df03e6198c44334262c4))

## [2.6.0]() (2023-04-01)


### Bug Fixes

* compatibility for scala 2.12 in LuceneQueryConverter ([fdb1d35](https://github.com/MongoCamp/mongodb-driver/commit/fdb1d35e71c217b7b7cdaf25871e75e71a656b6c))
* pagination aggregation should always use the RAW-DAO ([0bf5dc9](https://github.com/MongoCamp/mongodb-driver/commit/0bf5dc9e52b3e281d6b3f2617d679db786c5a82c))


### Code Refactoring

* cleanup plugins ([5f3134d](https://github.com/MongoCamp/mongodb-driver/commit/5f3134da4d62ecd56335bbcc664e89c0f0137812))
* DocumentIncludes and MongoImplicits to own files ([753a633](https://github.com/MongoCamp/mongodb-driver/commit/753a63381dea71a92fa696aaa28f849580069af6))
* foreach moved to pagination trait ([ecbb62f](https://github.com/MongoCamp/mongodb-driver/commit/ecbb62f4a4a1f8b6fd1f4c1ee82dd56099567d79))
* foreach moved to pagination trait ([54dd250](https://github.com/MongoCamp/mongodb-driver/commit/54dd25072f7fb4091400a600129299e68908589c))


### Features

* Compact Method for complete user scope ([e567be7](https://github.com/MongoCamp/mongodb-driver/commit/e567be7fe2666d85ef8b96f98868a6f4844a6fe0))
* Compact Methods for Database or DAO ([0926b1d](https://github.com/MongoCamp/mongodb-driver/commit/0926b1da0ba3d80f0be1bb996fc18ff18a9db535))
* Foreach in Pagination Result ([1fe1d8b](https://github.com/MongoCamp/mongodb-driver/commit/1fe1d8ba31f34e13df2afc3d1c3c9ae1f6b034ea))
* Implicit for LuceneQuery to BsonDocument ([32f1029](https://github.com/MongoCamp/mongodb-driver/commit/32f10294baa8ef73bff2e26247f07e4639340ff3))
* Lucene Query can converted to Bson ([e56e5d4](https://github.com/MongoCamp/mongodb-driver/commit/e56e5d416535b21a4d2c92932db82b488a4321af))
* Pagination for MongoDb Search and Aggregation ([52abbe3](https://github.com/MongoCamp/mongodb-driver/commit/52abbe3f8b34a434e88b889b9f19a595037936ff))


### Maintenance

* **dependencies:** Found 7 dependency updates ([89a0e27](https://github.com/MongoCamp/mongodb-driver/commit/89a0e272072b8f9dfeefb920627a048091e08ca1))
* **plugins:** Found 5 dependency updates ([cc13a06](https://github.com/MongoCamp/mongodb-driver/commit/cc13a06bdb495cfa2518b44d21058928b32fac6a))


### Reverts

* sonatype removed by accident ([e320ed1](https://github.com/MongoCamp/mongodb-driver/commit/e320ed1eef7bb9ffad5234743f882f65ee1bf3dc))

## [2.5.4]() (2023-02-12)


### Maintenance

* **Dependencies:** mongo scala driver 4.9.0 ([356c7c4](https://github.com/MongoCamp/mongodb-driver/commit/356c7c42291ca7d3f7a7ad69a60dfac3493dbc9f))

## [2.5.3]() (2022-12-04)


### Maintenance

* **Dependencies:** mongo scala driver 4.8.0 ([2a3af87](https://github.com/MongoCamp/mongodb-driver/commit/2a3af879df3229fe44aca7dd92c6faefccdfa118))

## [2.5.1]() (2022-10-11)


### Maintenance

* **Dependencies:** mongo scala driver 4.7.2 ([f050d78](https://github.com/MongoCamp/mongodb-driver/commit/f050d78ac16e6cfbbf52c067a3eacd8091965bbb))

## [2.5.0]() (2022-07-27)


### Maintenance

* **Dependencies:** mongo scala driver 4.7.0 ([9e37be8](https://github.com/MongoCamp/mongodb-driver/commit/9e37be88878bfb27d1ba092a45fbb19c63e431e6))

## [2.4.9]() (2022-07-13)


### Bug Fixes

* recursive BsonConverter.asMap for List of Documents ([3c7d0c8](https://github.com/MongoCamp/mongodb-driver/commit/3c7d0c87096f0342936d38dd0e2fbd0c0bf64785))
* recursive BsonConverter.asMap for List of Documents ([901fb0d](https://github.com/MongoCamp/mongodb-driver/commit/901fb0d6670feb31697cd25b7b5eb326b2ac5695))


### Maintenance

* 2 dependency updates ([00d3202](https://github.com/MongoCamp/mongodb-driver/commit/00d3202c8d5449d54ce4d1d3b0f4a3b8ad28f045))

## [2.4.8]() (2022-06-29)

## [2.4.7]() (2022-06-27)


### Bug Fixes

* **Database:** Database Size ([c35778c](https://github.com/MongoCamp/mongodb-driver/commit/c35778c8e3cff94d0759a62c9400342e154c9c9d))


### Code Refactoring

* **Project:** moved to github mongocamp organization ([bb65f8b](https://github.com/MongoCamp/mongodb-driver/commit/bb65f8b87e6c77bd5976a1828595366e3e3953d2))


### Features

* Added MongoDb Listeners to MongoConfig (Command and Connection Pool) ([bbe77de](https://github.com/MongoCamp/mongodb-driver/commit/bbe77de2e7151ac3c00a38f38c3ed29c1da06388))
* installed pgp key from secret ([b15f232](https://github.com/MongoCamp/mongodb-driver/commit/b15f23295e831b498aec0b132dedcdbbcfd2d05b))


### Maintenance

* 7 dependency updates ([3e844ad](https://github.com/MongoCamp/mongodb-driver/commit/3e844ad65aeada7abc30cad8fa92e98a3882be59))
* **Dependencies:** mongo scala driver 4.6.1 ([6397659](https://github.com/MongoCamp/mongodb-driver/commit/63976595197ed9a75932803429347dd25666af86))
* **dependencies:** prepare release ([676cb29](https://github.com/MongoCamp/mongodb-driver/commit/676cb2964c5f11d42e1b08315e7fa538a7f5cf11))
* **dependencies:** update ([4f52595](https://github.com/MongoCamp/mongodb-driver/commit/4f52595ec2d99430669b0b032914f55f83c14abd))
* **dependencies:** update ([e3e073e](https://github.com/MongoCamp/mongodb-driver/commit/e3e073e28c497627df0d5da62235862f5ef600bc))
* **dependencies:** update ([cf7d00e](https://github.com/MongoCamp/mongodb-driver/commit/cf7d00e4991f5d6f453dc73ea8b05d9451a329c0))
* **dependencies:** update ([0fd5bb7](https://github.com/MongoCamp/mongodb-driver/commit/0fd5bb71aedacda535538c579891cff79efc27c9))
* Dependency Updates ([9e4658c](https://github.com/MongoCamp/mongodb-driver/commit/9e4658c210a1bbd869b5ff294e89349951eb1e23))
* Dependency Updates ([3f749c0](https://github.com/MongoCamp/mongodb-driver/commit/3f749c05300b23eaf48588966aec7a926f3c4f1c))
* **deps:** mongo scala driver 4.5.0 ([c547da7](https://github.com/MongoCamp/mongodb-driver/commit/c547da7bb78e60013ead92c814d5ef4646c5e76b))
* **deps:** mongo scala driver 4.5.1 ([208eb54](https://github.com/MongoCamp/mongodb-driver/commit/208eb54b58c616e19d515df18cfbf27bcdbe3b0a))
* **plugins:** Dependency Updates ([50c44a2](https://github.com/MongoCamp/mongodb-driver/commit/50c44a255a53721ea7dad36336b959290cb6c4e7))
* **version:** prepare next release version ([d01f042](https://github.com/MongoCamp/mongodb-driver/commit/d01f042043ac745320b01316f1493565acdcf2b2))
* **version:** prepare next release version ([0396f10](https://github.com/MongoCamp/mongodb-driver/commit/0396f1058a589836e09addf3a3648891929ecded))
* **version:** prepare next release version ([54c87c4](https://github.com/MongoCamp/mongodb-driver/commit/54c87c4f25ca2f77757f352df44cb6f4aca68314))
* **version:** prepare next release version ([5aa71df](https://github.com/MongoCamp/mongodb-driver/commit/5aa71df7fc3e875d48f950787c93024020a6d3e0))
* **version:** prepare next release version ([637f262](https://github.com/MongoCamp/mongodb-driver/commit/637f2629256785e754047e605f47cb5abc148f8d))


[v2.6.5]: https://github.com/MongoCamp/mongodb-driver/compare/v2.6.4...v2.6.5
[v2.6.7]: https://github.com/MongoCamp/mongodb-driver/compare/v2.6.6...v2.6.7
[v2.6.8]: https://github.com/MongoCamp/mongodb-driver/compare/v2.6.7...v2.6.8
[v2.6.9]: https://github.com/MongoCamp/mongodb-driver/compare/v2.6.8...v2.6.9
[v2.7.0]: https://github.com/MongoCamp/mongodb-driver/compare/v2.6.10...v2.7.0
[v2.8.0]: https://github.com/MongoCamp/mongodb-driver/compare/v2.7.0...v2.8.0
[v2.8.1]: https://github.com/MongoCamp/mongodb-driver/compare/v2.8.0...v2.8.1
[v3.0.0]: https://github.com/MongoCamp/mongodb-driver/compare/v2.8.1...v3.0.0
[v3.0.1]: https://github.com/MongoCamp/mongodb-driver/compare/v3.0.0...v3.0.1
[v3.0.2]: https://github.com/MongoCamp/mongodb-driver/compare/v3.0.1...v3.0.2
[v3.0.3]: https://github.com/MongoCamp/mongodb-driver/compare/v3.0.2...v3.0.3
[v3.0.8]: https://github.com/MongoCamp/mongodb-driver/compare/v3.0.7...v3.0.8
[v3.0.7]: https://github.com/MongoCamp/mongodb-driver/compare/v3.0.6...v3.0.7
[v3.0.9]: https://github.com/MongoCamp/mongodb-driver/compare/v3.0.8...v3.0.9
