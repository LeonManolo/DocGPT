package com.manolo_stiller.docgpt.state

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.manolo_stiller.docgpt.models.SupportedLLMs

@State(
    name = "DocGPTPersistentStateComponent",
    storages = [Storage("DocGPT_state.xml")]
)
class DocGPTPersistentStateComponent : PersistentStateComponent<DocGPTState> {

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


}

data class DocGPTState(
    var activeLLM: SupportedLLMs = SupportedLLMs.OpenAI,
    var llms: Map<SupportedLLMs, DocGPTLLMDetailsState> = SupportedLLMs.entries.associateWith {
        DocGPTLLMDetailsState(model = it.defaultModel())
    }
) {

//    init {
//        if(llms.isEmpty()) {
//            llms = buildMap {
//                for(llm in SupportedLLMs.entries) {
//                    put(llm, DocGPTLLMDetailsState(
//                        model = llm.defaultModel(),
//                        maxTokens = 2048
//                    ))
//                }
//            }
//        }
//    }
}

data class DocGPTLLMDetailsState(var model: String = "", var maxTokens: Int = 2048)
