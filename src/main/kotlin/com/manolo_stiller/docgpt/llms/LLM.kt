package com.manolo_stiller.docgpt.llms

import com.manolo_stiller.docgpt.llms.gemini.Gemini
import com.manolo_stiller.docgpt.llms.openai.OpenAI
import com.manolo_stiller.docgpt.models.SupportedLLMs
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

abstract class LLM {
    abstract val apiKey: String

    companion object {
        fun instantiateForLLM(llm: SupportedLLMs, apiKey: String): LLM {
            return when (llm) {
                SupportedLLMs.OpenAI -> OpenAI(apiKey)
                SupportedLLMs.Gemini -> Gemini(apiKey)
            }
        }
    }

    protected val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    abstract suspend fun generateDocComment(
        functionAsString: String,
        programmingLanguage: String,
        model: String,
        maxTokens: Int? = null,
    ): String

    protected fun docInstruction(): String {
        return """
            You are an Codegenerator that generates doc comments for functions in various programming languages. The user will provide you with the programming language and a function, and you need to respond only with the appropriate raw doc comment for that function. Do not include the function itself in your response. Ensure the doc comment is detailed, covering the purpose of the function, descriptions of its parameters, and the return value if applicable. Adapt the comment format based on the provided programming language. Input Format:
            Language:
            [Programming Language]

            Function:
            [Function Code]
        """
    }
}