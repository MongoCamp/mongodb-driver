import{_ as s,c as t,o as i,a2 as e}from"./chunks/framework.B3hEhInr.js";const c=JSON.parse('{"title":"Metadata","description":"","frontmatter":{},"headers":[],"relativePath":"documentation/gridfs-dao/metadata.md","filePath":"documentation/gridfs-dao/metadata.md","lastUpdated":1743452235000}'),l={name:"documentation/gridfs-dao/metadata.md"};function n(p,a,h,d,k,r){return i(),t("div",null,a[0]||(a[0]=[e(`<h1 id="metadata" tabindex="-1">Metadata <a class="header-anchor" href="#metadata" aria-label="Permalink to &quot;Metadata&quot;">​</a></h1><p>Metadata can be updated by the <a href="./">GridFSDAO</a> object.</p><h2 id="update-complete-metadata" tabindex="-1">Update complete Metadata <a class="header-anchor" href="#update-complete-metadata" aria-label="Permalink to &quot;Update complete Metadata&quot;">​</a></h2><p>UpdateMetadata function will replace the whole metadata for one file.</p><div class="language-scala vp-adaptive-theme"><button title="Copy Code" class="copy"></button><span class="lang">scala</span><pre class="shiki shiki-themes github-light github-dark vp-code" tabindex="0"><code><span class="line"><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">val</span><span style="--shiki-light:#E36209;--shiki-dark:#FFAB70;"> value</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;"> =</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;"> Map</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(</span><span style="--shiki-light:#032F62;--shiki-dark:#9ECBFF;">&quot;index&quot;</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">-&gt;</span><span style="--shiki-light:#005CC5;--shiki-dark:#79B8FF;">11</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">, </span><span style="--shiki-light:#032F62;--shiki-dark:#9ECBFF;">&quot;category&quot;</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">-&gt;</span><span style="--shiki-light:#032F62;--shiki-dark:#9ECBFF;">&quot;templates&quot;</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">)</span></span>
<span class="line"><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">ImageFilesDAO</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">.updateMetadata(oid, value)</span></span></code></pre></div><h2 id="update-metadata-elements" tabindex="-1">Update Metadata elements <a class="header-anchor" href="#update-metadata-elements" aria-label="Permalink to &quot;Update Metadata elements&quot;">​</a></h2><p>UpdateMetadataElement/s update some part of the metadata by a given filter.</p><div class="language-scala vp-adaptive-theme"><button title="Copy Code" class="copy"></button><span class="lang">scala</span><pre class="shiki shiki-themes github-light github-dark vp-code" tabindex="0"><code><span class="line"><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">val</span><span style="--shiki-light:#E36209;--shiki-dark:#FFAB70;"> elements</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;"> =</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">  Map</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(</span><span style="--shiki-light:#032F62;--shiki-dark:#9ECBFF;">&quot;category&quot;</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">-&gt;</span><span style="--shiki-light:#032F62;--shiki-dark:#9ECBFF;">&quot;logos&quot;</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">)</span></span>
<span class="line"><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">val</span><span style="--shiki-light:#E36209;--shiki-dark:#FFAB70;"> filter</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;"> =</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;"> Map</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">() </span><span style="--shiki-light:#6A737D;--shiki-dark:#6A737D;">// all files</span></span>
<span class="line"><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">ImageFilesDAO</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">.updateMetadataElements(filter, elements)</span></span></code></pre></div>`,8)]))}const g=s(l,[["render",n]]);export{c as __pageData,g as default};
