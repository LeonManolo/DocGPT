package com.manolo_stiller.docgpt.strategies

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.lang.dart.psi.DartComponent
import com.jetbrains.lang.dart.psi.DartFunctionDeclarationWithBodyOrNative
import com.jetbrains.lang.dart.psi.DartMethodDeclaration
import org.jetbrains.kotlin.psi.KtPsiFactory

class DartDocumentationStrategy : DocumentationStrategy {
    override fun getMethod(element: PsiElement): PsiElement? {
        val dartFunction =
            PsiTreeUtil.getParentOfType(element, DartComponent::class.java)
        //println(dartFunction)
        if(dartFunction is DartMethodDeclaration || dartFunction is DartFunctionDeclarationWithBodyOrNative) {
            return dartFunction
        }
        return null
    }

    //DartElementGenerator
    override fun createComment(project: Project, text: String): PsiComment {
        val psiFactory = KtPsiFactory(project)
        return psiFactory.createComment(text)
    }
}
