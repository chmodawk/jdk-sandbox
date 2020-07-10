/*
 * Copyright (c) 2020, Red Hat, Inc. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
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

/*
 * @test
 * @summary Test for Runtime.fieldOffsetOf
 *
 * @run main/othervm -XX:+CheckIntrinsics    FieldOffsetOf
 * @run main/othervm -Xint                   FieldOffsetOf
 * @run main/othervm -XX:TieredStopAtLevel=1 FieldOffsetOf
 * @run main/othervm -XX:TieredStopAtLevel=2 FieldOffsetOf
 * @run main/othervm -XX:TieredStopAtLevel=3 FieldOffsetOf
 * @run main/othervm -XX:TieredStopAtLevel=4 FieldOffsetOf
 * @run main/othervm -XX:-TieredCompilation  FieldOffsetOf
 */

import java.lang.reflect.Field;

public class FieldOffsetOf {

    public static void main(String ... args) throws Exception {
        Field f = Integer.class.getDeclaredField("value");
        for (int c = 0; c < 100000; c++) {
           assertEquals(Runtime.fieldOffsetOf(f), 12);
        }
    }

    public static void assertEquals(long expected, long actual) {
        if (expected != actual) {
            throw new IllegalStateException("Error: expected: " + expected + ", actual: " + actual);
        }
    }

}
