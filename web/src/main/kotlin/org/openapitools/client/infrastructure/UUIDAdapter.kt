package org.openapitools.client.infrastructure

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.util.UUID

class UUIDAdapter: JsonAdapter<UUID>() {
    override fun fromJson(reader: JsonReader): UUID? {
        return UUID.fromString(reader.nextString())
    }
    override fun toJson(writer: JsonWriter, value: UUID?) {
        writer.value(value.toString())
    }
}
