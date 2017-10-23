# CMS WebApp

A Java webapp providing blog-like publishing functionality based on JavaEE
technologies and j2html.

The fulltext search index is stored below ~/lucene/cms-webapp.

The database file is stored at ~/h2.mv.db.

An up-to-date list of features can be found at: https://gruust.stream/byTag/site-software


## Google Oauth2

Used for authentication.

Add your personal Google Oauth2 API credentials as follows:

    INSERT INTO CONFIGVALUE (KEY, VALUE)
    VALUES
        ('GOOGLE_OAUTH2_CLIENT_ID', '...'),
        ('GOOGLE_OAUTH2_CLIENT_SECRET', '...'),
        ('HTML_HEAD_FRAGMENT', '<script>...</script>')

You can use the h2-frontend war package to access the SQL database directly.
It denies connections from anything else than localhost. 

https://console.developers.google.com/apis/credentials



## Other DB config values

    INSERT INTO CONFIGVALUE (KEY, VALUE)
    VALUES
        ('WEBSITE_TITLE', '...')

Beware! The CONFIGVALUE table is annotated with @Cacheable. You might need
to clear the cache after having done updates to it. (currently, the AppCache bean is
doing the caching, and when using the settings page, an automatic reload is done
on save)

See ConfigKey class for a complete list of possible config values. Or simply visit
the settings page.



## set up admin user

Define the admin user via 'ADMINS' config key:

    INSERT INTO CONFIGVALUE (KEY, VALUE)
    VALUES
        ('ADMINS', 'google-$uid')

$uid currently is your Google account's unique subject/unique google user id.

To get your unique google user id, login to the application. Upon redirect from
the Google confirmation form page, the server will log that information. Search
for "unique google user id".

Currently, all pages that require login, also require admin status.



## system properties for production

None. Defaults should be appropriate.



## system properties for development

    hibernate.hbm2ddl.auto = update
    hibernate.show_sql = true (stdout, or use your logger)
    hibernate.format_sql = true
    hibernate.use_sql_comments = true
    cms.devel = true

See also cms-it/pom.xml where you can find the test setup.

The `cms.devel` system property enables access to /devel/login which is needed
for automated testing.



## dev remarks

Values provided by hibernate.xml take precedence, so make sure the aforementioned
properties are not listed there.

Setting system properties in WildFly 11 requires a server restart/reload.

HTTP session persistence requres &lt;distributable/> tag in web.xml. There is another
variant, but that's not reliable (too fast reloads tend to lose the session store).

HTTP servers generally have a problem of not knowing the form data encoding submitted
to them because browser usually don't care to attach the charset encoding information.
See https://stackoverflow.com/questions/3278900/httpservletrequest-setcharacterencoding-seems-to-do-nothing/46162810#46162810
on how to change the servlet container's default charset encoding. I have added
descriptor files for glassfish and wildfly to fix this issue (ie. set the default
encoding to UTF-8, accept-charset params in html forms must have the same value!).
Only WildFly 11 CR1 is tested.



## embedding, sanitization, markdown processing

Javascript performs the following steps in the listed order:

1. Markdown conversion (on editor save)
2. HTML sanitization (server side, on save)
3. replacing embed://service/id tags on page load, async, not delaying the page load, deferred until
   user scrolls stuff into his browser view

Currently only youtube is supported via:

    embed://youtube/$videoId
    embed://youtube/$videoId/1h3m  (<- start position)
    embed://image/<image-id>/<image-filename>
    embed://tag/<tag-name>

The editor's live preview only includes step 1.



## Google AdSense and Analytics

Go to the settings page and insert the corresponding javascripts into header and/or footer.



## Cookie Consent

Cookieconsent is enabled by setting its message on the settings page. The cookie consent message displayed
to the user will contain a link pointing to the tag 'site-privacy-policy'. For that reason, select a single post
and give it that tag, and put your privacy policy there. You also need to follow Google's privacy policy guide lines
if using any of their stuff.

Example cookie consent message:

```
We use cookies to personalise content and ads, to provide social media features and to analyse our traffic. We also share information about your use of our site with our social media, advertising and analytics partners who may combine it with other information you’ve provided to them or they’ve collected from your use of their services.

[How Google uses data when you use our partners' sites or apps](https://www.google.com/intl/en/policies/privacy/partners/)

On top of that, we need cookies to keep track of authorization/login status.
```

Adjust to your own needs. I don't take any responsibility. This is just an example.



## Ad-blocker detection

Go to settings page and set the corresponding option to TRUE.


