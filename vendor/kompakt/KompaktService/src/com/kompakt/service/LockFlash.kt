package com.kompakt.service

/** Kompakt: meink's white and black clearing flash at every lock, as on stock, not every eighth. */
object LockFlash {
    private const val DEGHOST = "/sys/module/meink_hal/parameters/deghost_every"

    /** Module parameters reset at every boot, so BootReceiver calls this. */
    fun apply() {
        if (!Sysfs.writable(DEGHOST)) return
        Sysfs.onIo { Sysfs.putBlocking(DEGHOST, "1\n") }
    }
}
