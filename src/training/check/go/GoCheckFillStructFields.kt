package training.check.go

import com.goide.psi.GoFile
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import training.check.Check

class GoCheckFillStructFields : Check {

    private var project: Project? = null
    private var editor: Editor? = null

    override fun set(project: Project, editor: Editor) {
        this.project = project
        this.editor = editor
    }

    override fun before() {}

    override fun check(): Boolean {
        val document = editor?.document ?: return false
        val manager = project?.let { PsiDocumentManager.getInstance(it) } ?: return false
        val file = manager.getPsiFile(document) as? GoFile ?: return false
        val block = file.functions.find { it.name == "main" }?.block ?: return false
        return block.text == "{\n" +
                "\tp := Person{\n" +
                "\t\tname:    \"\",\n" +
                "\t\tage:     0,\n" +
                "\t\taddress: Address{},\n" +
                "\t}\n" +
                "\tfmt.Println(p)\n" +
                "}"
    }

    override fun listenAllKeys(): Boolean = false

}