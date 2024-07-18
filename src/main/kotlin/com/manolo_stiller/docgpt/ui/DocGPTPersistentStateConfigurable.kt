package com.manolo_stiller.docgpt.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.options.Configurable
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import com.manolo_stiller.docgpt.state.DocGPTPersistentStateComponent
import com.manolo_stiller.docgpt.utils.SecureStorageUtil
import java.awt.BorderLayout
import java.text.NumberFormat
import javax.swing.*
import javax.swing.text.NumberFormatter


class DocGPTPersistentStateConfigurable : Configurable, Configurable.NoScroll, Disposable {

    private val secureStorage = SecureStorageUtil("com.manolo_stiller.docgpt")

    // configurations for the text fields
    private val numberFormatter = NumberFormatter(NumberFormat.getIntegerInstance()).also {
        it.allowsInvalid = true
    }

    // this is the persistent component we can read or write to
    private val configState
        get() = DocGPTPersistentStateComponent.instance.state

    private val apiKey by lazy {
        secureStorage.retrieveData("api_key")
    }
    // ui components
    private val apiKeyField = JBPasswordField().also {
        it.text = apiKey
    }
    private val modelField = JBTextField().also {
        it.text = configState.model
    }
    private var maxTokensField: JFormattedTextField = JFormattedTextField(numberFormatter).also {
        it.text = configState.maxTokens.toString()
    }

    // providing a title
    override fun getDisplayName(): String = "DocGPT"

    // creating the ui
    override fun createComponent(): JComponent {
        val formPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Api key: "), apiKeyField, 1, false)
            .addLabeledComponent(JBLabel("Model: "), modelField, 1, false)
            .addLabeledComponent(JBLabel("Max tokens: "), maxTokensField, 1, false)
            .panel

        return JPanel(BorderLayout()).also { it.add(formPanel, BorderLayout.NORTH) }
    }


    override fun dispose() {
    }

    // this tells the preferences window whether to enable or disable the "Apply" button.
    // so if the user has changed anything - we want to know.
    override fun isModified(): Boolean {
        return configState.maxTokens != maxTokensField.text.toIntOrNull()
                || configState.model != modelField.text
                || apiKey != apiKeyField.text
    }

    // when the user hits "ok" or "apply" we want to update the configurable state
    override fun apply() {
        secureStorage.storeData("api_key", apiKeyField.text)
        configState.model = modelField.text
        maxTokensField.text.toIntOrNull()?.let {
            configState.maxTokens = it
        }
    }

    // hitting "reset" should reset the ui to the latest saved config
    override fun reset() {
        apiKeyField.text = apiKey
        modelField.text = configState.model
        maxTokensField.text = configState.maxTokens.toString()
    }
}