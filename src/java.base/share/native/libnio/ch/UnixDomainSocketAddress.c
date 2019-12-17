/*
 * Copyright (c) 1997, 2016, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

#include <string.h>

#include "java_nio_channels_UnixDomainSocketAddress.h"
#include "net_util.h"

/************************************************************************
 * UnixDomainSocketAddress
 */

jclass udsa_class;
jmethodID udsa_ctorID;
jfieldID udsa_pathID;

static int udsa_initialized = 0;

/*
 * Class:     java_nio_channels_UnixDomainSocketAddress
 * Method:    init
 * Signature: ()V
 */
JNIEXPORT void JNICALL
Java_java_nio_channels_UnixDomainSocketAddress_init(JNIEnv *env, jclass dontuse) {
    if (!udsa_initialized) {
        jclass c = (*env)->FindClass(env,"java/nio/channels/UnixDomainSocketAddress");
        CHECK_NULL(c);
        udsa_class = (*env)->NewGlobalRef(env, c);
        CHECK_NULL(udsa_class);

        udsa_pathID = (*env)->GetFieldID(env, udsa_class, "pathname", "Ljava/lang/String;");
        CHECK_NULL(udsa_pathID);

        udsa_ctorID = (*env)->GetMethodID(env, udsa_class, "<init>", "(Ljava/lang/String;)V");
        CHECK_NULL(udsa_ctorID);

        udsa_initialized = 1;
    }
}
