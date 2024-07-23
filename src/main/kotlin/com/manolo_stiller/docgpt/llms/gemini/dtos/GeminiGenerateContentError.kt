package com.manolo_stiller.docgpt.llms.gemini.dtos

import kotlinx.serialization.Serializable

@Serializable
class GeminiGenerateContentErrorDto(
    val error: ErrorDto,
) {

    @Serializable
    data class ErrorDto(val code: Int, val message: String, val status: String)
}