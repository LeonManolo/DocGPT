package com.manolo_stiller.docgpt.ui.validations

import com.intellij.ui.components.JBTextField
import javax.swing.InputVerifier
import javax.swing.JComponent

class TokenFieldVerifier: InputVerifier() {
    override fun verify(input: JComponent?): Boolean {
        val textField = input as? JBTextField

        return textField?.text?.toIntOrNull() != null
    }
}