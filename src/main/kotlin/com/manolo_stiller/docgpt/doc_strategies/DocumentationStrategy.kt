package com.manolo_stiller.docgpt.doc_strategies

import com.intellij.lang.Language
import com.intellij.lang.java.JavaLanguage
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.jetbrains.lang.dart.DartLanguage
import com.manolo_stiller.docgpt.exceptions.DocCommentException
import org.jetbrains.kotlin.idea.KotlinLanguage

interface DocumentationStrategy {
    fun getMethod(element: PsiElement): PsiElement?
    fun createComment(project: Project, text: String): PsiElement

    companion object {

        @Throws(DocCommentException::class)
        fun getStrategyByLanguage(language: Language): DocumentationStrategy? {
            return when (language) {
                JavaLanguage.INSTANCE -> JavaDocumentationStrategy()
                KotlinLanguage.INSTANCE -> KotlinDocumentationStrategy()
                DartLanguage.INSTANCE -> DartDocumentationStrategy()
                else -> null  // unsupported language
            }
        }
    }
}