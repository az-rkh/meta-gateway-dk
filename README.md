# meta-gateway-dk

A Yocto BSP layer for the **LS1046A Gateway Development Kit**, an embedded gateway board based on the NXP LS1046A (QorIQ, quad-core Cortex-A72). This layer produces a minimal recovery initramfs used for rescue, provisioning, and factory operations.

---

## Hardware

| Property | Value |
|---|---|
| SoC | NXP LS1046A (QorIQ) |
| CPU | Quad-core Arm Cortex-A72 |
| Architecture | AArch64 (ARMv8-A) |
| Serial console | `ttyS0` / `ttyAMA0` @ 115200 baud |
| Kernel image | `Image.gz` |
| Device tree | `freescale/ls1046a-gateway.dk.dtb` |

---

## What This Layer Builds

A compressed initramfs (`cpio.gz`) bundled with the kernel, containing:

- BusyBox userspace (init, shell, core utilities)
- Networking: `curl`, `wget`
- Storage tools: `parted`, `fdisk`, `lsblk`, `blkid`, `e2fsprogs`, `mmc-utils`, `mtd-utils`
- Hardware debug: `i2c-tools`, `kmod`, `udev`
- No systemd, no package manager, no root password

---

## Layer Structure

```
meta-gateway-dk/
├── conf/
│   ├── layer.conf               # Layer registration (scarthgap, priority 10)
│   ├── machine/
│   │   └── gateway-dk.conf      # Machine definition for NXP LS1046A
│   └── distro/
│       └── recovery.conf        # Minimal distro (glibc, BusyBox init)
├── kas/
│   └── firmware.yaml            # KAS manifest for reproducible builds
├── recipes-core/
│   └── images/
│       └── recovery-image.bb    # Initramfs image recipe
└── recipes-kernel/
    └── linux/
        ├── linux-ls1046a_6.12.bb   # Kernel recipe (NXP QorIQ fork, v6.12.34)
        └── files/
            ├── defconfig        # Kernel config
            └── ls1046a-gateway-dk.dts  # Custom device tree source
```

---

## Dependencies

| Dependency | Branch / Version |
|---|---|
| openembedded-core | `scarthgap` |
| bitbake | `2.8` |

---

## Building

This layer uses [KAS](https://kas.readthedocs.io) for reproducible builds. KAS fetches all dependencies automatically.

**Install KAS:**

```bash
pip install kas
```

**Build the firmware:**

```bash
kas build kas/firmware.yaml
```

The output will be at:

```
build/tmp/deploy/images/gateway-dk/
```

Look for `Image.gz` — this is the kernel with the initramfs bundled inside.

---

## Kernel

- **Source:** NXP QorIQ Linux fork (`github.com/nxp-qoriq/linux`, branch `lf-6.12.y`)
- **Version:** 6.12.34
- **Pinned revision:** `be78e49cb4339fd38c9a40019df49b72fbb8bcb7`
- **Custom DTS:** `ls1046a-gateway-dk.dts` is injected into `arch/arm64/boot/dts/freescale/` at build time

To update the kernel revision, change `SRCREV` in [recipes-kernel/linux/linux-ls1046a_6.12.bb](recipes-kernel/linux/linux-ls1046a_6.12.bb).

---

## Distro

The `recovery` distro ([conf/distro/recovery.conf](conf/distro/recovery.conf)) is intentionally stripped down:

- C library: `glibc`
- Init system: BusyBox init (no systemd)
- No GUI, no D-Bus, no extra distro features
- Maintainer: Tomaz Zaman `<tomaz@mono.si>`

---

## Notes

- `conf/site.conf` is excluded from version control — it holds machine-local path overrides
- `build/` and `sources/` are excluded — fully reproducible via `kas build`
- The initramfs has no root password by design (`empty-root-password` image feature)
