package com.manolo_stiller.docgpt.models

enum class SupportedLLMs {
    OpenAI {
        override fun id(): String {
            return "open_ai"
        }

        override fun title(): String {
            return "Open AI"
        }

        override fun defaultModel(): String {
            return "gpt-4o-mini"
        }
    },
    Gemini {
        override fun id(): String {
            return "gemini"
        }

        override fun title(): String {
            return "Gemini"
        }

        override fun defaultModel(): String {
            return "gemini-1.5-flash"
        }
    };

    abstract fun id(): String
    abstract fun title(): String
    abstract fun defaultModel(): String
}

//class SupportedLLMs {
//    companion object {
//        const val OPEN_AI_ID = "open_ai"
//        const val GEMINI_ID = "gemini"
//
//        val list = arrayOf(
//            SupportedLLM(id = OPEN_AI_ID, title = "Open AI"),
//            SupportedLLM(id = GEMINI_ID, title = "Gemini")
//        )
//    }
//
//    data class SupportedLLM(val id: String, val title: String) {
//        override fun toString(): String {
//            return this.title
//        }
//    }
//}



