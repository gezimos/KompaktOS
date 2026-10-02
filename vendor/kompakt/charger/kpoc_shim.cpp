// Symbols Mudita's kpoc_charger and libshowlogo.so (built for Android 12)
// import that Android 16 no longer exports under the same mangled names:
// libbase now takes std::string_view in Trim, Basename and WriteStringToFd,
// and libui made Size::INVALID constexpr. Each one is defined here with the
// old signature; kpoc_charger links this library directly (patch-needed.py).
//
// No libbase or libui headers on purpose: they declare the new signatures,
// and these have to be the old ones. borrowed_fd was a class holding one int,
// passed by value in a register, so a one-int struct matches it.

#include <string>

#include <ctype.h>
#include <errno.h>
#include <unistd.h>

namespace android {

namespace base {

struct borrowed_fd {
    int fd_;
};

std::string Trim(const std::string& s) {
    size_t start = 0;
    size_t end = s.size();
    while (start < end && isspace(static_cast<unsigned char>(s[start]))) start++;
    while (end > start && isspace(static_cast<unsigned char>(s[end - 1]))) end--;
    return s.substr(start, end - start);
}

std::string Basename(const std::string& path) {
    std::string p = path;
    while (p.size() > 1 && p.back() == '/') p.pop_back();
    size_t slash = p.find_last_of('/');
    if (slash == std::string::npos || p.size() == 1) return p;
    return p.substr(slash + 1);
}

bool WriteStringToFd(const std::string& content, borrowed_fd fd) {
    const char* p = content.data();
    size_t left = content.size();
    while (left > 0) {
        ssize_t n = write(fd.fd_, p, left);
        if (n == -1) {
            if (errno == EINTR) continue;
            return false;
        }
        p += n;
        left -= n;
    }
    return true;
}

}  // namespace base

namespace ui {

struct Size {
    int width;
    int height;
    static const Size INVALID;
};

const Size Size::INVALID{-1, -1};

}  // namespace ui

}  // namespace android
