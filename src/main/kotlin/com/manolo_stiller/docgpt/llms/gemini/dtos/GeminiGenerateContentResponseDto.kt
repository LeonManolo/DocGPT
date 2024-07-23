package com.manolo_stiller.docgpt.llms.gemini.dtos

import kotlinx.serialization.Serializable

@Serializable
data class GeminiGenerateContentResponseDto(val candidates: List<CandidateDto>) {

    @Serializable
    data class CandidateDto(
        val finishReason: String,
        val content: ContentDto,
    ) {

        @Serializable
        data class ContentDto(
            val parts: List<PartDto>
        ) {

            @Serializable
            data class PartDto(val text: String)
        }
    }
}