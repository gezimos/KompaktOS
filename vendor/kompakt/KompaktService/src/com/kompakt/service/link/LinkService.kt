package com.kompakt.service.link

import com.kompakt.service.R

import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.ParcelFileDescriptor
import android.util.Log
import java.io.EOFException
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import kotlin.concurrent.thread

const val TAG = "KompaktSerial"

/** Serves the Mudita Center protocol over the USB gadget serial port. */
class LinkService : Service() {

    private companion object {
        const val PORT = "/dev/ttyGS0"
        const val CHANNEL = "kompakt-link"

        /** Backoff between reopen attempts while USB is absent. */
        const val RETRY_MS = 2000L

        /** Quiet time after the last USB state change before the port is cycled. */
        const val SETTLE_MS = 1500L
    }

    @Volatile private var running = false

    /** The stream the serve thread is blocked on, so it can be closed from here. */
    @Volatile private var current: ParcelFileDescriptor? = null

    private val handler = Handler(Looper.getMainLooper())

    private val cycle = Runnable {
        Log.i(TAG, "usb settled, cycling the port")
        runCatching { current?.close() }
    }

    /** Reopen the port once the USB gadget has been reconfigured and settled. */
    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // Closing ttyGS0 while the host sets up ACM can panic the kernel.
            handler.removeCallbacks(cycle)
            handler.postDelayed(cycle, SETTLE_MS)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!running) {
            running = true
            // Kompakt: a plain started service.
            getSystemService(NotificationManager::class.java)?.deleteNotificationChannel(CHANNEL)
            // USB_STATE is a protected system broadcast; NOT_EXPORTED because nothing outside the system should be able to.
            registerReceiver(
                usbReceiver,
                IntentFilter("android.hardware.usb.action.USB_STATE"),
                Context.RECEIVER_NOT_EXPORTED,
            )
            serve()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        running = false
        handler.removeCallbacks(cycle)
        runCatching { unregisterReceiver(usbReceiver) }
        runCatching { current?.close() }
    }

    /** Own the port for as long as the service lives. */
    private fun serve() = thread(name = "kompakt-link", isDaemon = true) {
        var complained = false
        while (running) {
            val f = File(PORT)
            if (!f.exists()) {
                // Normal before the gadget is composed; say it once, not every two seconds forever.
                if (!complained) {
                    Log.i(TAG, "$PORT is not there yet, waiting")
                    complained = true
                }
                sleep(RETRY_MS)
                continue
            }
            complained = false

            try {
                // Tty.open, not FileInputStream: the node has to be put in raw mode before a single byte of this protocol works.
                val pfd = Tty.open(PORT)
                if (pfd == null) {
                    sleep(RETRY_MS)
                    continue
                }
                pfd.use {
                    current = it
                    val input = FileInputStream(it.fileDescriptor)
                    val output = FileOutputStream(it.fileDescriptor)
                    Log.i(TAG, "port open and raw, serving")
                    pump(input, output)
                }
            } catch (e: EOFException) {
                Log.i(TAG, "host went away (${e.message})")
            } catch (t: Throwable) {
                // SELinux denial, EBUSY while the gadget reconfigures, a host that closed mid-write.
                Log.w(TAG, "port error: ${t.javaClass.simpleName}: ${t.message}")
            }
            current = null
            sleep(RETRY_MS)
        }
    }

    /** One request, one reply, until the port closes. */
    private fun pump(input: InputStream, output: OutputStream) {
        while (running) {
            val request = Frame.read(input)
            val reply = try {
                Api.handle(this, request)
            } catch (t: Throwable) {
                // Never let one bad request kill the connection.
                Log.e(TAG, "handler threw", t)
                continue
            }
            Frame.write(output, reply)
        }
    }

    private fun sleep(ms: Long) = try {
        Thread.sleep(ms)
    } catch (e: InterruptedException) {
        Thread.currentThread().interrupt()
    }

}
