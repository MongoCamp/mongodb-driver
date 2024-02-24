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