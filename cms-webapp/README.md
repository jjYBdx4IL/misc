# CMS WebApp

A Java webapp providing blog-like publishing functionality based on JavaEE
technologies and j2html.

The fulltext search index is stored below ~/.cms-webapp.

## Google Oauth2

Used for authentication.

Add your personal Google Oauth2 API credentials as follows:

    INSERT INTO CONFIGVALUE (KEY, VALUE)
    VALUES
        ('GOOGLE_OAUTH2_CLIENT_ID', '...'),
        ('GOOGLE_OAUTH2_CLIENT_SECRET', '...')

## Other DB config values

    INSERT INTO CONFIGVALUE (KEY, VALUE)
    VALUES
        ('WEBSITE_TITLE', '...')

Beware! The CONFIGVALUE table is annotated with @Cacheable. You might need
to clear the cache after having done updates to it.

## set up admin user

Define the admin user via server system property:

    cms.webapp.admin.uid = $uid

$uid currently is your Google account's unique subject prefixed with "google-".

See the follow up call in GoogleLogin.callback() to a successful oauth2 request.

## system properties for production

    hibernate.hbm2ddl.auto = validate

## system properties for development

    hibernate.hbm2ddl.auto = update
    hibernate.show_sql = true (stdout, or use your logger)
    hibernate.format_sql = true
    hibernate.use_sql_comments = true
    env.devel = true

The `env.devel` system property enables access to /devel/login which is needed
for testing (currently there is the RootIT unit test available from the cms-it
package).

## remarks

Values provided by hibernate.xml take precedence, so make sure the aforementioned
properties are not listed there.

Setting system properties in WildFly 11 requires a server restart.

HTTP session persistence requres &lt;distributable/> tag in web.xml. There is another
variant, but that's not reliable (too fast reloads tend to lose the session store).

HTTP servers generally have a problem of not knowing the form data encoding submitted
to them because browser usually don't care to attach the charset encoding information.
See https://stackoverflow.com/questions/3278900/httpservletrequest-setcharacterencoding-seems-to-do-nothing/46162810#46162810
on how to change the servlet container's default charset encoding. I have added
descriptor files for glassfish and wildfly to fix this issue (ie. set the default
encoding to UTF-8, accept-charset params in html forms must have the same value!).
Only WildFly 11 CR1 is tested.


