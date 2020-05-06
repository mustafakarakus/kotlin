/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.perf

import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.testFramework.UsefulTestCase
import org.jetbrains.kotlin.idea.perf.util.enableSingleInspection
import org.jetbrains.kotlin.idea.perf.util.lastPathSegment
import org.jetbrains.kotlin.idea.perf.util.statisticSuite
import org.jetbrains.kotlin.idea.testFramework.ProjectOpenAction
import java.nio.file.Paths

class UnusedSymbolInspectionPerformanceProjectTest : UsefulTestCase() {
    val listOfFiles = arrayOf(
        "libraries/tools/kotlin-gradle-plugin-integration-tests/src/test/kotlin/org/jetbrains/kotlin/gradle/NewMultiplatformIT.kt",
        "idea/idea-analysis/src/org/jetbrains/kotlin/idea/util/PsiPrecedences.kt",
        "compiler/psi/src/org/jetbrains/kotlin/psi/KtElement.kt",
        "compiler/psi/src/org/jetbrains/kotlin/psi/KtFile.kt",
        "compiler/psi/src/org/jetbrains/kotlin/psi/KtImportInfo.kt"
    )
    val listOfInspections = arrayOf("UnusedSymbol", "MemberVisibilityCanBePrivate")

    fun testLocalInspection() {
        statisticSuite {
            app {
                gradleProject("../perfTestProject") {
                    for (inspection in listOfInspections) {
                        enableSingleInspection(inspection)
                        for (file in listOfFiles) {
                            val editorFile = editor(file)

                            measure<List<HighlightInfo>>(inspection, file.lastPathSegment()) {
                                test = {
                                    highlight(editorFile)
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}