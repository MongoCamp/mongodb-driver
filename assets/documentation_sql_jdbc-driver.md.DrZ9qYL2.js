import{_ as e,c as a,o as i,a2 as t}from"./chunks/framework.B3hEhInr.js";const c=JSON.parse('{"title":"JDBC driver","description":"","frontmatter":{},"headers":[],"relativePath":"documentation/sql/jdbc-driver.md","filePath":"documentation/sql/jdbc-driver.md","lastUpdated":1743452235000}'),n={name:"documentation/sql/jdbc-driver.md"};function r(l,s,h,o,p,d){return i(),a("div",null,s[0]||(s[0]=[t(`<h1 id="jdbc-driver" tabindex="-1">JDBC driver <a class="header-anchor" href="#jdbc-driver" aria-label="Permalink to &quot;JDBC driver&quot;">​</a></h1><p>The JDBC driver is a way to use the SQL queries in your application and run them like a &#39;normal&#39; SQL database. The driver is based on the <a href="./queryholder.html">MongoSqlQueryHolder</a> to convert the SQL query to a Mongo query and execute it on the MongoDB database.</p><h2 id="usage" tabindex="-1">Usage <a class="header-anchor" href="#usage" aria-label="Permalink to &quot;Usage&quot;">​</a></h2><h3 id="register-driver" tabindex="-1">Register Driver <a class="header-anchor" href="#register-driver" aria-label="Permalink to &quot;Register Driver&quot;">​</a></h3><p>In some environments you have to register the driver manually. This is the case for example in the tests.</p><div class="language-scala vp-adaptive-theme"><button title="Copy Code" class="copy"></button><span class="lang">scala</span><pre class="shiki shiki-themes github-light github-dark vp-code" tabindex="0"><code><span class="line"><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">val</span><span style="--shiki-light:#E36209;--shiki-dark:#FFAB70;"> connectionProps</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;"> =</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;"> new</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;"> Properties</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">()</span></span>
<span class="line"><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">val</span><span style="--shiki-light:#E36209;--shiki-dark:#FFAB70;"> driver</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">          =</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;"> new</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;"> MongoJdbcDriver</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">()</span></span>
<span class="line"><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">DriverManager</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">.registerDriver(driver)</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">connection </span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">=</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;"> DriverManager</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">.getConnection(</span></span>
<span class="line"><span style="--shiki-light:#032F62;--shiki-dark:#9ECBFF;">  &quot;jdbc:mongodb://localhost:27017/mongocamp-unit-test?retryWrites=true&amp;loadBalanced=false&amp;serverSelectionTimeoutMS=5000&amp;connectTimeoutMS=10000&quot;</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">,</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">  connectionProps</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">)</span></span></code></pre></div><p>After the driver is registered you can use the driver like a normal <a href="https://www.baeldung.com/java-jdbc" target="_blank" rel="noreferrer">JDBC driver</a>.</p><div class="tip custom-block"><p class="custom-block-title">TIP</p><p>The most default sql statements are supported, but because the difference between MongoDb and SQL the driver can&#39;t support SQL statements with subselects.</p></div>`,8)]))}const g=e(n,[["render",r]]);export{c as __pageData,g as default};
