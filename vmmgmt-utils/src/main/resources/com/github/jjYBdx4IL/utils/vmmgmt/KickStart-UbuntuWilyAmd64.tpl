#
#Generic Kickstart template for Ubuntu
#Platform: x86 and x86-64
#

#System language
lang en_US

#Language modules to install
langsupport en_US

#System keyboard
keyboard de

#System mouse
mouse

#System timezone
#timezone America/New_York
timezone --utc America/New_York

#Root password
#rootpw --disabled
rootpw rootpass

#Initial user (user with sudo capabilities)
user ${initialUser} --fullname "Ubuntu User" --password ${initialUserPwd}

#Reboot after installation
poweroff

#Use text mode install
text

#Install OS instead of upgrade
install

#Installation media
cdrom
#nfs --server=server.com --dir=/path/to/ubuntu/

#System bootloader configuration
bootloader --location=mbr

#Clear the Master Boot Record
zerombr yes

#Partition clearing information
clearpart --all --initlabel

#Basic disk partition
part / --fstype ext4 --size 1 --grow --asprimary
part swap --size 1024
part /boot --fstype ext4 --size 256 --asprimary

#Advanced partition
#part /boot --fstype=ext4 --size=500 --asprimary
#part pv.aQcByA-UM0N-siuB-Y96L-rmd3-n6vz-NMo8Vr --grow --size=1
#volgroup vg_mygroup --pesize=4096 pv.aQcByA-UM0N-siuB-Y96L-rmd3-n6vz-NMo8Vr
#logvol / --fstype=ext4 --name=lv_root --vgname=vg_mygroup --grow --size=10240 --maxsize=20480
#logvol swap --name=lv_swap --vgname=vg_mygroup --grow --size=1024 --maxsize=8192

#System authorization infomation
auth  --useshadow  --enablemd5

#Network information
network --bootproto=dhcp --device=eth0

#Firewall configuration
firewall --disabled --trust=eth0 --ssh

#Do not configure the X Window System
skipx

%pre

%post
echo buildvm > /etc/hostname
echo "127.0.1.2 buildvm" >> /etc/hosts
sed -i /etc/apt/sources.list -e "s_^deb http\\S* wily_deb http://de.archive.ubuntu.com/ubuntu/ wily_"
sed -i /etc/apt/sources.list -e "s_^deb-src http\\S* wily_deb-src http://de.archive.ubuntu.com/ubuntu/ wily_"
apt-get install -y openssh-server vim
sed -i /etc/ssh/sshd_config -e 's;^PermitRootLogin.*;PermitRootLogin yes;'
sed -i /etc/ssh/sshd_config -e 's;^PermitEmptyPasswords.*;PermitEmptyPasswords yes;'
sed -i 's/nullok_secure/nullok/' /etc/pam.d/common-auth
echo "#!/bin/bash" > /etc/rc.local
echo "passwd -d root" >> /etc/rc.local
echo "GRUB_TERMINAL=console" >> /etc/default/grub
echo "GRUB_TIMEOUT=1" >> /etc/default/grub
echo "GRUB_CMDLINE_LINUX_DEFAULT=" >> /etc/default/grub
update-grub
sed -i /etc/fstab -e 's:errors=remount-ro:errors=remount-ro,discard,noatime:'
echo "auto eth0" >> /etc/network/interfaces
echo "iface eth0 inet dhcp" >> /etc/network/interfaces
fstrim /
