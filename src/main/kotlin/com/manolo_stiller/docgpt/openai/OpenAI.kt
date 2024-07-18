package com.manolo_stiller.docgpt.openai

import com.manolo_stiller.docgpt.exceptions.GenerateDocCommentException
import com.manolo_stiller.docgpt.openai.dtos.OpenAIChatCompletionsMessageRequestDto
import com.manolo_stiller.docgpt.openai.dtos.OpenAIChatCompletionsRequestDto
import com.manolo_stiller.docgpt.openai.dtos.OpenAIChatCompletionsResponseDto
import com.manolo_stiller.docgpt.openai.dtos.OpenAIChatCompletionsResponseErrorDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class OpenAI(private val apiKey: String) {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun generateDocComment(
        functionAsString: String,
        programmingLanguage: String,
        model: String = "gpt-3.5-turbo",
        maxTokens: Int? = null,
    ): String {
        val body = OpenAIChatCompletionsRequestDto(
            model = model,
            maxTokens = maxTokens,
            messages = listOf(
                OpenAIChatCompletionsMessageRequestDto(
                    role = "system",
                    content = docInstruction(),
                ),
                OpenAIChatCompletionsMessageRequestDto(
                    role = "user",
                    content = "Language:\n$programmingLanguage\n\nFunction:\n$functionAsString",
                ),
            ),
        )

        val response = client.post("https://api.openai.com/v1/chat/completions") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $apiKey")
            }
            contentType(ContentType.Application.Json)
            setBody(body)
        }

        if (!response.status.isSuccess()) {
            val responseBody = response.body<OpenAIChatCompletionsResponseErrorDto>()
            throw GenerateDocCommentException(responseBody.error.message)
        }


        val responseBody = response.body<OpenAIChatCompletionsResponseDto>()

        return responseBody.choices.first().message.content
    }

    private fun docInstruction(): String {
        return """
            You are an AI that generates doc comments for functions in various programming languages. The user will provide you with the programming language and a function, and you need to respond only with the appropriate doc comment for that function. Do not include the function itself in your response. Ensure the doc comment is detailed, covering the purpose of the function, descriptions of its parameters, and the return value if applicable. Adapt the comment format based on the provided programming language. Input Format:
            Language:
            [Programming Language]

            Function:
            [Function Code]
        """
    }
}