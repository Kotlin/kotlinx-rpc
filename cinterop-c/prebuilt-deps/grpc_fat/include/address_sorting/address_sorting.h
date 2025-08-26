// Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.

/*	$NetBSD: getaddrinfo.c,v 1.82 2006/03/25 12:09:40 rpaulo Exp $	*/
/*	$KAME: getaddrinfo.c,v 1.29 2000/08/31 17:26:57 itojun Exp $	*/
/*
 * This is an adaptation of Android's implementation of RFC 6724
 * (in Android's getaddrinfo.c). It has some cosmetic differences
 * from Android's getaddrinfo.c, but Android's getaddrinfo.c was
 * used as a guide or example of a way to implement the RFC 6724 spec when
 * this was written.
 */

#ifndef ADDRESS_SORTING_H
#define ADDRESS_SORTING_H

#include <stddef.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef struct address_sorting_address {
  char addr[128];
  size_t len;
} address_sorting_address;

/* address_sorting_sortable represents one entry in a list of destination
 * IP addresses to sort. It contains the destination IP address
 * "sorting key", along with placeholder and scratch fields. */
typedef struct address_sorting_sortable {
  // input data; sorting key
  address_sorting_address dest_addr;
  // input data; optional value to attach to the sorting key
  void* user_data;
  // internal fields, these must be zero'd when passed to sort function
  address_sorting_address source_addr;
  bool source_addr_exists;
  size_t original_index;
} address_sorting_sortable;

void address_sorting_rfc_6724_sort(address_sorting_sortable* sortables,
                                   size_t sortables_len);

void address_sorting_init();
void address_sorting_shutdown();

struct address_sorting_source_addr_factory;

/* The interfaces below are exposed only for testing */
typedef struct {
  /* Gets the source address that would be used for the passed-in destination
   * address, and fills in *source_addr* with it if one exists.
   * Returns true if a source address exists for the destination address,
   * and false otherwise. */
  bool (*get_source_addr)(struct address_sorting_source_addr_factory* factory,
                          const address_sorting_address* dest_addr,
                          address_sorting_address* source_addr);
  void (*destroy)(struct address_sorting_source_addr_factory* factory);
} address_sorting_source_addr_factory_vtable;

typedef struct address_sorting_source_addr_factory {
  const address_sorting_source_addr_factory_vtable* vtable;
} address_sorting_source_addr_factory;

/* Platform-compatible address family types */
typedef enum {
  ADDRESS_SORTING_AF_INET,
  ADDRESS_SORTING_AF_INET6,
  ADDRESS_SORTING_UNKNOWN_FAMILY,
} address_sorting_family;

/* Indicates whether the address is AF_INET, AF_INET6, or another address
 * family. */
address_sorting_family address_sorting_abstract_get_family(
    const address_sorting_address* address);

void address_sorting_override_source_addr_factory_for_testing(
    address_sorting_source_addr_factory* factory);

bool address_sorting_get_source_addr_for_testing(
    const address_sorting_address* dest, address_sorting_address* source);

#ifdef __cplusplus
}
#endif

#endif  // ADDRESS_SORTING_H
