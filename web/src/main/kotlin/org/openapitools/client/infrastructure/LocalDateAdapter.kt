package org.openapitools.client.infrastructure

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

class LocalDateAdapter: JsonAdapter<LocalDate>() {
    override fun fromJson(reader: JsonReader): LocalDate? {
        return LocalDate.parse(reader.nextString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }
    override fun toJson(writer: JsonWriter, value: LocalDate?) {
        writer.value(value?.format(DateTimeFormatter.ISO_LOCAL_DATE))
    }
}

