SUMMARY = "Linux kernel for LS1046A gateway board"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

inherit kernel

DEPENDS += "bison-native flex-native bc-native"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

LINUX_VERSION = "6.12.34"
PV = "${LINUX_VERSION}+git${SRCPV}"

LINUX_QORIQ_BRANCH = "lf-6.12.y"
LINUX_QORIQ_SRC = "git://github.com/nxp-qoriq/linux.git;protocol=https"

SRC_URI = "${LINUX_QORIQ_SRC};branch=${LINUX_QORIQ_BRANCH} \
            file://defconfig \
            file://ls1046a-gateway-dk.dts \
            "

SRCREV = "be78e49cb4339fd38c9a40019df49b72fbb8bcb7"

S = "${WORKDIR}/git"

do_configure:prepend() {
    cp ${WORKDIR}/defconfig ${B}/.config
    cp ${WORKDIR}/ls1046a-gateway-dk.dts ${S}/arch/arm64/boot/dts/freescale/ls1046a-gateway-dk.dts
    echo "dtb-y += ls1046a-gateway-dk.dtb" >> ${S}/arch/arm64/boot/dts/freescale/Makefile
}
