

/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.test.runners;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.util.KtTestUtil;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link kotlinx.rpc.codegen.test.GenerateTestsKt}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("src/testData/diagnostics")
@TestDataPath("$PROJECT_ROOT")
public class DiagnosticTestGenerated extends AbstractDiagnosticTest {
  @Test
  public void testAllFilesPresentInDiagnostics() {
    KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("src/testData/diagnostics"), Pattern.compile("^(.+)\\.kt$"), null, true);
  }

  @Test
  @TestMetadata("checkedAnnotation.kt")
  public void testCheckedAnnotation() {
    runTest("src/testData/diagnostics/checkedAnnotation.kt");
  }

  @Test
  @TestMetadata("rpcChecked.kt")
  public void testRpcChecked() {
    runTest("src/testData/diagnostics/rpcChecked.kt");
  }
}
