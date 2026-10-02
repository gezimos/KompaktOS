/* uinput device for the fingerprint sensor's gesture keys, visible to accessibility services. */
#include <errno.h>
#include <fcntl.h>
#include <jni.h>
#include <string.h>
#include <sys/ioctl.h>
#include <unistd.h>

#include <linux/input.h>
#include <linux/uinput.h>

#include <android/log.h>

#define TAG "KompaktButtons"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)

/* Keys with no phone action that remapping apps know; order matches Buttons.kt. */
static const int kButtons[] = { KEY_RED, KEY_GREEN, KEY_YELLOW, KEY_F13, KEY_F14 };
#define BUTTON_COUNT ((int) (sizeof(kButtons) / sizeof(kButtons[0])))

/* Returns the device fd, or -errno. */
JNIEXPORT jint JNICALL
Java_com_kompakt_service_Buttons_create(JNIEnv *env, jobject thiz)
{
    int fd = open("/dev/uinput", O_WRONLY | O_NONBLOCK | O_CLOEXEC);
    if (fd < 0) {
        int err = errno;
        LOGE("open /dev/uinput failed: %s", strerror(err));
        return -err;
    }
    if (ioctl(fd, UI_SET_EVBIT, EV_KEY) < 0) goto fail;
    for (int i = 0; i < BUTTON_COUNT; i++) {
        if (ioctl(fd, UI_SET_KEYBIT, kButtons[i]) < 0) goto fail;
    }

    struct uinput_setup setup;
    memset(&setup, 0, sizeof(setup));
    setup.id.bustype = BUS_VIRTUAL;
    setup.id.vendor = 0x4b4d; /* "KM" */
    setup.id.product = 0x0001;
    strncpy(setup.name, "Kompakt fingerprint buttons", UINPUT_MAX_NAME_SIZE - 1);
    if (ioctl(fd, UI_DEV_SETUP, &setup) < 0) goto fail;
    if (ioctl(fd, UI_DEV_CREATE) < 0) goto fail;
    LOGI("created");
    return fd;

fail: {
        int err = errno;
        LOGE("uinput setup failed: %s", strerror(err));
        close(fd);
        return -err;
    }
}

static int emit(int fd, int type, int code, int value)
{
    struct input_event ev;
    memset(&ev, 0, sizeof(ev));
    ev.type = type;
    ev.code = code;
    ev.value = value;
    return write(fd, &ev, sizeof(ev)) == (ssize_t) sizeof(ev) ? 0 : -errno;
}

/* index into kButtons; down true to press, false to release. Returns 0 or -errno. */
JNIEXPORT jint JNICALL
Java_com_kompakt_service_Buttons_press(JNIEnv *env, jobject thiz, jint fd, jint index, jboolean down)
{
    if (index < 0 || index >= BUTTON_COUNT) return -EINVAL;
    int r = emit(fd, EV_KEY, kButtons[index], down ? 1 : 0);
    if (r == 0) r = emit(fd, EV_SYN, SYN_REPORT, 0);
    if (r != 0) LOGE("write failed: %s", strerror(-r));
    return r;
}
