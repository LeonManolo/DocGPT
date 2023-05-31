package com.manolo_stiller.docgpt.strategies

import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.util.PsiTreeUtil

class JavaDocumentationStrategy : DocumentationStrategy {
    override fun getMethod(element: PsiElement): PsiElement? {
        return PsiTreeUtil.getParentOfType(element, PsiMethod::class.java)
    }

    override fun createComment(project: Project, text: String): PsiComment {
        val elementFactory = JavaPsiFacade.getElementFactory(project)
        return elementFactory.createDocCommentFromText(text)
    }
}