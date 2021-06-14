# vmmgmt-utils

## Debian 10

* apt install qemu-kvm libvirt-clients libvirt-daemon-system bridge-utils virtinst libvirt-daemon virt-manager -y
* adduser build libvirt
* adduser build libvirt-qemu
* reboot

QEMU+libvirt works perfectly fine in a VirtualBox 6.1.22 AMD-v enabled Debian 10 amd64 guest under Windows 10/amd64. 

Virt-manager GUI: add QEMU *user* session. Then you'll see the VM.

## Misc

* https://github.com/libvirt/libvirt/tree/master/tests/qemuxml2argvdata
* https://libvirt.org/formatdomain.html#network-interfaces
** there seems to be no explicit template support for port forwarding, so we have to set up the entire NIC on our own
via the argument option.
* https://www.linuxtechi.com/install-configure-kvm-debian-10-buster/
