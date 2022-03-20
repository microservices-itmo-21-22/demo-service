package com.itmo.microservices.demo.bombardier.external.knownServices

import com.itmo.microservices.demo.bombardier.BombardierProperties
import com.itmo.microservices.demo.bombardier.ServiceDescriptor
import com.itmo.microservices.demo.bombardier.external.ExternalServiceApi
import com.itmo.microservices.demo.bombardier.external.RealExternalService
import com.itmo.microservices.demo.bombardier.external.storage.UserStorage
import com.itmo.microservices.demo.bombardier.flow.UserManagement
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class ServiceDescriptorNotFoundException(name: String) : Exception("Descriptor for service $name was not found")

data class ServiceWithApiAndAdditional(val api: ExternalServiceApi, val userManagement: UserManagement)


@Service
class KnownServices(private val props: BombardierProperties) {
    private val storage = mutableListOf<ServiceDescriptor>()
    private val apis = mutableMapOf<ServiceDescriptor, ServiceWithApiAndAdditional>() // todo make concurrent

    init {
        storage.addAll(props.getDescriptors())
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
            val api = RealExternalService(descriptor, UserStorage(), props)
            ServiceWithApiAndAdditional(api, UserManagement(api))
        }
    }
}