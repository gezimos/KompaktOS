/* aee_core_forwarder - restore usermodehelper on the Mudita Kompakt. */

#include <stdio.h>
#include <unistd.h>

int main(int argc, char **argv)
{
    /* argv[0] is us; argv[1] is the program the kernel actually wanted. */
    if (argc < 2) {
        return 0;
    }

    execv(argv[1], &argv[1]);

    /* Only reached if the exec failed. */
    return 0;
}
