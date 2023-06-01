package com.manolo_stiller.docgpt.state

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.manolo_stiller.docgpt.utils.SecureStorageUtil

@State(
    name = "DocGPTPersistentStateComponent",
    storages = [Storage("DocGPT_state.xml")]
)
class DocGPTPersistentStateComponent : PersistentStateComponent<DocGPTPersistentStateComponent.DocGPTState> {

    // this is how we're going to call the component from different classes
    companion object {
        val instance: DocGPTPersistentStateComponent
            get() = ServiceManager.getService(DocGPTPersistentStateComponent::class.java)
    }

    private var docGPTState = DocGPTState()
    override fun getState(): DocGPTState {
        return docGPTState
    }

    override fun loadState(state: DocGPTState) {
        docGPTState = state
    }

    class DocGPTState {
        var model: String = "text-davinci-003"
        var maxTokens: Int = 2048
    }
}