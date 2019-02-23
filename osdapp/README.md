# OSD-APP

Version: ${my.release.version}



## Homepage / Download

https://webtools.leponceau.org



## HOW TO RUN

```
--- START: COMMAND EXAMPLE ---

./${project.artifactId} \
    --orientation CENTER_TOP -w 800 \
    --disable-cert-checks \
    --nagios-url ${url.nagios.doc} \
    --jenkins-url ${url.jenkins.doc} \
    --jenkins-wol \
    --wol eth0 \
    --diskfree /:512:1024 \
    --diskfree /usr:512:1024 \
    --netdev eth0 \
    --netdev-wol 32768 \
    --zfs --zfs-wol \
    --disk-standby sda,5 --disk-standby-script <standby.sh> \

--- END: COMMAND EXAMPLE ---
```

Run

    ./${project.artifactId} --help

to show a short description of all available command line parameters.

Have a look at properties.xml if you need to supply credentials to access your nagios status page.
You will have to copy it to `~/.config/java-osd/properties.xml`.



## Jenkins/Hudson Monitoring

Hint: to configure which jobs should be monitored, set up a specific monitoring view and point this application
to it using the `--jenkins-url` argument (do not append the `/app/xml` part to the url).



## Wake-On-LAN (Keep-Alive)

The WoL functionality is intended to be used in conjunction with utilities like powernap: powernap is
monitoring a machine for inactivity and you can configure it to listen to WoL ports. If it receives WoL
packets, it will consider those as activity, thereby preventing powernap from shutting down the machine.
In other words: the WoL packets sent out by the program are used as keep-alive pings to keep the host machine
running.

The Jenkins WoL feature looks for active Jenkins build jobs. The netdev WoL feature looks for any network
device with spike throughput above the given value. The ZFS WoL feature looks for
scrubbing/scanning/resilvering activity.



## Plugin

```
--- START: PLUGIN COMMAND EXAMPLE ---

./${project.artifactId} --orientation CENTER_TOP -w 800 \
    --plugin-exe <some-script-or-executable> \
    --plugin-ival 60 \
    --plugin-wol

--- END: PLUGIN COMMAND EXAMPLE ---
```



## Disk Standby

    ./${project.artifactId} --disk-standby sda,5

checks `/proc/diskstats` for disk activity on `/dev/sda`. If there is no activity for 5 or more minutes,
the command `sudo hdparm -y /dev/sda` will be issued. A corresponding `sudoers` entry is required. This feature
may be useful in the many situations where setting the drive's spindown timeout does not work.



## BUG REPORTS, SOURCE CODE

https://github.com/jjYBdx4IL/misc



## FEATURE REQUESTS

If you have the need for a specific monitoring extension to this app, send us an inquiry. If your request
is generic enough, we might consider implementing it for free.

