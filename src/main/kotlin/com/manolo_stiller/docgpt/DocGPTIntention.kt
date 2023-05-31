package com.manolo_stiller.docgpt

import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.MessageType
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.wm.ToolWindowId
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.openapi.wm.WindowManager
import com.intellij.psi.*
import com.intellij.ui.awt.RelativePoint
import com.manolo_stiller.docgpt.strategies.DocumentationStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class DocGPTIntention : PsiElementBaseIntentionAction(), IntentionAction {
    private val apiKey = "sk-"
    val openai = OpenAI(
        token = apiKey,
        //timeout = Timeout(socket = 60.seconds),
    )

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
        val statusBar = WindowManager.getInstance().getStatusBar(project)
        val toolWindowManager = ToolWindowManager.getInstance(project)
        val toolWindow = toolWindowManager.getToolWindow(ToolWindowId.)
        if (statusBar != null) {
            println("Statusbar nicht null")
            JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder("Das ist ein Test", MessageType.INFO, null)
                .setFadeoutTime(7500)
                .createBalloon()
                //.show(RelativePoint.getNorthEastOf(statusBar.component), Balloon.Position.below)
            .show(RelativePoint.getCenterOf(toolWindow!!.component), Balloon.Position.above)
        }
        JBPopupFactory.getInstance().createMessage("HALLO")
        //notificationGroup.createNotification("Das ist ein test", NotificationType.INFORMATION)
       //     .notify(project)

        /*
        val strategy = DocumentationStrategy.getStrategyByLanguage(element.language) ?: return
        val method = strategy.getMethod(element) ?: return
        println("test: ${element.text}")
        println("test2: ${method.text}")
        val prompt =
            "Write me doc comment for the code I provided below in the specific doc style for the language. " +
                    "The programming language is ${element.language.displayName}. Only answer with the doc comment, the function itself should" +
                    " not be included in your response. There should be no errors when I paste the response of you" +
                    " in my IDE. If you cant produce a doc comment answer with a doc comment that explain why you" +
                    " couldn't write a doc comment (keep it short).\n" +
                    "The Code:\n${method.text}"

        val promptTokenCount = prompt.length % 4

        val modelId = ModelId("text-davinci-003")
        val completionRequest = CompletionRequest(
            model = modelId,
            prompt = prompt,
            n = 1,
            echo = false,
            maxTokens = 2048 - promptTokenCount, // cant be higher than the max token of the specified model
        )

        // Background thread needed so that the UI doesn't freeze
        val backgroundTask = object : Task.Backgroundable(project, "Background") {
            override fun run(indicator: ProgressIndicator) {
                val result = runBlocking(Dispatchers.IO) {
                    openai.completion(completionRequest)
                }
                result.choices.firstOrNull()?.let {
                    println(it.text)
                    // Needs to be on the UI thread again to update the file
                    ApplicationManager.getApplication().invokeLater {
                        WriteCommandAction.runWriteCommandAction(project) {
                            val comment = strategy.createComment(project, it.text)
                            method.parent.addBefore(comment, method)

                        }
                    }
                }
            }
        }
        ProgressManager.getInstance().run(backgroundTask)

         */
    }

    override fun getText(): String {
        return "Generate Doc comment"
    }

}