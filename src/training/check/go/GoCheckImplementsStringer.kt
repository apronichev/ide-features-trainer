/*
 * Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package training.check.go

import com.goide.psi.GoFile
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import training.check.Check

class GoCheckImplementsStringer : Check {

  private lateinit var project: Project
  private lateinit var editor: Editor

  override fun set(project: Project, editor: Editor) {
    this.project = project
    this.editor = editor
  }

  override fun before() {}

  override fun check(): Boolean {
    val document = editor.document
    val manager = PsiDocumentManager.getInstance(project)
    val file = manager.getPsiFile(document) as? GoFile ?: return false
    return file.methods.any { it.name == "String" }
  }

}