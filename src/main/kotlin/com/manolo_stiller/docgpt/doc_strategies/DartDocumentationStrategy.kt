package com.manolo_stiller.docgpt.doc_strategies

import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.lang.dart.ide.DartCommenter
import com.jetbrains.lang.dart.psi.DartComponent
import com.jetbrains.lang.dart.psi.DartFunctionDeclarationWithBodyOrNative
import com.jetbrains.lang.dart.psi.DartMethodDeclaration
import com.jetbrains.lang.dart.util.DartElementGenerator
import com.manolo_stiller.docgpt.exceptions.DocCommentException

class DartDocumentationStrategy : DocumentationStrategy {
    override fun getMethod(element: PsiElement): PsiElement? {
        val dartFunction =
            PsiTreeUtil.getParentOfType(element, DartComponent::class.java)
        println(dartFunction)
        if (dartFunction is DartMethodDeclaration || dartFunction is DartFunctionDeclarationWithBodyOrNative) {
            return dartFunction
        }
        return null
    }

    override fun createComment(project: Project, text: String): PsiElement {
        try {
            val psiFile = DartElementGenerator.createDummyFile(project, text)
            val dartComment = PsiTreeUtil.findChildOfType(psiFile, PsiComment::class.java)
                ?: throw DocCommentException("Invalid doc comment")

            val dartCommenter = DartCommenter()
            val isDartComment = dartCommenter.isDocumentationComment(dartComment)
            print(isDartComment)

            print(dartComment)
            return if (isDartComment) dartComment else throw DocCommentException("Invalid doc comment")
        } catch (e: Exception) {
            throw DocCommentException(e.message ?: "")
        }
    }
}


