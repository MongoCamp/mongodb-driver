import{_ as s,c as i,a2 as e,o as n}from"./chunks/framework.DPuwY6B9.js";const k=JSON.parse('{"title":"GridFSDAO","description":"","frontmatter":{},"headers":[],"relativePath":"documentation/gridfs-dao/index.md","filePath":"documentation/gridfs-dao/index.md","lastUpdated":1730840042000}'),t={name:"documentation/gridfs-dao/index.md"};function l(o,a,r,d,h,p){return n(),i("div",null,a[0]||(a[0]=[e(`<h1 id="gridfsdao" tabindex="-1">GridFSDAO <a class="header-anchor" href="#gridfsdao" aria-label="Permalink to &quot;GridFSDAO&quot;">​</a></h1><h2 id="info" tabindex="-1">Info <a class="header-anchor" href="#info" aria-label="Permalink to &quot;Info&quot;">​</a></h2><p>GridFSDAO adds MongoDB <a href="https://docs.mongodb.com/manual/core/gridfs/" target="_blank" rel="noreferrer">GridFS</a> support.</p><p>It provides easy upload, download and metadata handling.</p><p>Sometimes also normal collections can be helpful for storing data.</p><div class="warning custom-block"><p class="custom-block-title">Official_MongoDB_Documentation</p><p>Furthermore, if your files are all smaller than the 16 MB BSON Document Size limit, consider storing each file in a single document instead of using GridFS. You may use the BinData data type to store the binary data. See your drivers documentation for details on using BinData.Furthermore, if your files are all smaller than the 16 MB BSON Document Size limit, consider storing each file in a single document instead of using GridFS. You may use the BinData data type to store the binary data. See your drivers documentation for details on using BinData.</p></div><h2 id="usage" tabindex="-1">Usage <a class="header-anchor" href="#usage" aria-label="Permalink to &quot;Usage&quot;">​</a></h2><p>A <a href="http://mongodb.github.io/mongo-scala-driver/2.3/scaladoc/org/mongodb/scala/MongoDatabase.html" target="_blank" rel="noreferrer">MongoDatabase</a> and a bucket name is needed.</p><h3 id="create-dao" tabindex="-1">Create DAO <a class="header-anchor" href="#create-dao" aria-label="Permalink to &quot;Create DAO&quot;">​</a></h3><div class="language-scala vp-adaptive-theme"><button title="Copy Code" class="copy"></button><span class="lang">scala</span><pre class="shiki shiki-themes github-light github-dark vp-code" tabindex="0"><code><span class="line"></span>
<span class="line"><span style="--shiki-light:#6A737D;--shiki-dark:#6A737D;">  /**</span></span>
<span class="line"><span style="--shiki-light:#6A737D;--shiki-dark:#6A737D;">  * use bucket name fs</span></span>
<span class="line"><span style="--shiki-light:#6A737D;--shiki-dark:#6A737D;">  */</span></span>
<span class="line"><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">  object</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;"> ImageFilesDAO</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;"> extends</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;"> GridFSDAO</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(database)</span></span>
<span class="line"></span>
<span class="line"><span style="--shiki-light:#6A737D;--shiki-dark:#6A737D;">  /**</span></span>
<span class="line"><span style="--shiki-light:#6A737D;--shiki-dark:#6A737D;">  * use bucket name images</span></span>
<span class="line"><span style="--shiki-light:#6A737D;--shiki-dark:#6A737D;">  */</span></span>
<span class="line"><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">  object</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;"> ImageFilesDAO</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;"> extends</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;"> GridFSDAO</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(database, </span><span style="--shiki-light:#032F62;--shiki-dark:#9ECBFF;">&quot;images&quot;</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">)</span></span></code></pre></div>`,10)]))}const g=s(t,[["render",l]]);export{k as __pageData,g as default};