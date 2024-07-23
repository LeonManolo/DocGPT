package com.manolo_stiller.docgpt.llms.openai

import com.manolo_stiller.docgpt.exceptions.GenerateDocCommentException
import com.manolo_stiller.docgpt.llms.LLM
import com.manolo_stiller.docgpt.llms.openai.dtos.OpenAIChatCompletionsMessageRequestDto
import com.manolo_stiller.docgpt.llms.openai.dtos.OpenAIChatCompletionsRequestDto
import com.manolo_stiller.docgpt.llms.openai.dtos.OpenAIChatCompletionsResponseDto
import com.manolo_stiller.docgpt.llms.openai.dtos.OpenAIChatCompletionsResponseErrorDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class OpenAI(override val apiKey: String) : LLM() {

    override suspend fun generateDocComment(
        functionAsString: String,
        programmingLanguage: String,
        model: String, //gpt-4o-mini
        maxTokens: Int?,
    ): String {
        println(model)
        val body = OpenAIChatCompletionsRequestDto(
            model = model,
            maxTokens = maxTokens,
            messages = listOf(
                OpenAIChatCompletionsMessageRequestDto(
                    role = "system",
                    content = this.docInstruction(),
                ),
                OpenAIChatCompletionsMessageRequestDto(
                    role = "user",
                    content = "Language:\n$programmingLanguage\n\nFunction:\n$functionAsString",
                ),
            ),
        )

        println(body)
        val response = client.post("https://api.openai.com/v1/chat/completions") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $apiKey")
            }
            contentType(ContentType.Application.Json)
            setBody(body)
        }

        println(response.status)
        println(response.bodyAsText())

        if (!response.status.isSuccess()) {
            val responseBody = response.body<OpenAIChatCompletionsResponseErrorDto>()
            throw GenerateDocCommentException(responseBody.error.message)
        }


        val responseBody = response.body<OpenAIChatCompletionsResponseDto>()

        return responseBody.choices.first().message.content
    }
}