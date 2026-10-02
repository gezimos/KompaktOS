/* Open a tty in raw mode and hand the fd back. */
#include <errno.h>
#include <fcntl.h>
#include <jni.h>
#include <string.h>
#include <termios.h>
#include <unistd.h>

#include <android/log.h>

#define TAG "KompaktSerial"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

/* Returns the fd, or -errno so the caller can log why rather than just that. */
JNIEXPORT jint JNICALL
Java_com_kompakt_service_link_Tty_openRaw(JNIEnv *env, jobject thiz, jstring path_)
{
    const char *path = (*env)->GetStringUTFChars(env, path_, NULL);
    if (path == NULL) {
        return -ENOMEM;
    }

    int fd = open(path, O_RDWR | O_NOCTTY);
    if (fd < 0) {
        int err = errno;
        LOGE("openRaw: open(%s) failed: %s", path, strerror(err));
        (*env)->ReleaseStringUTFChars(env, path_, path);
        return -err;
    }

    struct termios tio;
    if (tcgetattr(fd, &tio) != 0) {
        /* Not fatal on its own -- start from a cleared struct and set it all. */
        LOGE("openRaw: tcgetattr failed: %s", strerror(errno));
        memset(&tio, 0, sizeof(tio));
    }

    /* The call this file is for: clears ECHO, ECHONL, ICANON, ISIG, IEXTEN, and turns off output post-processing. */
    cfmakeraw(&tio);

    /* Center opens its end at 9600. */
    cfsetispeed(&tio, B9600);
    cfsetospeed(&tio, B9600);

    tio.c_cflag |= (CLOCAL | CREAD);
    /* Block until at least one byte, then return what is there. */
    tio.c_cc[VMIN] = 1;
    tio.c_cc[VTIME] = 0;

    if (tcsetattr(fd, TCSANOW, &tio) != 0) {
        int err = errno;
        LOGE("openRaw: tcsetattr failed: %s", strerror(err));
        close(fd);
        (*env)->ReleaseStringUTFChars(env, path_, path);
        return -err;
    }

    /* Anything the host sent while the line was still cooked is not a frame we can trust the alignment of. */
    tcflush(fd, TCIOFLUSH);

    LOGI("openRaw: %s is raw, fd=%d", path, fd);
    (*env)->ReleaseStringUTFChars(env, path_, path);
    return fd;
}
