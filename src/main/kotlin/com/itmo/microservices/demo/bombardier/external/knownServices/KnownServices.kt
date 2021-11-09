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
    private val apis = mutableMapOf<ServiceDescriptor, ServiceWithApiAndAdditional>()

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
    ServiceDescriptor("DemoService", "DemoServiceTeam", URL("https://vk.com/baneks"))
)