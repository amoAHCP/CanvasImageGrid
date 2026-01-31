#!/bin/bash
#
#
#                                  Apache License
#                            Version 2.0, January 2004
#                         http://www.apache.org/licenses/
#
#    TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION
#
#    1. Definitions.
#
#       "License" shall mean the terms and conditions for use, reproduction,
#       and distribution as defined by Sections 1 through 9 of this document.
#
#       "Licensor" shall mean the copyright owner or entity authorized by
#       the copyright owner that is granting the License.
#
#       "Legal Entity" shall mean the union of the acting entity and all
#       other entities that control, are controlled by, or are under common
#       control with that entity. For the purposes of this definition,
#       "control" means (i) the power, direct or indirect, to cause the
#       direction or management of such entity, whether by contract or
#       otherwise, or (ii) ownership of fifty percent (50%) or more of the
#       outstanding shares, or (iii) beneficial ownership of such entity.
#
#       "You" (or "Your") shall mean an individual or Legal Entity
#       exercising permissions granted by this License.
#
#       "Source" form shall mean the preferred form for making modifications,
#       including but not limited to software source code, documentation
#       source, and configuration files.
#
#       "Object" form shall mean any form resulting from mechanical
#       transformation or translation of a Source form, including but
#       not limited to compiled object code, generated documentation,
#       and conversions to other media types.
#
#       "Work" shall mean the work of authorship, whether in Source or
#       Object form, made available under the License, as indicated by a
#       copyright notice that is included in or attached to the work
#       (an example is provided in the Appendix below).
#
#       "Derivative Works" shall mean any work, whether in Source or Object
#       form, that is based on (or derived from) the Work and for which the
#       editorial revisions, annotations, elaborations, or other modifications
#       represent, as a whole, an original work of authorship. For the purposes
#       of this License, Derivative Works shall not include works that remain
#       separable from, or merely link (or bind by name) to the interfaces of,
#       the Work and Derivative Works thereof.
#
#       "Contribution" shall mean any work of authorship, including
#       the original version of the Work and any modifications or additions
#       to that Work or Derivative Works thereof, that is intentionally
#       submitted to Licensor for inclusion in the Work by the copyright owner
#       or by an individual or Legal Entity authorized to submit on behalf of
#       the copyright owner. For the purposes of this definition, "submitted"
#       means any form of electronic, verbal, or written communication sent
#       to the Licensor or its representatives, including but not limited to
#       communication on electronic mailing lists, source code control systems,
#       and issue tracking systems that are managed by, or on behalf of, the
#       Licensor for the purpose of discussing and improving the Work, but
#       excluding communication that is conspicuously marked or otherwise
#       designated in writing by the copyright owner as "Not a Contribution."
#
#       "Contributor" shall mean Licensor and any individual or Legal Entity
#       on behalf of whom a Contribution has been received by Licensor and
#       subsequently incorporated within the Work.
#
#    2. Grant of Copyright License. Subject to the terms and conditions of
#       this License, each Contributor hereby grants to You a perpetual,
#       worldwide, non-exclusive, no-charge, royalty-free, irrevocable
#       copyright license to reproduce, prepare Derivative Works of,
#       publicly display, publicly perform, sublicense, and distribute the
#       Work and such Derivative Works in Source or Object form.
#
#    3. Grant of Patent License. Subject to the terms and conditions of
#       this License, each Contributor hereby grants to You a perpetual,
#       worldwide, non-exclusive, no-charge, royalty-free, irrevocable
#       (except as stated in this section) patent license to make, have made,
#       use, offer to sell, sell, import, and otherwise transfer the Work,
#       where such license applies only to those patent claims licensable
#       by such Contributor that are necessarily infringed by their
#       Contribution(s) alone or by combination of their Contribution(s)
#       with the Work to which such Contribution(s) was submitted. If You
#       institute patent litigation against any entity (including a
#       cross-claim or counterclaim in a lawsuit) alleging that the Work
#       or a Contribution incorporated within the Work constitutes direct
#       or contributory patent infringement, then any patent licenses
#       granted to You under this License for that Work shall terminate
#       as of the date such litigation is filed.
#
#    4. Redistribution. You may reproduce and distribute copies of the
#       Work or Derivative Works thereof in any medium, with or without
#       modifications, and in Source or Object form, provided that You
#       meet the following conditions:
#
#       (a) You must give any other recipients of the Work or
#           Derivative Works a copy of this License; and
#
#       (b) You must cause any modified files to carry prominent notices
#           stating that You changed the files; and
#
#       (c) You must retain, in the Source form of any Derivative Works
#           that You distribute, all copyright, patent, trademark, and
#           attribution notices from the Source form of the Work,
#           excluding those notices that do not pertain to any part of
#           the Derivative Works; and
#
#       (d) If the Work includes a "NOTICE" text file as part of its
#           distribution, then any Derivative Works that You distribute must
#           include a readable copy of the attribution notices contained
#           within such NOTICE file, excluding those notices that do not
#           pertain to any part of the Derivative Works, in at least one
#           of the following places: within a NOTICE text file distributed
#           as part of the Derivative Works; within the Source form or
#           documentation, if provided along with the Derivative Works; or,
#           within a display generated by the Derivative Works, if and
#           wherever such third-party notices normally appear. The contents
#           of the NOTICE file are for informational purposes only and
#           do not modify the License. You may add Your own attribution
#           notices within Derivative Works that You distribute, alongside
#           or as an addendum to the NOTICE text from the Work, provided
#           that such additional attribution notices cannot be construed
#           as modifying the License.
#
#       You may add Your own copyright statement to Your modifications and
#       may provide additional or different license terms and conditions
#       for use, reproduction, or distribution of Your modifications, or
#       for any such Derivative Works as a whole, provided Your use,
#       reproduction, and distribution of the Work otherwise complies with
#       the conditions stated in this License.
#
#    5. Submission of Contributions. Unless You explicitly state otherwise,
#       any Contribution intentionally submitted for inclusion in the Work
#       by You to the Licensor shall be under the terms and conditions of
#       this License, without any additional terms or conditions.
#       Notwithstanding the above, nothing herein shall supersede or modify
#       the terms of any separate license agreement you may have executed
#       with Licensor regarding such Contributions.
#
#    6. Trademarks. This License does not grant permission to use the trade
#       names, trademarks, service marks, or product names of the Licensor,
#       except as required for reasonable and customary use in describing the
#       origin of the Work and reproducing the content of the NOTICE file.
#
#    7. Disclaimer of Warranty. Unless required by applicable law or
#       agreed to in writing, Licensor provides the Work (and each
#       Contributor provides its Contributions) on an "AS IS" BASIS,
#       WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
#       implied, including, without limitation, any warranties or conditions
#       of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A
#       PARTICULAR PURPOSE. You are solely responsible for determining the
#       appropriateness of using or redistributing the Work and assume any
#       risks associated with Your exercise of permissions under this License.
#
#    8. Limitation of Liability. In no event and under no legal theory,
#       whether in tort (including negligence), contract, or otherwise,
#       unless required by applicable law (such as deliberate and grossly
#       negligent acts) or agreed to in writing, shall any Contributor be
#       liable to You for damages, including any direct, indirect, special,
#       incidental, or consequential damages of any character arising as a
#       result of this License or out of the use or inability to use the
#       Work (including but not limited to damages for loss of goodwill,
#       work stoppage, computer failure or malfunction, or any and all
#       other commercial damages or losses), even if such Contributor
#       has been advised of the possibility of such damages.
#
#    9. Accepting Warranty or Additional Liability. While redistributing
#       the Work or Derivative Works thereof, You may choose to offer,
#       and charge a fee for, acceptance of support, warranty, indemnity,
#       or other liability obligations and/or rights consistent with this
#       License. However, in accepting such obligations, You may act only
#       on Your own behalf and on Your sole responsibility, not on behalf
#       of any other Contributor, and only if You agree to indemnify,
#       defend, and hold each Contributor harmless for any liability
#       incurred by, or claims asserted against, such Contributor by reason
#       of your accepting any such warranty or additional liability.
#
#    END OF TERMS AND CONDITIONS
#
#    APPENDIX: How to apply the Apache License to your work.
#
#       To apply the Apache License to your work, attach the following
#       boilerplate notice, with the fields enclosed by brackets "[]"
#       replaced with your own identifying information. (Don't include
#       the brackets!)  The text should be enclosed in the appropriate
#       comment syntax for the file format. We also recommend that a
#       file or class name and description of purpose be included on the
#       same "printed page" as the copyright notice for easier
#       identification within third-party archives.
#
#    Copyright [2017] [Andy Moncsek]
#
#    Licensed under the Apache License, Version 2.0 (the "License");
#    you may not use this file except in compliance with the License.
#    You may obtain a copy of the License at
#
#        http://www.apache.org/licenses/LICENSE-2.0
#
#    Unless required by applicable law or agreed to in writing, software
#    distributed under the License is distributed on an "AS IS" BASIS,
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#    See the License for the specific language governing permissions and
#    limitations under the License.
#

set -e

echo "=========================================="
echo "Gluon Substrate Native Build for CanvasImageGrid"
echo "=========================================="
echo ""

# Check for GraalVM
if ! command -v native-image &> /dev/null; then
    echo "ERROR: native-image not found!"
    echo "Gluon Client requires GraalVM with native-image installed"
    echo ""
    echo "Installation steps:"
    echo "1. Download GraalVM from https://www.graalvm.org/downloads/"
    echo "2. Set JAVA_HOME to GraalVM directory"
    echo "3. Run: gu install native-image"
    exit 1
fi

echo "Using: $(native-image --version)"
echo "Java: $(java -version 2>&1 | head -1)"
echo ""

# Check Maven version
MAVEN_VERSION=$(mvn --version | head -1 | awk '{print $3}')
MAVEN_MAJOR=$(echo $MAVEN_VERSION | cut -d. -f1)
MAVEN_MINOR=$(echo $MAVEN_VERSION | cut -d. -f2)

if [ "$MAVEN_MAJOR" != "3" ] || [ "$MAVEN_MINOR" != "8" ]; then
    echo "ERROR: Gluon requires Maven 3.8.x (you have $MAVEN_VERSION)"
    echo ""
    echo "Please install Maven 3.8.8:"
    echo "  sdk install maven 3.8.8"
    echo "  sdk use maven 3.8.8"
    echo ""
    exit 1
fi

echo "Maven: Apache Maven $MAVEN_VERSION (OK)"
echo ""

# Check GraalVM version
GRAALVM_VERSION=$(java -version 2>&1 | grep "GraalVM" | grep -oE "[0-9]+\.[0-9]+\.[0-9]+" | head -1)
if [ -z "$GRAALVM_VERSION" ]; then
    echo "ERROR: GraalVM not detected!"
    echo "Please install GraalVM 21.x:"
    echo "  sdk install java 21.0.2-graal"
    echo "  sdk use java 21.0.2-graal"
    exit 1
fi

GRAALVM_MAJOR=$(echo $GRAALVM_VERSION | cut -d. -f1)
if [ "$GRAALVM_MAJOR" != "21" ]; then
    echo "ERROR: Gluon requires GraalVM 21.x (you have $GRAALVM_VERSION)"
    echo ""
    echo "GraalVM 25.x is NOT supported by Gluon 1.0.24!"
    echo ""
    echo "Please install GraalVM 21.x:"
    echo "  sdk install java 21.0.2-graal"
    echo "  sdk use java 21.0.2-graal"
    echo ""
    exit 1
fi

echo "GraalVM: version $GRAALVM_VERSION (OK)"
echo ""

# Set GRAALVM_HOME if not already set
if [ -z "$GRAALVM_HOME" ]; then
    # Try to find GraalVM from JAVA_HOME
    if [ -n "$JAVA_HOME" ] && [[ "$JAVA_HOME" == *"graal"* ]]; then
        export GRAALVM_HOME="$JAVA_HOME"
        echo "Set GRAALVM_HOME=$GRAALVM_HOME (from JAVA_HOME)"
    else
        # Try SDKMAN current Java
        if [ -d "$HOME/.sdkman/candidates/java/current" ]; then
            export GRAALVM_HOME="$HOME/.sdkman/candidates/java/current"
            export JAVA_HOME="$GRAALVM_HOME"
            echo "Set GRAALVM_HOME=$GRAALVM_HOME (from SDKMAN)"
        else
            echo "ERROR: GRAALVM_HOME not set and could not auto-detect GraalVM"
            echo "Please set GRAALVM_HOME environment variable"
            exit 1
        fi
    fi
fi

echo ""

# Clean previous builds
echo "Step 1: Cleaning previous builds..."
mvn clean

# Build with Gluon
echo ""
echo "Step 2: Building native image with Gluon Substrate..."
echo "This will take 5-15 minutes depending on your system..."
echo ""

mvn gluonfx:build

if [ $? -eq 0 ]; then
    echo ""
    echo "=========================================="
    echo "✅ SUCCESS! Native executable created with Gluon"
    echo "=========================================="
    echo ""

    # Find the executable
    if [ -f "target/gluonfx/aarch64-darwin/CanvasImageGrid" ]; then
        EXEC_PATH="target/gluonfx/aarch64-darwin/CanvasImageGrid"
    elif [ -f "target/gluonfx/x86_64-darwin/CanvasImageGrid" ]; then
        EXEC_PATH="target/gluonfx/x86_64-darwin/CanvasImageGrid"
    elif [ -f "target/gluonfx/x86_64-linux/CanvasImageGrid" ]; then
        EXEC_PATH="target/gluonfx/x86_64-linux/CanvasImageGrid"
    elif [ -f "target/gluonfx/aarch64-linux/CanvasImageGrid" ]; then
        EXEC_PATH="target/gluonfx/aarch64-linux/CanvasImageGrid"
    else
        EXEC_PATH=$(find target/gluonfx -name "CanvasImageGrid" -type f 2>/dev/null | head -1)
    fi

    if [ -n "$EXEC_PATH" ]; then
        echo "Location: $EXEC_PATH"
        echo "Size: $(du -h "$EXEC_PATH" | cut -f1)"
        echo ""
        echo "To run:"
        echo "  ./$EXEC_PATH <image-directory>"
        echo ""
        echo "Example:"
        echo "  ./$EXEC_PATH ~/Pictures"
        echo ""
    else
        echo "Executable created but location could not be determined."
        echo "Check target/gluonfx/ directory"
    fi
else
    echo ""
    echo "=========================================="
    echo "❌ Gluon build FAILED"
    echo "=========================================="
    echo ""
    echo "Common issues:"
    echo "1. Make sure you're using GraalVM (not regular JDK)"
    echo "2. Ensure native-image is installed: gu install native-image"
    echo "3. Check that you have enough memory (8GB+ recommended)"
    echo "4. On first run, Gluon downloads native libraries (may take time)"
    echo ""
    exit 1
fi
