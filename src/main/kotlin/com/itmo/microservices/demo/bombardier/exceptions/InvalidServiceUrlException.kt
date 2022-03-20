package com.itmo.microservices.demo.bombardier.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class InvalidServiceUrlException : java.lang.Exception("the url to service is invalid")