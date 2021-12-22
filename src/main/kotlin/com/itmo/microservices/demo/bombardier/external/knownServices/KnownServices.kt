package com.itmo.microservices.demo.bombardier.external.knownServices

import com.itmo.microservices.demo.bombardier.external.ExternalServiceApi
import com.itmo.microservices.demo.bombardier.external.RealExternalService
import com.itmo.microservices.demo.bombardier.external.storage.UserStorage
import com.itmo.microservices.demo.bombardier.flow.UserManagement
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.Exception
import java.net.URL

@ResponseStatus(HttpStatus.NOT_FOUND)
class ServiceDescriptorNotFoundException(name: String) : Exception("Descriptor for service $name was not found")

class ServiceDescriptor(val name: String, val teamName: String, private val url: URL, private val internalAddress: URL) {
    fun getServiceAddress(): URL {
        val isInternal = !System.getProperty("is.local", "false").toBoolean()
        LoggerFactory.getLogger(ServiceDescriptor::class.java).info(isInternal)
        return if (isInternal) internalAddress else url
    }
}

data class ServiceWithApiAndAdditional(val api: ExternalServiceApi, val userManagement: UserManagement)

class KnownServices(vararg descriptors: ServiceDescriptor) {
    companion object {
        fun getInstance() = KnownServicesStorage
    }

    private val storage = mutableListOf<ServiceDescriptor>()
    private val apis = mutableMapOf<ServiceDescriptor, ServiceWithApiAndAdditional>() // todo make concurrent

    init {
        storage.addAll(descriptors)
    }

    fun add(descriptor: ServiceDescriptor) {
        storage.add(descriptor)
    }

    fun descriptorFromName(name: String): ServiceDescriptor {
        return storage.firstOrNull { it.name == name } ?: throw ServiceDescriptorNotFoundException(name)
    }

    fun getStuff(name: String): ServiceWithApiAndAdditional {
        val descriptor = descriptorFromName(name)
        return apis.getOrPut(descriptor) {
            val api = RealExternalService(descriptor, UserStorage())
            ServiceWithApiAndAdditional(api, UserManagement(api))
        }
    }
}

val KnownServicesStorage = KnownServices(
    //ServiceDescriptor("p02", "p02", URL("http://77.234.215.138:30012"), URL("http://p02")),
    ServiceDescriptor("p03", "p03", URL("http://77.234.215.138:30013"), URL("http://p03")),
    ServiceDescriptor("p04", "p04", URL("http://77.234.215.138:30014"), URL("http://p04")),
    ServiceDescriptor("p05", "p05", URL("http://77.234.215.138:30015"), URL("http://p05")),
    ServiceDescriptor("p07", "p07", URL("http://77.234.215.138:30017"), URL("http://p07")),
    ServiceDescriptor("p08", "p08", URL("http://77.234.215.138:30018"), URL("http://p08")),
    ServiceDescriptor("p09", "p09", URL("http://77.234.215.138:30019"), URL("http://p09")),
    ServiceDescriptor("p10", "p10", URL("http://77.234.215.138:30020"), URL("http://p10")),
    ServiceDescriptor("p11", "p11", URL("http://77.234.215.138:30021"), URL("http://p11")),
    ServiceDescriptor("p12", "p12", URL("http://77.234.215.138:30022"), URL("http://p12")),
    ServiceDescriptor("p81", "p81", URL("http://77.234.215.138:30023"), URL("http://p81")),
)