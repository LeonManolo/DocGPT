package com.manolo_stiller.docgpt.llms.openai.dtos

import kotlinx.serialization.Serializable

@Serializable
data class OpenAIChatCompletionsResponseDto(
    val id: String,
    val model: String,
    val choices: List<OpenAIChatCompletionsChoiceResponseDto>
)

@Serializable
data class OpenAIChatCompletionsChoiceResponseDto(
    val index: Int,
    val message: OpenAIChatCompletionsChoiceMessageResponseDto
)

@Serializable
data class OpenAIChatCompletionsChoiceMessageResponseDto(val role: String, val content: String)
