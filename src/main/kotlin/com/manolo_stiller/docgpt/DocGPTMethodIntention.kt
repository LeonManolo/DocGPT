package com.manolo_stiller.docgpt

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.codeInsight.intention.preview.IntentionPreviewUtils
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.manolo_stiller.docgpt.state.DocGPTPersistentStateComponent
import com.manolo_stiller.docgpt.doc_strategies.DocumentationStrategy
import com.manolo_stiller.docgpt.openai.OpenAI
import com.manolo_stiller.docgpt.utils.NotificationUtils
import com.manolo_stiller.docgpt.utils.SecureStorageUtil
import kotlinx.coroutines.*

class DocGPTMethodIntention : PsiElementBaseIntentionAction(), IntentionAction {
    private val secureStorage = SecureStorageUtil("com.manolo_stiller.docgpt")
    private val configState = DocGPTPersistentStateComponent.instance.state
    private val notificationUtils = NotificationUtils()
    private val apiKey by lazy {
        secureStorage.retrieveData("api_key")
    }



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

        // Check if it's intention preview
        // Intention Preview (hovering) seems to break everything, I dont even know what it should be used for
        if (IntentionPreviewUtils.isIntentionPreviewActive()) return

        notificationUtils.sendToolWindowNotification(project, "Generating doc comment")

        val strategy = DocumentationStrategy.getStrategyByLanguage(element.language) ?: return

        val method = strategy.getMethod(element)
        val methodText = method?.text ?: return

        if (apiKey == null) {
            return notificationUtils.sendNotification(
                project,
                "API key not found!",
                NotificationType.ERROR,
            )
        }

        val openAI = OpenAI(apiKey = apiKey!!)

        // Use a background task to avoid blocking the UI thread
        val backgroundTask = object : Task.Backgroundable(project, "AIDocGeneration") {
            override fun run(indicator: ProgressIndicator) {
                try {
                    // Run the suspend function in a coroutine
                    val docComment = runBlocking(Dispatchers.IO) {
                        openAI.generateDocComment(
                            functionAsString = methodText,
                            programmingLanguage = element.language.displayName,
                            model = configState.model,
                            maxTokens = configState.maxTokens,
                        )
                         //"/** test */"
                    }

                    // Needs to be on the UI thread again to update the file
                    ApplicationManager.getApplication().invokeLater {
                        WriteCommandAction.runWriteCommandAction(project) {
                            try {
                                val comment = strategy.createComment(project, docComment)
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
                } catch (e: Exception) {
                    notificationUtils.sendNotification(
                        project,
                        "Error, check your configurations\n${e.message}",
                        NotificationType.ERROR
                    )
                }
            }
        }
        ProgressManager.getInstance().run(backgroundTask)
    }

    override fun getText(): String {
        return "Generate Doc comment"
    }
}
