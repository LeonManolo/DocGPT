package com.manolo_stiller.docgpt.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import com.manolo_stiller.docgpt.models.SupportedLLMs
import com.manolo_stiller.docgpt.state.DocGPTPersistentStateComponent
import com.manolo_stiller.docgpt.utils.SecureStorageUtil
import java.awt.*
import java.text.NumberFormat
import javax.swing.*
import javax.swing.text.NumberFormatter


class DocGPTPersistentStateConfigurable : Configurable, Configurable.NoScroll, Disposable {

    companion object {
        const val SECURE_STORAGE_API_KEY_SUFFIX = "api_key"
    }


    private val secureStorage = SecureStorageUtil("com.manolo_stiller.docgpt")


    // configurations for the text fields
    private val numberFormatter =
        NumberFormatter(NumberFormat.getIntegerInstance().apply { isGroupingUsed = false }).also {
            it.minimum = 0           // Minimale erlaubte Zahl ist 1
            it.maximum = Int.MAX_VALUE
        }

    // this is the persistent component we can read or write to
    private val configState
        get() = DocGPTPersistentStateComponent.instance.state

    private val apiKeys by lazy {
        buildMap {
            for (llm in SupportedLLMs.entries) {
                val apiKey = secureStorage.retrieveData(getApiKeyForLLM(llm))
                put(llm, apiKey)
            }
        }
    }


    private val llmSelectorComboBox =
        ComboBox(SupportedLLMs.entries.toTypedArray()).also {
            it.addActionListener { _ ->
                val item = it.selectedItem as SupportedLLMs
                println(item.id())
                llmDetailsCardLayout.show(llmDetailsPanel, item.id())
            }
        }

    private val llmDetailsCardLayout = CardLayout()

    data class LlmDetailComponent(
        val apiKeyTextField: JBPasswordField,
        val modelField: JBTextField,
        val tokenField: JFormattedTextField,
    )

    private val llmFormFields = buildMap {
        println(configState)
        for (llm in SupportedLLMs.entries)
            put(
                llm, LlmDetailComponent(
                    apiKeyTextField = JBPasswordField().also {
                        it.text = apiKeys[llm]
                    },
                    modelField = JBTextField().also {
                        it.text = configState.llms[llm]?.model
                    },
                    tokenField = JFormattedTextField(numberFormatter).also {
                        it.text = configState.llms[llm]?.maxTokens.toString()
                        //it.inputVerifier = TokenFieldVerifier()
                    },
                )
            )
    }


    private val llmDetailsPanel = JPanel(llmDetailsCardLayout).also {
        for (llm in llmFormFields) {
            val value = llm.value
            val formPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(JBLabel("Api key: "), value.apiKeyTextField, 1, false)
                .addLabeledComponent(JBLabel("Model: "), value.modelField, 1, false)
                .addLabeledComponent(JBLabel("Max tokens: "), value.tokenField, 1, false)
                .addComponentFillVertically(Box(0), 0)
                .panel

            it.add(formPanel, llm.key.id())
        }
    }

    // providing a title
    override fun getDisplayName(): String = "DocGPT"

    // creating the ui
    override fun createComponent(): JComponent {
        return JPanel(BorderLayout(0, 10)).also {
            it.alignmentY = 0f
            it.add(JPanel().also { inner ->
                inner.layout = BoxLayout(inner, BoxLayout.X_AXIS)
                inner.add(JBLabel("LLM:"))
                inner.add(Box.createHorizontalStrut(10))
                inner.add(llmSelectorComboBox)
                inner.add(Box.createHorizontalGlue())
            }, BorderLayout.NORTH)
            it.add(llmDetailsPanel, BorderLayout.CENTER)
        }
    }


    override fun dispose() {
    }

    // this tells the preferences window whether to enable or disable the "Apply" button.
    // so if the user has changed anything - we want to know.
    override fun isModified(): Boolean {
        val selectedLLM = llmSelectorComboBox.selectedItem as SupportedLLMs
        if (selectedLLM != configState.activeLLM) return true

        for (llm in SupportedLLMs.entries) {
            val apiKeyFromStorage = secureStorage.retrieveData(getApiKeyForLLM(llm)) ?: ""
            val apiKeyFromField = llmFormFields[llm]?.apiKeyTextField?.text
            val model = llmFormFields[llm]?.modelField?.text
            val maxTokens = llmFormFields[llm]?.tokenField?.text?.toIntOrNull()

            if (apiKeyFromField != apiKeyFromStorage) return true
            if (model != configState.llms[llm]?.model) return true
            if (maxTokens != configState.llms[llm]?.maxTokens) return true
        }

        return false
    }

    // when the user hits "ok" or "apply" we want to update the configurable state
    override fun apply() {
        configState.activeLLM = (llmSelectorComboBox.selectedItem as SupportedLLMs)

        for (llm in SupportedLLMs.entries) {
            val model = llmFormFields[llm]?.modelField?.text?.trim()
            val maxTokens = llmFormFields[llm]?.tokenField?.text?.toIntOrNull()
            val apiKey = llmFormFields[llm]?.apiKeyTextField?.text?.trim()
            println("APIKEY:$apiKey")
            if (apiKey != null) {
                secureStorage.storeData(getApiKeyForLLM(llm), apiKey)
            }


            //TODO: fix with copyWith
            if (model != null) {
                configState.llms[llm]?.model = model
            }

            if (maxTokens != null) {
                configState.llms[llm]?.maxTokens = maxTokens
            }
        }

        println("SAVING CONFIG STATE:")
        println(configState)
    }

    // hitting "reset" should reset the ui to the latest saved config
    override fun reset() {
        llmSelectorComboBox.selectedItem = configState.activeLLM

        for (llm in SupportedLLMs.entries) {
            val apiKey = secureStorage.retrieveData(getApiKeyForLLM(llm))
            val model = configState.llms[llm]?.model
            val maxTokens = configState.llms[llm]?.maxTokens

            llmFormFields[llm]?.apiKeyTextField?.text = apiKey
            llmFormFields[llm]?.modelField?.text = model
            llmFormFields[llm]?.tokenField?.value = maxTokens
        }
    }
}

fun getApiKeyForLLM(llm: SupportedLLMs): String {
    return "${llm.id()}_${DocGPTPersistentStateConfigurable.SECURE_STORAGE_API_KEY_SUFFIX}"
}