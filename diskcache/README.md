# diskcache

* Simple persistent disk cache utilizing an embedded derby database.
* It is not recommended to store large data chunks currently because every operation is processed in memory and not streamed to disk.
* There is no cleanup support. You might want to remove the cache directories on your own from time to time.
* There is no concurrency support yet.



--
[![Build Status](https://travis-ci.org/jjYBdx4IL/diskcache.png?branch=master)](https://travis-ci.org/jjYBdx4IL/diskcache)
devel/java/github/diskcache@7197
