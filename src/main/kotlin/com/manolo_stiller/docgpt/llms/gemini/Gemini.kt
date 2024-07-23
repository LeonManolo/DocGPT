package com.manolo_stiller.docgpt.llms.gemini

import com.manolo_stiller.docgpt.exceptions.GenerateDocCommentException
import com.manolo_stiller.docgpt.llms.LLM
import com.manolo_stiller.docgpt.llms.gemini.dtos.GeminiGenerateContentErrorDto
import com.manolo_stiller.docgpt.llms.gemini.dtos.GeminiGenerateContentRequestDto
import com.manolo_stiller.docgpt.llms.gemini.dtos.GeminiGenerateContentResponseDto
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class Gemini(override val apiKey: String) : LLM() {

    override suspend fun generateDocComment(
        functionAsString: String,
        programmingLanguage: String,
        model: String,
        maxTokens: Int?
    ): String {
        val requestBody = GeminiGenerateContentRequestDto(
            systemInstruction = GeminiGenerateContentRequestDto.SystemInstructionDto(
                parts = GeminiGenerateContentRequestDto.SystemInstructionDto.PartsDto(
                    text = this.docInstruction(),
                ),
            ),
            contents = GeminiGenerateContentRequestDto.ContentsDto(
                parts = GeminiGenerateContentRequestDto.ContentsDto.PartsDto(
                    text = functionAsString,
                ),
            ),
            generationConfig = GeminiGenerateContentRequestDto.GenerationConfig(
                maxOutputTokens = maxTokens,
            ),
        )
        val response =
            client.post("https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent") {
                parameters {
                    parameter("key", apiKey)
                }
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }

        println(response.status)
        println(response.bodyAsText())

        if (!response.status.isSuccess()) {
            val responseBody = response.body<GeminiGenerateContentErrorDto>()
            throw GenerateDocCommentException(responseBody.error.message)
        }


        val responseBody = response.body<GeminiGenerateContentResponseDto>()
        val docComment = responseBody.candidates.first().content.parts.first().text
        // Gemini always produces markdown responses
        val markdownFree = removeMarkdownIfExists(docComment)
        return markdownFree
    }

    private fun removeMarkdownIfExists(text: String): String {
        return if (text.startsWith("```") && text.endsWith("```")) {
            text.substringAfter("\n").substringBeforeLast("\n").trim()
        } else {
            text
        }
    }
}