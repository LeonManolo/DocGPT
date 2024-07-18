package com.manolo_stiller.docgpt.openai.dtos

import kotlinx.serialization.Serializable

@Serializable
data class OpenAIChatCompletionsResponseErrorDto(val error: OpenAIChatCompletionsResponseErrorErrorDto)

@Serializable
data class OpenAIChatCompletionsResponseErrorErrorDto(val message: String, val code: String)
