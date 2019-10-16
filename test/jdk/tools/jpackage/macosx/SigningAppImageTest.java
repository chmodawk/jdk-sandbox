/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
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

import java.nio.file.Path;
import jdk.jpackage.test.JPackageCommand;
import jdk.jpackage.test.TKit;

/**
 * Tests generation of app image with --mac-sign and related arguments. Test will
 * generate app image and verify signature of main launcher and app bundle itself.
 * This test requires that machine is configured with test certificate for
 * "Developer ID Application: jpackage.openjdk.java.net" in jpackagerTest keychain with
 * always allowed access to this keychain for user which runs test.
 */

/*
 * @test
 * @summary jpackage with --package-type app-image --mac-sign
 * @library ../helpers
 * @library /test/lib
 * @library base
 * @build SigningBase
 * @build SigningCheck
 * @build jtreg.SkippedException
 * @build jdk.jpackage.test.*
 * @modules jdk.jpackage/jdk.jpackage.internal
 * @requires (os.family == "mac")
 * @run main/othervm -Xmx512m SigningAppImageTest
 */
public class SigningAppImageTest {

    public static void main(String[] args) throws Exception {
        TKit.run(args, () -> {
            SigningCheck.checkCertificates();

            JPackageCommand cmd = JPackageCommand.helloAppImage();
            cmd.addArguments("--mac-sign", "--mac-signing-key-user-name",
                    SigningBase.DEV_NAME, "--mac-signing-keychain",
                    "jpackagerTest.keychain");
            cmd.executeAndAssertHelloAppImageCreated();

            Path launcherPath = cmd.appLauncherPath();
            SigningBase.verifyCodesign(launcherPath, true);

            Path appImage = cmd.outputBundle();
            SigningBase.verifyCodesign(appImage, true);
            SigningBase.verifySpctl(appImage, "exec");
        });
    }
}
