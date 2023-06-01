package com.manolo_stiller.docgpt

import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.RetryStrategy
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
import com.manolo_stiller.docgpt.state.DocGPTPersistentStateComponent
import com.manolo_stiller.docgpt.strategies.DocumentationStrategy
import com.manolo_stiller.docgpt.utils.NotificationUtils
import com.manolo_stiller.docgpt.utils.SecureStorageUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

// https://github.com/JetBrains/intellij-sdk-docs/blob/main/code_samples/conditional_operator_intention/src/main/java/org/intellij/sdk/intention/ConditionalOperatorConverter.java
class DocGPTIntention : PsiElementBaseIntentionAction(), IntentionAction {
    private val secureStorage = SecureStorageUtil("com.manolo_stiller.docgpt")
    private val configState = DocGPTPersistentStateComponent.instance.state
    private val notificationUtils = NotificationUtils()


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
        val openai = OpenAI(
            token = secureStorage.retrieveData("api_key") ?: "",
            retry = RetryStrategy(maxRetries = 2)
        )
        notificationUtils.sendToolWindowNotification(project, "Generating doc comment")

        val strategy = DocumentationStrategy.getStrategyByLanguage(element.language) ?: return
        val method = strategy.getMethod(element) ?: return
        val prompt =
            "Write me doc comment for the code I provided below in the specific doc style for the language. " +
                    "The programming language is ${element.language.displayName}. Only answer with the doc comment, the function itself should" +
                    " not be included in your response. There should be no errors when I paste the response of you" +
                    " in my IDE. If you cant produce a doc comment answer with a doc comment that explains why you" +
                    " couldn't write a doc comment (keep it short).\n" +
                    "The Code:\n${method.text}"

        val promptTokenCount = prompt.length % 4 // temporary a tokenizer should be used

        val modelId = ModelId(configState.model)
        val completionRequest = CompletionRequest(
            model = modelId,
            prompt = prompt,
            n = 1,
            echo = false,
            maxTokens = configState.maxTokens - promptTokenCount, // cant be higher than the max token of the specified model
        )

        // Background thread needed so that the UI doesn't freeze
        val backgroundTask = object : Task.Backgroundable(project, "Background") {
            override fun run(indicator: ProgressIndicator) {
                val result = runBlocking(Dispatchers.IO) {
                    try {
                        openai.completion(completionRequest)
                    } catch (e: Exception) {
                        notificationUtils.sendNotification(project, "Error, check your configurations\n${e.message}", NotificationType.ERROR)
                        return@runBlocking null
                    }
                }

                result?.choices?.firstOrNull()?.let {
                    // Needs to be on the UI thread again to update the file
                    ApplicationManager.getApplication().invokeLater {
                        WriteCommandAction.runWriteCommandAction(project) {
                            try {
                                val comment = strategy.createComment(project, it.text)
                                method.parent.addBefore(comment, method)
                                notificationUtils.sendToolWindowNotification(project, "Doc comment generated!")
                            } catch (e: Exception) {
                                notificationUtils.sendNotification(
                                    project,
                                    "Response was not a valid doc comment\n(try again)",
                                    NotificationType.WARNING
                                )
                                println(e)
                            }
                        }
                    }
                }
            }
        }
        ProgressManager.getInstance().run(backgroundTask)
    }

    override fun getText(): String {
        return "Generate Doc comment"
    }

}
