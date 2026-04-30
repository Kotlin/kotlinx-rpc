/*
 * KRPC-597: in-process crash backtrace shim, loaded via LD_PRELOAD (Linux)
 * or DYLD_INSERT_LIBRARIES (macOS) into Kotlin/Native test binaries.
 *
 * On SIGABRT (the "pure virtual method called" -> libstdc++ verbose terminate
 * -> abort path), SIGSEGV, or SIGBUS the shim prints a per-thread backtrace
 * and a memory map (Linux: /proc/self/maps) to stderr, then re-raises the
 * signal under the default handler so the process still terminates with the
 * original disposition.
 *
 * Build (per-platform, done by the :buildCrashbtShim Gradle task):
 *   Linux:  gcc -O0 -g -fPIC -shared -o libcrashbt.so scripts/crashbt.c -ldl
 *   macOS:  clang -O0 -g -dynamiclib -o libcrashbt.dylib scripts/crashbt.c
 *
 * Use:
 *   Linux:  LD_PRELOAD=$PWD/libcrashbt.so ./test.kexe
 *   macOS:  DYLD_INSERT_LIBRARIES=$PWD/libcrashbt.dylib \
 *           DYLD_FORCE_FLAT_NAMESPACE=1 ./test.kexe
 *
 * Symbol resolution: backtrace_symbols_fd prints "binary(symbol+offset) [addr]"
 * - Linux: for unresolved frames, run
 *     addr2line -fpe test.kexe <addr>
 *   against the captured /proc/self/maps to translate runtime addresses.
 * - macOS: for unresolved frames, run
 *     atos -o test.kexe -l <slide> <addr>
 *   where <slide> is the image base for the kexe (printed in the dyld dump).
 *
 * Why not gdb-batch? Kotlin/Native's runtime installs its own
 * SIGSEGV-as-safepoint and SIGBUS handlers during init; gdb's ptrace intercept
 * of those signals breaks the runtime's invariants and every iteration fails
 * fast (rc=1, no actual run -- TC build #54004 confirms). An in-process
 * sigaction handler installed via __attribute__((constructor)) doesn't perturb
 * execution -- it just adds one extra sigaction() call before main().
 */

#define _GNU_SOURCE
#include <execinfo.h>
#include <fcntl.h>
#include <pthread.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#if defined(__APPLE__)
#include <mach-o/dyld.h>
#elif defined(__linux__)
#include <sys/syscall.h>
#endif

static void write_str(int fd, const char *s) {
    size_t n = strlen(s);
    while (n) {
        ssize_t w = write(fd, s, n);
        if (w <= 0) return;
        s += w;
        n -= (size_t)w;
    }
}

static long current_tid(void) {
#if defined(__APPLE__)
    uint64_t tid = 0;
    pthread_threadid_np(NULL, &tid);
    return (long)tid;
#elif defined(__linux__)
    return (long)syscall(SYS_gettid);
#else
    return 0;
#endif
}

#if defined(__linux__)
static void dump_maps(int fd) {
    int maps = open("/proc/self/maps", O_RDONLY);
    if (maps < 0) return;
    char buf[4096];
    ssize_t r;
    while ((r = read(maps, buf, sizeof(buf))) > 0) {
        ssize_t off = 0;
        while (off < r) {
            ssize_t w = write(fd, buf + off, (size_t)(r - off));
            if (w <= 0) break;
            off += w;
        }
    }
    close(maps);
}
#elif defined(__APPLE__)
/* macOS has no /proc; the equivalent in-process info is the dyld image list.
 * Print "image_base name" for each loaded Mach-O image so `atos -l <base>`
 * can resolve unresolved frames. */
static void dump_maps(int fd) {
    uint32_t count = _dyld_image_count();
    char line[1024];
    for (uint32_t i = 0; i < count; i++) {
        const char *name = _dyld_get_image_name(i);
        intptr_t slide = _dyld_get_image_vmaddr_slide(i);
        int n = snprintf(line, sizeof(line), "%016lx %s\n",
                         (unsigned long)slide, name ? name : "(null)");
        if (n > 0) write(fd, line, (size_t)n);
    }
}
#else
static void dump_maps(int fd) { (void)fd; }
#endif

static void handler(int sig, siginfo_t *info, void *ucontext) {
    (void)ucontext;
    char hdr[160];
    int n = snprintf(hdr, sizeof(hdr),
                     "\n=== crashbt: signal %d (si_code=%d, si_addr=%p) tid=%ld ===\n",
                     sig, info ? info->si_code : 0,
                     info ? info->si_addr : (void *)0, current_tid());
    if (n > 0) write(STDERR_FILENO, hdr, (size_t)n);

    void *frames[128];
    int nframes = backtrace(frames, 128);
    write_str(STDERR_FILENO, "=== backtrace (crashing thread) ===\n");
    backtrace_symbols_fd(frames, nframes, STDERR_FILENO);

#if defined(__linux__)
    write_str(STDERR_FILENO, "\n=== /proc/self/maps ===\n");
#elif defined(__APPLE__)
    write_str(STDERR_FILENO, "\n=== dyld images (slide path) ===\n");
#endif
    dump_maps(STDERR_FILENO);
    write_str(STDERR_FILENO, "=== end crashbt ===\n");

    /* Restore default handler and re-raise so the process exits with the
     * original signal disposition (e.g. SIGABRT exit code, core if enabled). */
    signal(sig, SIG_DFL);
    raise(sig);
}

__attribute__((constructor))
static void crashbt_install(void) {
    struct sigaction sa;
    memset(&sa, 0, sizeof(sa));
    sa.sa_sigaction = handler;
    sigemptyset(&sa.sa_mask);
    sa.sa_flags = SA_SIGINFO | SA_NODEFER | SA_RESETHAND;

    /* SIGABRT is the primary target (pure-virtual abort path). SIGSEGV and
     * SIGBUS are best-effort: K/N's runtime may override them post-init for
     * its own memory manager / safepoints, in which case our handler is
     * replaced and we don't get a backtrace for those -- but the historical
     * KRPC-604 bug surfaces as SIGABRT, so coverage of that signal is what
     * matters. */
    sigaction(SIGABRT, &sa, NULL);
    sigaction(SIGSEGV, &sa, NULL);
    sigaction(SIGBUS,  &sa, NULL);
}
