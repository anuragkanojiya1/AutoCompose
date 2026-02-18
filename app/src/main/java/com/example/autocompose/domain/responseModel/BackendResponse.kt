package com.example.autocompose.domain.responseModel

import androidx.annotation.Keep
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

@Keep
data class BackendResponse(
    @SerializedName("email")
    @JsonAdapter(EmailResponseDeserializer::class)
    val email: EmailResponse
)

@Keep
data class EmailResponse(
    @SerializedName("subject")
    val subject: String,
    @SerializedName("body")
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