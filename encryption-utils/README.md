# encryption-utils

[![Build Status](https://travis-ci.org/jjYBdx4IL/misc.png?branch=master)](https://travis-ci.org/jjYBdx4IL/misc)

## DISCLAIMER

I'm no crypto expert. Use at your own risk only and only if you know exactly what you are doing!
This is available for free, and you know, there is no free lunch. All these people using OpenSSL
and Debian for years and noone ever discovering blatant security holes, that should tell you
something.

## REQUIREMENTS

Default JRE/JDK installations won't be able to run this library. You need to install the JCE to support strong
encryption levels.

## HOW IT WORKS

Currently, there is an EncryptedInputStream and a corresponding EncryptedOutputStream. The generated output stream
contains a serialized EncryptedStreamHeader object containing SALT and INIT VECTOR for decryption, and is followed
by the encrypted data. The data written to EncryptedOutputStream is wrapped in a GZIPOutputStream for compression and,
most importantly, to have a decompression/password integrity check. The fact that this encrypted GZIP stream has a header
containing a header checksum also makes the decryption fail fast in case of a bad password -- as opposed to failing
after having decrypted all data, or potentially not failing at all if no GZIPOutputStream would be used at all. There
are often consistency checks at the encryption level, but they are usually not reliable and by default encryption
algorithms do not seem to care about decryption validation by themselves.

The EncryptedStreamHeader could also be easily extended to contain more data, like compression type, different
compression algorithms, different key types and lengths etc.

## MAYDOs

* add seekable encrypted files support with intermediary keys to allow for quick password changes
