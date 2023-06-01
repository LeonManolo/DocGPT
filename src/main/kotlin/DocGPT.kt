import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.*
import com.intellij.psi.util.elementType
import org.jetbrains.kotlin.psi.KtNamedFunction

class DocGPT : AnAction() {
    private val notificationGroup: NotificationGroup =
        NotificationGroupManager.getInstance().getNotificationGroup("DocGPT_balloon")

    override fun actionPerformed(e: AnActionEvent) {
        //BrowserUtil.browse("https://google.de")
        notificationGroup.createNotification("Das ist ein test", NotificationType.INFORMATION)
            .notify(e.project)
        val editor = e.getRequiredData(CommonDataKeys.EDITOR)
        val document = editor.document
        val psiFile = editor.project?.let { PsiDocumentManager.getInstance(it).getPsiFile(document) }
        val offset = editor.caretModel.offset
        val psiElement = psiFile?.findElementAt(offset)
        val psiMe = psiElement?.parent
        val elementFactory = JavaPsiFacade.getElementFactory(editor.project!!)
        val comment: PsiComment = elementFactory.createCommentFromText("// Dein Kommentar hier", null)

        if(psiMe is PsiMethod) {

            val psiMethod = (psiMe as PsiMethod)
            WriteCommandAction.runWriteCommandAction(editor.project!!) {
                //psiMe.parent.add(comment)
                psiMe.parent.addBefore(comment, psiMe)
            }
            val text = (psiMe as PsiMethod).text
            println(text)
        }

        if(psiMe is KtNamedFunction) {
            val funct = psiMe
            WriteCommandAction.runWriteCommandAction(editor.project!!) {
                psiMe.parent.add(comment)
                //psiMe.parent.parent.add(comment)
            }
           // val text = (psiMe as PsiMethod).text
        }



        notificationGroup.createNotification(" ${offset}Ist eine methode: ${psiElement?.language} ${psiElement is PsiMethod} ${psiElement!!::class.java}", NotificationType.INFORMATION)
            .notify(e.project)
    }

}