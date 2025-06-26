package com.example.autocompose.domain.responseModel

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.JsonAdapter
import java.lang.reflect.Type

data class BackendResponse(
    @JsonAdapter(EmailResponseDeserializer::class)
    val email: EmailResponse
)

data class EmailResponse(
    val subject: String,
    val body: String
)

class EmailResponseDeserializer : JsonDeserializer<EmailResponse> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): EmailResponse {
        // Handle both cases: if email is an object or if it's a string
        return if (json.isJsonObject) {
            val obj = json.asJsonObject
            EmailResponse(
                subject = obj.get("subject")?.asString ?: "",
                body = obj.get("body")?.asString ?: ""
            )
        } else {
            // If it's a string, use empty subject and the string as body
            EmailResponse(
                subject = "",
                body = json.asString
            )
        }
    }
}