package com.itmo.microservices.demo.bombardier.external.communicator

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class BombardierMappingExceptionWithUrl(url: String, content: String, originalException: Throwable) : Exception("""
    URL: $url
    $content
""".trimIndent(), originalException)

class BombardierMappingException(content: String, clazz: Class<*>, originalException: Throwable) : Exception(
    """
        MAPPING EXCEPTION
        
        Original request
        $content
        
        Excepted to parse to
        ${clazz.name}
        
        with fields
        ${clazz.declaredFields.map { "${it.name}: ${it.type.name}" }.joinToString("\n\t")}
        
        Original exception message
        ${originalException.message}
    """.trimIndent(),
    originalException
) {
    fun exceptionWithUrl(url: String) = BombardierMappingExceptionWithUrl(url, message!!, cause!!)
}

val mapper = jacksonObjectMapper().apply {
    findAndRegisterModules()
}

inline fun <reified T> readValueBombardier(content: String): T {
    return try {
        mapper.readValue(content, object : TypeReference<T>(){})
    }
    catch (t: JsonProcessingException) {
        throw BombardierMappingException(content, T::class.java, t)
    }
}