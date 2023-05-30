package com.manolo_stiller.docgpt.strategies

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtPsiFactory

class KotlinDocumentationStrategy : DocumentationStrategy {
    override fun getMethod(element: PsiElement): PsiElement? {
        return PsiTreeUtil.getParentOfType(element, KtNamedFunction::class.java)
    }

    override fun createComment(project: Project, text: String): PsiComment {
        val psiFactory = KtPsiFactory(project)
        return psiFactory.createComment("/** This is a KDoc comment */")
    }
}