/*
 * KRPC-604: tiny LD_PRELOAD shim that prints a backtrace + /proc/self/maps to
 * stderr when the test process receives SIGABRT (the "pure virtual method called"
 * → libstdc++ verbose terminate → abort path), SIGSEGV, or SIGBUS. After
 * printing it re-raises the signal under the default handler so the process
 * still terminates with the original signal code.
 *
 * Build (inside the agent / docker container):
 *   gcc -O0 -g -fPIC -shared -o libcrashbt.so scripts/crashbt.c -ldl
 *
 * Use:
 *   LD_PRELOAD=$PWD/libcrashbt.so ./test.kexe --ktest_filter=...
 *
 * Symbol resolution: backtrace_symbols_fd prints "binary(symbol+offset) [addr]"
 * — for unresolved frames, run `addr2line -fpe test.kexe <addr>` against the
 * captured /proc/self/maps to translate runtime addresses to source lines.
 *
 * Why not gdb-batch? K/N's runtime installs its own SIGSEGV-as-safepoint and
 * SIGBUS handlers during init; gdb's ptrace intercept of those signals breaks
 * the runtime's invariants and every test fails fast (rc=1, no actual run).
 * An LD_PRELOAD'd in-process handler doesn't perturb execution at all — it's
 * just an extra sigaction() call in __attribute__((constructor)).
 */

#define _GNU_SOURCE
#include <execinfo.h>
#include <fcntl.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

static void write_str(int fd, const char *s) {
    size_t n = strlen(s);
    while (n) {
        ssize_t w = write(fd, s, n);
        if (w <= 0) return;
        s += w;
        n -= (size_t)w;
    }
}

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

static void handler(int sig, siginfo_t *info, void *ucontext) {
    (void)ucontext;
    char hdr[160];
    int n = snprintf(hdr, sizeof(hdr),
                     "\n=== crashbt: signal %d (si_code=%d, si_addr=%p) tid=%ld ===\n",
                     sig, info ? info->si_code : 0,
                     info ? info->si_addr : (void *)0, (long)gettid());
    if (n > 0) write(STDERR_FILENO, hdr, (size_t)n);

    void *frames[128];
    int nframes = backtrace(frames, 128);
    write_str(STDERR_FILENO, "=== backtrace (crashing thread) ===\n");
    backtrace_symbols_fd(frames, nframes, STDERR_FILENO);

    write_str(STDERR_FILENO, "\n=== /proc/self/maps ===\n");
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

    /* Only hook signals that K/N's runtime is unlikely to claim for itself.
     * SIGABRT is the actual target (pure-virtual → abort path). SIGSEGV and
     * SIGBUS are best-effort: K/N may override them post-init for its own
     * memory manager / safepoints, in which case our handler is replaced and
     * we just don't get a bt for those — but the bug we're hunting is
     * SIGABRT, so it doesn't matter. */
    sigaction(SIGABRT, &sa, NULL);
    sigaction(SIGSEGV, &sa, NULL);
    sigaction(SIGBUS,  &sa, NULL);
}
