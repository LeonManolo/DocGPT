package com.manolo_stiller.docgpt.llms.gemini.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeminiGenerateContentRequestDto(
    @SerialName("system_instruction")
    val systemInstruction: SystemInstructionDto,

    @SerialName("contents")
    val contents: ContentsDto,

    @SerialName("generationConfig")
    val generationConfig: GenerationConfig,
) {

    @Serializable
    data class SystemInstructionDto(val parts: PartsDto){

        @Serializable
        data class PartsDto(val text: String)

    }

    @Serializable
    data class ContentsDto(val parts: PartsDto) {

        @Serializable
        data class PartsDto(val text: String)
    }

    @Serializable
    data class GenerationConfig(val maxOutputTokens: Int?)
}

