package com.itmo.microservices.demo.bombardier.external.knownServices

import com.itmo.microservices.demo.bombardier.external.ExternalServiceApi
import com.itmo.microservices.demo.bombardier.external.RealExternalService
import com.itmo.microservices.demo.bombardier.external.storage.UserStorage
import com.itmo.microservices.demo.bombardier.flow.UserManagement
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.Exception
import java.net.URL

@ResponseStatus(HttpStatus.NOT_FOUND)
class ServiceDescriptorNotFoundException(name: String) : Exception("Descriptor for service $name was not found")

data class ServiceDescriptor(val name: String, val teamName: String, val url: URL) // name is unique

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

    fun add(name: String, teamName: String, url: URL) {
        add(ServiceDescriptor(name, teamName, url))
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
    ServiceDescriptor("p02", "p02", URL("http://77.234.215.138:30012")),
    ServiceDescriptor("p03", "p03", URL("http://77.234.215.138:30013")),
    ServiceDescriptor("p04", "p04", URL("http://77.234.215.138:30014")),
    ServiceDescriptor("p05", "p05", URL("http://77.234.215.138:30015")),
    ServiceDescriptor("p07", "p07", URL("http://77.234.215.138:30017")),
    ServiceDescriptor("p08", "p08", URL("http://77.234.215.138:30018")),
    ServiceDescriptor("p10", "p10", URL("http://77.234.215.138:30020")),
    ServiceDescriptor("p11", "p11", URL("http://77.234.215.138:30021")),
    ServiceDescriptor("p12", "p12", URL("http://77.234.215.138:30022")),
    ServiceDescriptor("p81", "p81", URL("http://77.234.215.138:30023")),
)