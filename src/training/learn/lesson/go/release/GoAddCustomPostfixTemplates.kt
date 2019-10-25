package training.learn.lesson.go.release

import com.goide.psi.GoFile
import com.intellij.psi.PsiDocumentManager
import com.intellij.ui.components.JBTextField
import com.intellij.ui.treeStructure.SimpleTree
import com.intellij.util.ui.UIUtil
import training.commands.SetSelectionCommand
import training.commands.kotlin.TaskContext
import training.learn.interfaces.Module
import training.learn.lesson.kimpl.KLesson
import training.learn.lesson.kimpl.LessonContext
import training.learn.lesson.kimpl.parseLessonSample
import training.learn.lesson.kimpl.trySelectionSample
import javax.swing.JButton
import javax.swing.JDialog
import javax.swing.text.JTextComponent

class GoAddCustomPostfixTemplates(module: Module) : KLesson("Add custom postfix templates", module, "go") {
    private val sample = parseLessonSample("""package main
        
func main() {
	a := [...]int{1, 2, 3, 4, 5, 6, 7}
    a.<caret>
//for i := len(${'$'}EXPR$) - 1; i >= 0; i-- {${'$'}END$}
}
  """.trimIndent())


    override val lessonContent: LessonContext.() -> Unit
        get() = {
            prepareSample(sample)
            task("EditorCopy") {
                editor.selectionModel.setSelection(81, 126)
                text("In the editor, press ${action(it)} to copy the code snippet.")
                trigger("EditorCopy")
            }
            task("SettingsTreeView") {
                text("Open settings by pressing ${action("ShowSettings")}  and navigate to <strong>Editor | General | Postfix Completion</strong>.")
                stateCheck { checkDialogIsOpened(it) }
            }
            task("Create new Go template") {
                editor.selectionModel.setSelection(78, 78)
                text("In the <strong>Postfix Completion</strong> menu, press the <strong>Add</strong> button and select <strong>Go</strong>.")
                stateCheck { checkItemIsPostfixCompletion(it) }
            }

            task("forrev") {
                text("In the <strong>Key</strong> text field, type the name of the postfix template: <code>$it</code>.")
                stateCheck { checkWordInTextField(it) }
            }
            task("for i := len(\$EXPR\$) - 1; i >= 0; i-- {\$END\$}") {
                text("In the text field at the bottom of the dialog, paste the code snippet that you have copied on the <strong>Step 1</strong>.")
                stateCheck { checkWordInTextField(it) }
            }
            task("OK") {
                text("Apply all changes by clicking <strong>OK</strong> in all settings dialogs.")
                stateCheck { checkButtonIsPressed(it) }
            }
            task {
                caret(5, 7)
                text("Start typing <code>forrev</code> and select the <code>forrev</code> template from the suggestion list.")
                trigger("EditorChooseLookupItem") {
                    val manager = PsiDocumentManager.getInstance(project)
                    val file = manager.getPsiFile(editor.document) as? GoFile ?: return@trigger false
                    val block = file.functions.find { it.name == "main" }?.block ?: return@trigger false
                    block.text == "{\n" +
                            "\ta := [...]int{1, 2, 3, 4, 5, 6, 7}\n" +
                            "\tfor i := len(a) - 1; i >= 0; i-- {\n" +
                            "\t}\n" +
                            "//for i := len(${'$'}EXPR\$) - 1; i >= 0; i-- {${'$'}END\$}\n" +
                            "}"
                }
            }
        }

    private fun TaskContext.checkWordInTextField(expected: String): Boolean =
            (focusOwner as? JTextComponent)?.text?.toLowerCase() == expected.toLowerCase()

    private fun TaskContext.checkButtonIsPressed(expected: String): Boolean =
            (focusOwner as JButton).text?.toLowerCase() == expected.toLowerCase()

    private fun TaskContext.checkDialogIsOpened(expected: String): Boolean =
            focusOwner?.javaClass?.name?.contains(expected) ?: false

    private fun TaskContext.checkItemIsPostfixCompletion(expected: String): Boolean =
            focusOwner is JBTextField && UIUtil.getParentOfType(JDialog::class.java, focusOwner)?.title == expected
}
