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
        ('GOOGLE_OAUTH2_CLIENT_SECRET', '...'),
        ('HTML_HEAD_FRAGMENT', '<script>...</script>')

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

## embedding, sanitization, markdown processing

Javascript performs the following steps in the listed order:

1. Markdown conversion.
2. XSS protection is done using DOMPurify
3. replacing embed://service/id tags.

Currently only youtube is supported via:

    embed://youtube/$videoId
    embed://youtube/$videoId/1h3m  (<- start position)



The editor's live preview only includes step 1 atm.

## cookieconsent2

It's assumed that Google Analytics and stuff is inserted into the HTML HEAD.
So by default we have cookieconsent enabled (opt-out mode). It links
to a privacy page tagged with "site-privacy-policy".

Example content:

```
We use cookies to personalise content and ads, to provide social media features and to analyse our traffic. We also share information about your use of our site with our social media, advertising and analytics partners who may combine it with other information you’ve provided to them or they’ve collected from your use of their services.

[How Google uses data when you use our partners' sites or apps](https://www.google.com/intl/en/policies/privacy/partners/)

On top of that, we need cookies to keep track of authorization/login status.
```

Adjust to your own needs. We don't take any responsibility. This is just an example.

```
INSERT INTO CONFIGVALUE (KEY, VALUE)
VALUES
        ('HTML_HEAD_FRAGMENT',
'
<!-- Cookie consent banner --> 
<link rel="stylesheet" type="text/css" href="//cdnjs.cloudflare.com/ajax/libs/cookieconsent2/3.0.4/cookieconsent.min.css" /> 
<script src="//cdnjs.cloudflare.com/ajax/libs/cookieconsent2/3.0.4/cookieconsent.min.js"></script> 
<script> 
window.addEventListener("load", function(){ 
window.cookieconsent.initialise({ 
  "palette": { 
    "popup": { 
      "background": "#000000", 
      "text": "#cccccc" 
    }, 
    "button": { 
      "background": "#cfcfcf" 
    } 
  }, 
  "theme": "edgeless",
  "content": {
    "message": "We use cookies to personalise content and ads, to provide social media features and to analyse our traffic. We also share information about your use of our site with our social media, advertising and analytics partners who may combine it with other information you’ve provided to them or they’ve collected from your use of their services.",
    "href": privacyPolicyUri
  }  
})}); 
</script> 
')
```
