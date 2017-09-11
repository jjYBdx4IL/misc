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

## set up admin user

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

## remarks

Values provided by hibernate.xml take precedence, so make sure the aforementioned
properties are not listed there.

Setting system properties in WildFly 11 requires a server restart.

