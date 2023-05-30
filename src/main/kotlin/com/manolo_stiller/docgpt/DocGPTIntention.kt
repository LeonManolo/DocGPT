package com.manolo_stiller.docgpt

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.lang.Language
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.manolo_stiller.docgpt.strategies.DocumentationStrategy
import org.jetbrains.kotlin.psi.KtNamedFunction

class DocGPTIntention : PsiElementBaseIntentionAction(), IntentionAction {
    private val notificationGroup: NotificationGroup =
        NotificationGroupManager.getInstance().getNotificationGroup("DocGPT")

    override fun getFamilyName(): String {
        return "DocGPTIntention"
    }

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        DocumentationStrategy.getStrategyByLanguage(element.language)?.let { strategy ->
            return strategy.getMethod(element) != null
        }
        return false
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        notificationGroup.createNotification("Das ist ein test", NotificationType.INFORMATION)
            .notify(project)

        val strategy = DocumentationStrategy.getStrategyByLanguage(element.language) ?: return
        val method = strategy.getMethod(element) ?: return
        val comment = strategy.createComment(project, "")

        method.parent.addBefore(comment, method)
    }

    override fun getText(): String {
        return "Generate Doc comment"
    }

}