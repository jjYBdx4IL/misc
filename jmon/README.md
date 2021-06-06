# JMon - a simple Nagios replacement

## Installation

No CA. Trust is based on deploying truststores manually. For that reason, validity period length isn't really an issue.

Server key:

    server jmon-cfgdir$ keytool -keystore keystore -storepass password -keypass password \
        -alias server -genkeypair -keyalg rsa -keysize 4096 -validity 12000 -dname CN=jmon.server.com

Put the server cert into the clients' truststores:

    server jmon-cfgdir$ keytool -keystore keystore -storepass password -keypass password \
        -alias server -export -file server.cert
    # for all clients:
    server jmon-cfgdir$ scp server.cert clientX:jmon-cfgdir/.
    clientX jmon-cfgdir$ keytool -keystore truststore -storepass password -keypass password -noprompt \
        -alias server-cert -import -file server.cert

Generate client keys identifying each client. The CN part of the certificate subject will be treated as the
hostname when submitting passive server check updates. It doesn't need to match your client's real address/domain.

    clientX jmon-cfgdir$ keytool -keystore keystore -storepass password -keypass password \
        -alias client -genkeypair -keyalg rsa -keysize 4096 -validity 12000 -dname CN=clientX.com
    
Add each client's certificate to the server's truststore:

    clientX jmon-cfgdir$ keytool -keystore keystore -storepass password -keypass password \
        -alias client -export -file clientX.cert
    clientX jmon-cfgdir$ scp clientX.cert server:jmon-cfgdir/.
    # re-import on the server:
    server jmon-cfgdir$ keytool -keystore truststore -storepass password -keypass password -noprompt \
        -alias clientX-cert -import -file clientX.cert

## Concurrency Model

* Concurrency starts after loading the definitions/configuration.
* Passive checks are handled by the RequestHandler.
* Active checks are handled by CheckThread.
* Synchronization is based on ServiceState instances. As long as there is only one CheckThread, this
synchronization is superfluous.
* The NotificationThread works on a ConcurrentHashMap that stores active check problems. The synchronization
is done via the ConcurrentHashMap only. The NotificationThread does not access the ServiceState directly. The
thread working on the ServiceState has to construct the problem report while holding the ServiceState instance
lock.
* HostState currently needs no synchronization because there is only one thing that can happen to it: after having
obtained the remote host's name during passive check submission (RequestHandler), there is an issue with the reported
passive check result. In that case, the HostState will get a status != 0.
* Host states include all of the host's service states. Each host's state get written to "hostname.state"
every N minutes after the first unsaved change (currently the save only happens when either a passive check
is submitted to the same host or the dirty flag on the host state is older than N minutes upon active check state
change). Status updates to passive checks trigger state saves immediately
within the request to provide transactional safety for passive check results that have a longer update interval.
Concurrency is achieved by locking the HostState instance. In order to ensure consistency, that also must imply
a lock over all contained service states, ie. the following lock ordering is hereby introduced:

    HostState -> ServiceState.

Each ServiceState modification must also get a HostState lock. Beware: the service state lock
can be held without holding the HostState lock as long as the service state does not get updated. This is also
a requirement to avoid externally provoked active check delays from blocking the processing.