package com.manolo_stiller.docgpt.openai.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenAIChatCompletionsRequestDto(
    val model: String,
    @SerialName("max_tokens")
    val maxTokens: Int? = null,
    val messages: List<OpenAIChatCompletionsMessageRequestDto>
)

@Serializable
data class OpenAIChatCompletionsMessageRequestDto(val role: String, val content: String)