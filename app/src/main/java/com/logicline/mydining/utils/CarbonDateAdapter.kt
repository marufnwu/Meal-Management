package com.logicline.mydining.utils

import com.google.gson.*
import java.lang.reflect.Type

class CarbonDateAdapter : JsonDeserializer<CarbonDate>, JsonSerializer<CarbonDate> {
    
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): CarbonDate {
        return CarbonDate(json.asString)
    }
    
    override fun serialize(src: CarbonDate, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.raw)
    }
} 