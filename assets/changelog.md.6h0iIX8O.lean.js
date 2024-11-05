import{_ as a,c as r,a2 as t,o}from"./chunks/framework.DPuwY6B9.js";const g=JSON.parse('{"title":"This is the Changelog","description":"","frontmatter":{},"headers":[],"relativePath":"changelog.md","filePath":"changelog.md","lastUpdated":1730840042000}'),n={name:"changelog.md"};function i(d,e,c,l,m,b){return o(),r("div",null,e[0]||(e[0]=[t('<h1 id="this-is-the-changelog" tabindex="-1">This is the Changelog <a class="header-anchor" href="#this-is-the-changelog" aria-label="Permalink to &quot;This is the Changelog&quot;">​</a></h1><h2 id="v2-8-1-2024-11-05" tabindex="-1"><a href="https://github.com/MongoCamp/mongodb-driver/compare/v2.8.0...v2.8.1" target="_blank" rel="noreferrer">v2.8.1</a> - 2024-11-05 <a class="header-anchor" href="#v2-8-1-2024-11-05" aria-label="Permalink to &quot;[v2.8.1] - 2024-11-05&quot;">​</a></h2><h3 id="new-features" tabindex="-1">✨ New Features <a class="header-anchor" href="#new-features" aria-label="Permalink to &quot;:sparkles: New Features&quot;">​</a></h3><ul><li><a href="https://github.com/MongoCamp/mongodb-driver/commit/df5a348515cf60f7ff419efde4e619a5013b2fab" target="_blank" rel="noreferrer"><code>df5a348</code></a> - joins without on statement in oracle style <em>(commit by <a href="https://github.com/QuadStingray" target="_blank" rel="noreferrer">@QuadStingray</a>)</em></li></ul><h3 id="bug-fixes" tabindex="-1">🐛 Bug Fixes <a class="header-anchor" href="#bug-fixes" aria-label="Permalink to &quot;:bug: Bug Fixes&quot;">​</a></h3><ul><li><a href="https://github.com/MongoCamp/mongodb-driver/commit/0e64c07a7cae391e90bc18b757abc5a2e4e4cb34" target="_blank" rel="noreferrer"><code>0e64c07</code></a> - use double to read number values from document <em>(commit by <a href="https://github.com/QuadStingray" target="_blank" rel="noreferrer">@QuadStingray</a>)</em></li><li><a href="https://github.com/MongoCamp/mongodb-driver/commit/23fae6e36b0d2a5f9f92b333942730dd9a215892" target="_blank" rel="noreferrer"><code>23fae6e</code></a> - added bytes read und write from and to mongodb <em>(commit by <a href="https://github.com/QuadStingray" target="_blank" rel="noreferrer">@QuadStingray</a>)</em></li><li><a href="https://github.com/MongoCamp/mongodb-driver/commit/90f867b8eb34a34dd44530fa7ce33242ed43c538" target="_blank" rel="noreferrer"><code>90f867b</code></a> - parameter replacement needs to keep order <em>(commit by <a href="https://github.com/QuadStingray" target="_blank" rel="noreferrer">@QuadStingray</a>)</em></li><li><a href="https://github.com/MongoCamp/mongodb-driver/commit/242ee8eaa570c59db110fe9364b3bb5306be6b78" target="_blank" rel="noreferrer"><code>242ee8e</code></a> - executeUpdate response sum from matched, delete and inserted count <em>(commit by <a href="https://github.com/QuadStingray" target="_blank" rel="noreferrer">@QuadStingray</a>)</em></li></ul><h3 id="refactors" tabindex="-1">♻️ Refactors <a class="header-anchor" href="#refactors" aria-label="Permalink to &quot;:recycle: Refactors&quot;">​</a></h3><ul><li><a href="https://github.com/MongoCamp/mongodb-driver/commit/43ee50b5f197ee2488636f323c7a1e18036be83e" target="_blank" rel="noreferrer"><code>43ee50b</code></a> - conversion is now scala 2.12.x conform <em>(commit by <a href="https://github.com/QuadStingray" target="_blank" rel="noreferrer">@QuadStingray</a>)</em></li></ul><h3 id="chores" tabindex="-1">🔧 Chores <a class="header-anchor" href="#chores" aria-label="Permalink to &quot;:wrench: Chores&quot;">​</a></h3><ul><li><a href="https://github.com/MongoCamp/mongodb-driver/commit/323dc0a1d265f5df8da5489d724d8881b545221f" target="_blank" rel="noreferrer"><code>323dc0a</code></a> - 3 dependency updates for mongodb-driver <em>(commit by <a href="https://github.com/QuadStingray" target="_blank" rel="noreferrer">@QuadStingray</a>)</em></li><li><a href="https://github.com/MongoCamp/mongodb-driver/commit/5a122458ed2959f7fad029e49e7abd8fad0a0e0d" target="_blank" rel="noreferrer"><code>5a12245</code></a> - 3 plugins and sbt versions updated <em>(commit by <a href="https://github.com/QuadStingray" target="_blank" rel="noreferrer">@QuadStingray</a>)</em></li></ul><h2 id="v2-8-0-2024-10-22" tabindex="-1"><a href="https://github.com/MongoCamp/mongodb-driver/compare/v2.7.0...v2.8.0" target="_blank" rel="noreferrer">v2.8.0</a> - 2024-10-22 <a class="header-anchor" href="#v2-8-0-2024-10-22" aria-label="Permalink to &quot;[v2.8.0] - 2024-10-22&quot;">​</a></h2><h3 id="breaking-changes" tabindex="-1">💥 BREAKING CHANGES <a class="header-anchor" href="#breaking-changes" aria-label="Permalink to &quot;:boom: BREAKING CHANGES&quot;">​</a></h3><ul><li><p>due to <a href="https://github.com/MongoCamp/mongodb-driver/commit/51aa92708c5aa5585c1925b6c5d97f5adb9bcbd7" target="_blank" rel="noreferrer"><code>51aa927</code></a> - drop java 11 and 19 support because <code>org.apache.lucene.queryparser</code> has dropped it <em>(commit by <a href="https://github.com/QuadStingray" target="_blank" rel="noreferrer">@QuadStingray</a>)</em>:</p><p>Lucene Query Parser has dropped Java 11 and 19 and only support Java 21 Support</p></li></ul><h3 id="new-features-1" tabindex="-1">✨ New Features <a class="header-anchor" href="#new-features-1" aria-label="Permalink to &quot;:sparkles: New Features&quot;">​</a></h3><ul><li><a href="https://github.com/MongoCamp/mongodb-driver/commit/9e73ce18a4c4fe5b4354da51962365db303f63cf" target="_blank" rel="noreferrer"><code>9e73ce1</code></a> - support lucene query converter support search with long value on string field <em>(commit by <a href="https://github.com/QuadStingray" target="_blank" rel="noreferrer">@QuadStingray</a>)</em></li><li><a href="https://github.com/MongoCamp/mongodb-driver/commit/c6fe36b5c41e356d9540b9e85d30ca61f50dc9aa" target="_blank" rel="noreferrer"><code>c6fe36b</code></a> - <strong>jdbc</strong>: start implementing jdbc driver for mongodb <em>(commit by <a href="https://github.com/QuadStingray" target="_blank" rel="noreferrer">@QuadStingray</a>)</em></li><li><a href="https://github.com/MongoCamp/mongodb-driver/commit/36a43071d15a0b5ad9dc4a26c94fe06be215265f" target="_blank" rel="noreferrer"><code>36a4307</code></a> - sql converter with execution <em>(commit by <a href="https://github.com/QuadStingray" target="_blank" rel="noreferrer">@QuadStingray</a>)</em></li><li><a href="https://github.com/MongoCamp/mongodb-driver/commit/0408c7e2cd9d581775697f7bc30cf1856c2d938a" target="_blank" rel="noreferrer"><code>0408c7e</code></a> - implemented jdbc driver methods <em>(commit by <a href="https://github.com/QuadStingray" target="_blank" rel="noreferrer">@QuadStingray</a>)</em></li><li><a href="https://github.com/MongoCamp/mongodb-driver/commit/0a7a7de71071c4f6eaeda16fd6528b0fdf578df0" target="_blank" rel="noreferrer"><code>0a7a7de</code></a> - added show databases and show tables implementation <em>(commit by <a href="https://github.com/QuadStingray" target="_blank" rel="noreferrer">@QuadStingray</a>)</em></li><li><a href="https://github.com/MongoCamp/mongodb-driver/commit/cf5aa462ab64554bf96aaf3380451841436c1971" target="_blank" rel="noreferrer"><code>cf5aa46</code></a> - execute sql with database selection <em>(commit by <a href="https://github.com/QuadStingray" target="_blank" rel="noreferrer">@QuadStingray</a>)</em></li><li><a href="https://github.com/MongoCamp/mongodb-driver/commit/8e242155af024145d0cb7c378f051a9149bb336e" target="_blank" rel="noreferrer"><code>8e24215</code></a> - detect schema <em>(commit by <a href="https://github.com/QuadStingray" target="_blank" rel="noreferrer">@QuadStingray</a>)</em></li><li><a href="https://github.com/MongoCamp/mongodb-driver/commit/2ecc2769539fc111a24dd8da5a7f1fe99c52a016" target="_blank" rel="noreferrer"><code>2ecc276</code></a> - implemented jdbc driver methods <em>(commit by <a href="https://github.com/QuadStingray" target="_blank" rel="noreferrer">@QuadStingray</a>)</em></li><li><a href="https://github.com/MongoCamp/mongodb-driver/commit/8a39da85d512f133ad861e979e038a75961b81c6" target="_blank" rel="noreferrer"><code>8a39da8</code></a> - compatibility for scala 2.12 <em>(commit by <a href="https://github.com/QuadStingray" target="_blank" rel="noreferrer">@QuadStingray</a>)</em></li><li><a href="https://github.com/MongoCamp/mongodb-driver/commit/022cc63ed9fa5eaae628c2502b25c140bda7705e" target="_blank" rel="noreferrer"><code>022cc63</code></a> - refactor for jsqlparser 5.0 <em>(commit by <a href="https://github.com/QuadStingray" target="_blank" rel="noreferrer">@QuadStingray</a>)</em></li></ul><h3 id="bug-fixes-1" tabindex="-1">🐛 Bug Fixes <a class="header-anchor" href="#bug-fixes-1" aria-label="Permalink to &quot;:bug: Bug Fixes&quot;">​</a></h3><ul><li><a href="https://github.com/MongoCamp/mongodb-driver/commit/d2f9727f78d74409ab2a2ea0d84df87b96bdc4d0" target="_blank" rel="noreferrer"><code>d2f9727</code></a> - fix not compiling test for schema <em>(commit by <a href="https://github.com/QuadStingray" target="_blank" rel="noreferrer">@QuadStingray</a>)</em></li></ul><h3 id="refactors-1" tabindex="-1">♻️ Refactors <a class="header-anchor" href="#refactors-1" aria-label="Permalink to &quot;:recycle: Refactors&quot;">​</a></h3><ul><li><a href="https://github.com/MongoCamp/mongodb-driver/commit/0c8861a677416d972240d6ac30c6635ab73d1333" target="_blank" rel="noreferrer"><code>0c8861a</code></a> - added serverAddressList as connection option <em>(commit by <a href="https://github.com/QuadStingray" target="_blank" rel="noreferrer">@QuadStingray</a>)</em></li><li><a href="https://github.com/MongoCamp/mongodb-driver/commit/51aa92708c5aa5585c1925b6c5d97f5adb9bcbd7" target="_blank" rel="noreferrer"><code>51aa927</code></a> - <strong>java</strong>: drop java 11 and 19 support because <code>org.apache.lucene.queryparser</code> has dropped it <em>(commit by <a href="https://github.com/QuadStingray" target="_blank" rel="noreferrer">@QuadStingray</a>)</em></li></ul><h3 id="tests" tabindex="-1">✅ Tests <a class="header-anchor" href="#tests" aria-label="Permalink to &quot;:white_check_mark: Tests&quot;">​</a></h3><ul><li><a href="https://github.com/MongoCamp/mongodb-driver/commit/08e247f60108a1c868309b965f6e35b75ad23f98" target="_blank" rel="noreferrer"><code>08e247f</code></a> - some errors on rerun tests <em>(commit by <a href="https://github.com/QuadStingray" target="_blank" rel="noreferrer">@QuadStingray</a>)</em></li></ul><h3 id="chores-1" tabindex="-1">🔧 Chores <a class="header-anchor" href="#chores-1" aria-label="Permalink to &quot;:wrench: Chores&quot;">​</a></h3><ul><li><a href="https://github.com/MongoCamp/mongodb-driver/commit/4d1846930ba430da4872c7d41b3b8e4bf8283d62" target="_blank" rel="noreferrer"><code>4d18469</code></a> - Dependency Update and changes for mongo-scala-driver 5.0.0 <em>(commit by <a href="https://github.com/QuadStingray" target="_blank" rel="noreferrer">@QuadStingray</a>)</em></li><li><a href="https://github.com/MongoCamp/mongodb-driver/commit/094f50d4afb2cbf9774c18c4a0fb998000c62221" target="_blank" rel="noreferrer"><code>094f50d</code></a> - plugins updated 5 dependency updates for project <em>(commit by <a href="https://github.com/QuadStingray" target="_blank" rel="noreferrer">@QuadStingray</a>)</em></li><li><a href="https://github.com/MongoCamp/mongodb-driver/commit/5f5c1ab1ca04359cdac37dbc1c6f5a879c4347be" target="_blank" rel="noreferrer"><code>5f5c1ab</code></a> - 7 dependency updates for mongodb-driver <em>(commit by <a href="https://github.com/QuadStingray" target="_blank" rel="noreferrer">@QuadStingray</a>)</em></li><li><a href="https://github.com/MongoCamp/mongodb-driver/commit/3948d48029f9389e7b13f0e9961657ef9f4d1fb0" target="_blank" rel="noreferrer"><code>3948d48</code></a> - 13 dependency updates for mongodb-driver <em>(commit by <a href="https://github.com/QuadStingray" target="_blank" rel="noreferrer">@QuadStingray</a>)</em></li></ul><h2 id="v2-7-0-2024-02-24" tabindex="-1"><a href="https://github.com/MongoCamp/mongodb-driver/compare/v2.6.10...v2.7.0" target="_blank" rel="noreferrer">v2.7.0</a> - 2024-02-24 <a class="header-anchor" href="#v2-7-0-2024-02-24" aria-label="Permalink to &quot;[v2.7.0] - 2024-02-24&quot;">​</a></h2><h3 id="chores-2" tabindex="-1">🔧 Chores <a class="header-anchor" href="#chores-2" aria-label="Permalink to &quot;:wrench: Chores&quot;">​</a></h3><ul><li><a href="https://github.com/MongoCamp/mongodb-driver/commit/0970eec2d2e99439cd0d9c864a20b193e0e58df2" target="_blank" rel="noreferrer"><code>0970eec</code></a> - Prepare next 2.7.x versions <em>(commit by <a href="https://github.com/sfxcode" target="_blank" rel="noreferrer">@sfxcode</a>)</em></li></ul><h2 id="v2-6-9-2024-02-24" tabindex="-1"><a href="https://github.com/MongoCamp/mongodb-driver/compare/v2.6.8...v2.6.9" target="_blank" rel="noreferrer">v2.6.9</a> - 2024-02-24 <a class="header-anchor" href="#v2-6-9-2024-02-24" aria-label="Permalink to &quot;[v2.6.9] - 2024-02-24&quot;">​</a></h2><h3 id="chores-3" tabindex="-1">🔧 Chores <a class="header-anchor" href="#chores-3" aria-label="Permalink to &quot;:wrench: Chores&quot;">​</a></h3><ul><li><a href="https://github.com/MongoCamp/mongodb-driver/commit/f7b3fbc319ad1918a1f214a1769c96b4277473de" target="_blank" rel="noreferrer"><code>f7b3fbc</code></a> - <strong>Dependencies</strong>: use last scala versions <em>(commit by <a href="https://github.com/sfxcode" target="_blank" rel="noreferrer">@sfxcode</a>)</em></li></ul><h2 id="v2-6-8-2023-11-21" tabindex="-1"><a href="https://github.com/MongoCamp/mongodb-driver/compare/v2.6.7...v2.6.8" target="_blank" rel="noreferrer">v2.6.8</a> - 2023-11-21 <a class="header-anchor" href="#v2-6-8-2023-11-21" aria-label="Permalink to &quot;[v2.6.8] - 2023-11-21&quot;">​</a></h2><h3 id="refactors-2" tabindex="-1">♻️ Refactors <a class="header-anchor" href="#refactors-2" aria-label="Permalink to &quot;:recycle: Refactors&quot;">​</a></h3><ul><li><a href="https://github.com/MongoCamp/mongodb-driver/commit/292355c1ca47fb8e79f98aadd01018eecf0e4451" target="_blank" rel="noreferrer"><code>292355c</code></a> - Use MaxWait also Pagination and other calls <em>(commit by <a href="https://github.com/QuadStingray" target="_blank" rel="noreferrer">@QuadStingray</a>)</em></li></ul><h3 id="chores-4" tabindex="-1">🔧 Chores <a class="header-anchor" href="#chores-4" aria-label="Permalink to &quot;:wrench: Chores&quot;">​</a></h3><ul><li><a href="https://github.com/MongoCamp/mongodb-driver/commit/63c3b466c1fafdf52eef0475027f883bd4387d9e" target="_blank" rel="noreferrer"><code>63c3b46</code></a> - dependency updates <em>(commit by <a href="https://github.com/QuadStingray" target="_blank" rel="noreferrer">@QuadStingray</a>)</em></li></ul><h2 id="v2-6-7-2023-11-04" tabindex="-1"><a href="https://github.com/MongoCamp/mongodb-driver/compare/v2.6.6...v2.6.7" target="_blank" rel="noreferrer">v2.6.7</a> - 2023-11-04 <a class="header-anchor" href="#v2-6-7-2023-11-04" aria-label="Permalink to &quot;[v2.6.7] - 2023-11-04&quot;">​</a></h2><h3 id="tests-1" tabindex="-1">✅ Tests <a class="header-anchor" href="#tests-1" aria-label="Permalink to &quot;:white_check_mark: Tests&quot;">​</a></h3><ul><li><a href="https://github.com/MongoCamp/mongodb-driver/commit/bdd695b956e30450be7a478539e4b5c3bf5b6852" target="_blank" rel="noreferrer"><code>bdd695b</code></a> - Added Java 21 to test <em>(commit by <a href="https://github.com/QuadStingray" target="_blank" rel="noreferrer">@QuadStingray</a>)</em></li></ul><h3 id="chores-5" tabindex="-1">🔧 Chores <a class="header-anchor" href="#chores-5" aria-label="Permalink to &quot;:wrench: Chores&quot;">​</a></h3><ul><li><a href="https://github.com/MongoCamp/mongodb-driver/commit/a6791682e8d7cf780a46a8d42ef7170122be4bd3" target="_blank" rel="noreferrer"><code>a679168</code></a> - fix build pipeline <em>(commit by <a href="https://github.com/QuadStingray" target="_blank" rel="noreferrer">@QuadStingray</a>)</em></li><li><a href="https://github.com/MongoCamp/mongodb-driver/commit/89484d41c613cbc65dd61320460d1da8125bb4ef" target="_blank" rel="noreferrer"><code>89484d4</code></a> - update sbt to 1.9.7 <em>(commit by <a href="https://github.com/QuadStingray" target="_blank" rel="noreferrer">@QuadStingray</a>)</em></li></ul><h2 id="v2-6-5-2023-10-22" tabindex="-1"><a href="https://github.com/MongoCamp/mongodb-driver/compare/v2.6.4...v2.6.5" target="_blank" rel="noreferrer">v2.6.5</a> - 2023-10-22 <a class="header-anchor" href="#v2-6-5-2023-10-22" aria-label="Permalink to &quot;[v2.6.5] - 2023-10-22&quot;">​</a></h2><h3 id="refactors-3" tabindex="-1">♻️ Refactors <a class="header-anchor" href="#refactors-3" aria-label="Permalink to &quot;:recycle: Refactors&quot;">​</a></h3><ul><li><a href="https://github.com/MongoCamp/mongodb-driver/commit/96203b88658b167bd30a3e006ca8a2aab89d0432" target="_blank" rel="noreferrer"><code>96203b8</code></a> - GraalVm native configuration <em>(commit by <a href="https://github.com/QuadStingray" target="_blank" rel="noreferrer">@QuadStingray</a>)</em></li></ul><h3 id="chores-6" tabindex="-1">🔧 Chores <a class="header-anchor" href="#chores-6" aria-label="Permalink to &quot;:wrench: Chores&quot;">​</a></h3><ul><li><a href="https://github.com/MongoCamp/mongodb-driver/commit/331b5567d50a01b3c6093ebbd76334c4b5f929d8" target="_blank" rel="noreferrer"><code>331b556</code></a> - Found 11 dependency updates for mongodb-driver <em>(commit by <a href="https://github.com/QuadStingray" target="_blank" rel="noreferrer">@QuadStingray</a>)</em></li></ul><h2 id="_2-6-4-2023-07-29" tabindex="-1"><a href="./.html">2.6.4</a> (2023-07-29) <a class="header-anchor" href="#_2-6-4-2023-07-29" aria-label="Permalink to &quot;[2.6.4]() (2023-07-29)&quot;">​</a></h2><h3 id="maintenance" tabindex="-1">Maintenance <a class="header-anchor" href="#maintenance" aria-label="Permalink to &quot;Maintenance&quot;">​</a></h3><ul><li><strong>Dependencies:</strong> mongo scala driver 4.10.2 (<a href="https://github.com/MongoCamp/mongodb-driver/commit/faa22b041f84b290b72eae509ed6993ed571926e" target="_blank" rel="noreferrer">faa22b0</a>)</li></ul><h2 id="_2-6-3-2023-05-12" tabindex="-1"><a href="./.html">2.6.3</a> (2023-05-12) <a class="header-anchor" href="#_2-6-3-2023-05-12" aria-label="Permalink to &quot;[2.6.3]() (2023-05-12)&quot;">​</a></h2><h3 id="bug-fixes-2" tabindex="-1">Bug Fixes <a class="header-anchor" href="#bug-fixes-2" aria-label="Permalink to &quot;Bug Fixes&quot;">​</a></h3><ul><li>paginated aggregation with empty response (<a href="https://github.com/MongoCamp/mongodb-driver/commit/0acf10cc1089da7f7f9411dfc44972609c3cab8e" target="_blank" rel="noreferrer">0acf10c</a>)</li></ul><h3 id="maintenance-1" tabindex="-1">Maintenance <a class="header-anchor" href="#maintenance-1" aria-label="Permalink to &quot;Maintenance&quot;">​</a></h3><ul><li>dependency update <code>lucene-queryparser</code> 9.5.0 -&gt; 9.6.0 (<a href="https://github.com/MongoCamp/mongodb-driver/commit/a71a599071ae889a5df5f3b5ab5c254c7b29e0bf" target="_blank" rel="noreferrer">a71a599</a>)</li></ul><h2 id="_2-6-2-2023-04-29" tabindex="-1"><a href="./.html">2.6.2</a> (2023-04-29) <a class="header-anchor" href="#_2-6-2-2023-04-29" aria-label="Permalink to &quot;[2.6.2]() (2023-04-29)&quot;">​</a></h2><h3 id="bug-fixes-3" tabindex="-1">Bug Fixes <a class="header-anchor" href="#bug-fixes-3" aria-label="Permalink to &quot;Bug Fixes&quot;">​</a></h3><ul><li>deploy_ghpages.sh wrong cname (<a href="https://github.com/MongoCamp/mongodb-driver/commit/42bdf994c25940e717662d2b3671acd53fa6c797" target="_blank" rel="noreferrer">42bdf99</a>)</li></ul><h3 id="maintenance-2" tabindex="-1">Maintenance <a class="header-anchor" href="#maintenance-2" aria-label="Permalink to &quot;Maintenance&quot;">​</a></h3><ul><li><strong>Dependencies:</strong> mongo scala driver 4.9.1 (<a href="https://github.com/MongoCamp/mongodb-driver/commit/c1795d8374941ebc7ae4b91d12dd092e1651e4ef" target="_blank" rel="noreferrer">c1795d8</a>)</li></ul><h2 id="_2-6-1-2023-04-01" tabindex="-1"><a href="./.html">2.6.1</a> (2023-04-01) <a class="header-anchor" href="#_2-6-1-2023-04-01" aria-label="Permalink to &quot;[2.6.1]() (2023-04-01)&quot;">​</a></h2><h3 id="bug-fixes-4" tabindex="-1">Bug Fixes <a class="header-anchor" href="#bug-fixes-4" aria-label="Permalink to &quot;Bug Fixes&quot;">​</a></h3><ul><li>provided removed and docs fixed (<a href="https://github.com/MongoCamp/mongodb-driver/commit/c18592da3a53a5bf6098df03e6198c44334262c4" target="_blank" rel="noreferrer">c18592d</a>)</li></ul><h2 id="_2-6-0-2023-04-01" tabindex="-1"><a href="./.html">2.6.0</a> (2023-04-01) <a class="header-anchor" href="#_2-6-0-2023-04-01" aria-label="Permalink to &quot;[2.6.0]() (2023-04-01)&quot;">​</a></h2><h3 id="bug-fixes-5" tabindex="-1">Bug Fixes <a class="header-anchor" href="#bug-fixes-5" aria-label="Permalink to &quot;Bug Fixes&quot;">​</a></h3><ul><li>compatibility for scala 2.12 in LuceneQueryConverter (<a href="https://github.com/MongoCamp/mongodb-driver/commit/fdb1d35e71c217b7b7cdaf25871e75e71a656b6c" target="_blank" rel="noreferrer">fdb1d35</a>)</li><li>pagination aggregation should always use the RAW-DAO (<a href="https://github.com/MongoCamp/mongodb-driver/commit/0bf5dc9e52b3e281d6b3f2617d679db786c5a82c" target="_blank" rel="noreferrer">0bf5dc9</a>)</li></ul><h3 id="code-refactoring" tabindex="-1">Code Refactoring <a class="header-anchor" href="#code-refactoring" aria-label="Permalink to &quot;Code Refactoring&quot;">​</a></h3><ul><li>cleanup plugins (<a href="https://github.com/MongoCamp/mongodb-driver/commit/5f3134da4d62ecd56335bbcc664e89c0f0137812" target="_blank" rel="noreferrer">5f3134d</a>)</li><li>DocumentIncludes and MongoImplicits to own files (<a href="https://github.com/MongoCamp/mongodb-driver/commit/753a63381dea71a92fa696aaa28f849580069af6" target="_blank" rel="noreferrer">753a633</a>)</li><li>foreach moved to pagination trait (<a href="https://github.com/MongoCamp/mongodb-driver/commit/ecbb62f4a4a1f8b6fd1f4c1ee82dd56099567d79" target="_blank" rel="noreferrer">ecbb62f</a>)</li><li>foreach moved to pagination trait (<a href="https://github.com/MongoCamp/mongodb-driver/commit/54dd25072f7fb4091400a600129299e68908589c" target="_blank" rel="noreferrer">54dd250</a>)</li></ul><h3 id="features" tabindex="-1">Features <a class="header-anchor" href="#features" aria-label="Permalink to &quot;Features&quot;">​</a></h3><ul><li>Compact Method for complete user scope (<a href="https://github.com/MongoCamp/mongodb-driver/commit/e567be7fe2666d85ef8b96f98868a6f4844a6fe0" target="_blank" rel="noreferrer">e567be7</a>)</li><li>Compact Methods for Database or DAO (<a href="https://github.com/MongoCamp/mongodb-driver/commit/0926b1da0ba3d80f0be1bb996fc18ff18a9db535" target="_blank" rel="noreferrer">0926b1d</a>)</li><li>Foreach in Pagination Result (<a href="https://github.com/MongoCamp/mongodb-driver/commit/1fe1d8ba31f34e13df2afc3d1c3c9ae1f6b034ea" target="_blank" rel="noreferrer">1fe1d8b</a>)</li><li>Implicit for LuceneQuery to BsonDocument (<a href="https://github.com/MongoCamp/mongodb-driver/commit/32f10294baa8ef73bff2e26247f07e4639340ff3" target="_blank" rel="noreferrer">32f1029</a>)</li><li>Lucene Query can converted to Bson (<a href="https://github.com/MongoCamp/mongodb-driver/commit/e56e5d416535b21a4d2c92932db82b488a4321af" target="_blank" rel="noreferrer">e56e5d4</a>)</li><li>Pagination for MongoDb Search and Aggregation (<a href="https://github.com/MongoCamp/mongodb-driver/commit/52abbe3f8b34a434e88b889b9f19a595037936ff" target="_blank" rel="noreferrer">52abbe3</a>)</li></ul><h3 id="maintenance-3" tabindex="-1">Maintenance <a class="header-anchor" href="#maintenance-3" aria-label="Permalink to &quot;Maintenance&quot;">​</a></h3><ul><li><strong>dependencies:</strong> Found 7 dependency updates (<a href="https://github.com/MongoCamp/mongodb-driver/commit/89a0e272072b8f9dfeefb920627a048091e08ca1" target="_blank" rel="noreferrer">89a0e27</a>)</li><li><strong>plugins:</strong> Found 5 dependency updates (<a href="https://github.com/MongoCamp/mongodb-driver/commit/cc13a06bdb495cfa2518b44d21058928b32fac6a" target="_blank" rel="noreferrer">cc13a06</a>)</li></ul><h3 id="reverts" tabindex="-1">Reverts <a class="header-anchor" href="#reverts" aria-label="Permalink to &quot;Reverts&quot;">​</a></h3><ul><li>sonatype removed by accident (<a href="https://github.com/MongoCamp/mongodb-driver/commit/e320ed1eef7bb9ffad5234743f882f65ee1bf3dc" target="_blank" rel="noreferrer">e320ed1</a>)</li></ul><h2 id="_2-5-4-2023-02-12" tabindex="-1"><a href="./.html">2.5.4</a> (2023-02-12) <a class="header-anchor" href="#_2-5-4-2023-02-12" aria-label="Permalink to &quot;[2.5.4]() (2023-02-12)&quot;">​</a></h2><h3 id="maintenance-4" tabindex="-1">Maintenance <a class="header-anchor" href="#maintenance-4" aria-label="Permalink to &quot;Maintenance&quot;">​</a></h3><ul><li><strong>Dependencies:</strong> mongo scala driver 4.9.0 (<a href="https://github.com/MongoCamp/mongodb-driver/commit/356c7c42291ca7d3f7a7ad69a60dfac3493dbc9f" target="_blank" rel="noreferrer">356c7c4</a>)</li></ul><h2 id="_2-5-3-2022-12-04" tabindex="-1"><a href="./.html">2.5.3</a> (2022-12-04) <a class="header-anchor" href="#_2-5-3-2022-12-04" aria-label="Permalink to &quot;[2.5.3]() (2022-12-04)&quot;">​</a></h2><h3 id="maintenance-5" tabindex="-1">Maintenance <a class="header-anchor" href="#maintenance-5" aria-label="Permalink to &quot;Maintenance&quot;">​</a></h3><ul><li><strong>Dependencies:</strong> mongo scala driver 4.8.0 (<a href="https://github.com/MongoCamp/mongodb-driver/commit/2a3af879df3229fe44aca7dd92c6faefccdfa118" target="_blank" rel="noreferrer">2a3af87</a>)</li></ul><h2 id="_2-5-1-2022-10-11" tabindex="-1"><a href="./.html">2.5.1</a> (2022-10-11) <a class="header-anchor" href="#_2-5-1-2022-10-11" aria-label="Permalink to &quot;[2.5.1]() (2022-10-11)&quot;">​</a></h2><h3 id="maintenance-6" tabindex="-1">Maintenance <a class="header-anchor" href="#maintenance-6" aria-label="Permalink to &quot;Maintenance&quot;">​</a></h3><ul><li><strong>Dependencies:</strong> mongo scala driver 4.7.2 (<a href="https://github.com/MongoCamp/mongodb-driver/commit/f050d78ac16e6cfbbf52c067a3eacd8091965bbb" target="_blank" rel="noreferrer">f050d78</a>)</li></ul><h2 id="_2-5-0-2022-07-27" tabindex="-1"><a href="./.html">2.5.0</a> (2022-07-27) <a class="header-anchor" href="#_2-5-0-2022-07-27" aria-label="Permalink to &quot;[2.5.0]() (2022-07-27)&quot;">​</a></h2><h3 id="maintenance-7" tabindex="-1">Maintenance <a class="header-anchor" href="#maintenance-7" aria-label="Permalink to &quot;Maintenance&quot;">​</a></h3><ul><li><strong>Dependencies:</strong> mongo scala driver 4.7.0 (<a href="https://github.com/MongoCamp/mongodb-driver/commit/9e37be88878bfb27d1ba092a45fbb19c63e431e6" target="_blank" rel="noreferrer">9e37be8</a>)</li></ul><h2 id="_2-4-9-2022-07-13" tabindex="-1"><a href="./.html">2.4.9</a> (2022-07-13) <a class="header-anchor" href="#_2-4-9-2022-07-13" aria-label="Permalink to &quot;[2.4.9]() (2022-07-13)&quot;">​</a></h2><h3 id="bug-fixes-6" tabindex="-1">Bug Fixes <a class="header-anchor" href="#bug-fixes-6" aria-label="Permalink to &quot;Bug Fixes&quot;">​</a></h3><ul><li>recursive BsonConverter.asMap for List of Documents (<a href="https://github.com/MongoCamp/mongodb-driver/commit/3c7d0c87096f0342936d38dd0e2fbd0c0bf64785" target="_blank" rel="noreferrer">3c7d0c8</a>)</li><li>recursive BsonConverter.asMap for List of Documents (<a href="https://github.com/MongoCamp/mongodb-driver/commit/901fb0d6670feb31697cd25b7b5eb326b2ac5695" target="_blank" rel="noreferrer">901fb0d</a>)</li></ul><h3 id="maintenance-8" tabindex="-1">Maintenance <a class="header-anchor" href="#maintenance-8" aria-label="Permalink to &quot;Maintenance&quot;">​</a></h3><ul><li>2 dependency updates (<a href="https://github.com/MongoCamp/mongodb-driver/commit/00d3202c8d5449d54ce4d1d3b0f4a3b8ad28f045" target="_blank" rel="noreferrer">00d3202</a>)</li></ul><h2 id="_2-4-8-2022-06-29" tabindex="-1"><a href="./.html">2.4.8</a> (2022-06-29) <a class="header-anchor" href="#_2-4-8-2022-06-29" aria-label="Permalink to &quot;[2.4.8]() (2022-06-29)&quot;">​</a></h2><h2 id="_2-4-7-2022-06-27" tabindex="-1"><a href="./.html">2.4.7</a> (2022-06-27) <a class="header-anchor" href="#_2-4-7-2022-06-27" aria-label="Permalink to &quot;[2.4.7]() (2022-06-27)&quot;">​</a></h2><h3 id="bug-fixes-7" tabindex="-1">Bug Fixes <a class="header-anchor" href="#bug-fixes-7" aria-label="Permalink to &quot;Bug Fixes&quot;">​</a></h3><ul><li><strong>Database:</strong> Database Size (<a href="https://github.com/MongoCamp/mongodb-driver/commit/c35778c8e3cff94d0759a62c9400342e154c9c9d" target="_blank" rel="noreferrer">c35778c</a>)</li></ul><h3 id="code-refactoring-1" tabindex="-1">Code Refactoring <a class="header-anchor" href="#code-refactoring-1" aria-label="Permalink to &quot;Code Refactoring&quot;">​</a></h3><ul><li><strong>Project:</strong> moved to github mongocamp organization (<a href="https://github.com/MongoCamp/mongodb-driver/commit/bb65f8b87e6c77bd5976a1828595366e3e3953d2" target="_blank" rel="noreferrer">bb65f8b</a>)</li></ul><h3 id="features-1" tabindex="-1">Features <a class="header-anchor" href="#features-1" aria-label="Permalink to &quot;Features&quot;">​</a></h3><ul><li>Added MongoDb Listeners to MongoConfig (Command and Connection Pool) (<a href="https://github.com/MongoCamp/mongodb-driver/commit/bbe77de2e7151ac3c00a38f38c3ed29c1da06388" target="_blank" rel="noreferrer">bbe77de</a>)</li><li>installed pgp key from secret (<a href="https://github.com/MongoCamp/mongodb-driver/commit/b15f23295e831b498aec0b132dedcdbbcfd2d05b" target="_blank" rel="noreferrer">b15f232</a>)</li></ul><h3 id="maintenance-9" tabindex="-1">Maintenance <a class="header-anchor" href="#maintenance-9" aria-label="Permalink to &quot;Maintenance&quot;">​</a></h3><ul><li>7 dependency updates (<a href="https://github.com/MongoCamp/mongodb-driver/commit/3e844ad65aeada7abc30cad8fa92e98a3882be59" target="_blank" rel="noreferrer">3e844ad</a>)</li><li><strong>Dependencies:</strong> mongo scala driver 4.6.1 (<a href="https://github.com/MongoCamp/mongodb-driver/commit/63976595197ed9a75932803429347dd25666af86" target="_blank" rel="noreferrer">6397659</a>)</li><li><strong>dependencies:</strong> prepare release (<a href="https://github.com/MongoCamp/mongodb-driver/commit/676cb2964c5f11d42e1b08315e7fa538a7f5cf11" target="_blank" rel="noreferrer">676cb29</a>)</li><li><strong>dependencies:</strong> update (<a href="https://github.com/MongoCamp/mongodb-driver/commit/4f52595ec2d99430669b0b032914f55f83c14abd" target="_blank" rel="noreferrer">4f52595</a>)</li><li><strong>dependencies:</strong> update (<a href="https://github.com/MongoCamp/mongodb-driver/commit/e3e073e28c497627df0d5da62235862f5ef600bc" target="_blank" rel="noreferrer">e3e073e</a>)</li><li><strong>dependencies:</strong> update (<a href="https://github.com/MongoCamp/mongodb-driver/commit/cf7d00e4991f5d6f453dc73ea8b05d9451a329c0" target="_blank" rel="noreferrer">cf7d00e</a>)</li><li><strong>dependencies:</strong> update (<a href="https://github.com/MongoCamp/mongodb-driver/commit/0fd5bb71aedacda535538c579891cff79efc27c9" target="_blank" rel="noreferrer">0fd5bb7</a>)</li><li>Dependency Updates (<a href="https://github.com/MongoCamp/mongodb-driver/commit/9e4658c210a1bbd869b5ff294e89349951eb1e23" target="_blank" rel="noreferrer">9e4658c</a>)</li><li>Dependency Updates (<a href="https://github.com/MongoCamp/mongodb-driver/commit/3f749c05300b23eaf48588966aec7a926f3c4f1c" target="_blank" rel="noreferrer">3f749c0</a>)</li><li><strong>deps:</strong> mongo scala driver 4.5.0 (<a href="https://github.com/MongoCamp/mongodb-driver/commit/c547da7bb78e60013ead92c814d5ef4646c5e76b" target="_blank" rel="noreferrer">c547da7</a>)</li><li><strong>deps:</strong> mongo scala driver 4.5.1 (<a href="https://github.com/MongoCamp/mongodb-driver/commit/208eb54b58c616e19d515df18cfbf27bcdbe3b0a" target="_blank" rel="noreferrer">208eb54</a>)</li><li><strong>plugins:</strong> Dependency Updates (<a href="https://github.com/MongoCamp/mongodb-driver/commit/50c44a255a53721ea7dad36336b959290cb6c4e7" target="_blank" rel="noreferrer">50c44a2</a>)</li><li><strong>version:</strong> prepare next release version (<a href="https://github.com/MongoCamp/mongodb-driver/commit/d01f042043ac745320b01316f1493565acdcf2b2" target="_blank" rel="noreferrer">d01f042</a>)</li><li><strong>version:</strong> prepare next release version (<a href="https://github.com/MongoCamp/mongodb-driver/commit/0396f1058a589836e09addf3a3648891929ecded" target="_blank" rel="noreferrer">0396f10</a>)</li><li><strong>version:</strong> prepare next release version (<a href="https://github.com/MongoCamp/mongodb-driver/commit/54c87c4f25ca2f77757f352df44cb6f4aca68314" target="_blank" rel="noreferrer">54c87c4</a>)</li><li><strong>version:</strong> prepare next release version (<a href="https://github.com/MongoCamp/mongodb-driver/commit/5aa71df7fc3e875d48f950787c93024020a6d3e0" target="_blank" rel="noreferrer">5aa71df</a>)</li><li><strong>version:</strong> prepare next release version (<a href="https://github.com/MongoCamp/mongodb-driver/commit/637f2629256785e754047e605f47cb5abc148f8d" target="_blank" rel="noreferrer">637f262</a>)</li></ul>',98)]))}const f=a(n,[["render",i]]);export{g as __pageData,f as default};