package org.openapitools.client.infrastructure

import android.util.Base64
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson

class ByteArrayAdapter : JsonAdapter<ByteArray>() {
    override fun fromJson(reader: JsonReader): ByteArray? {
        return Base64.decode(reader.nextString(), Base64.DEFAULT)
    }
    override fun toJson(writer: JsonWriter, value: ByteArray?) {
        writer.value(Base64.encodeToString(value, Base64.DEFAULT))
    }
}
