package com.itmo.microservices.demo.categories.impl.logging

import com.itmo.microservices.commonlib.logging.NotableEvent

enum class CategoryServiceNotableEvents(private val template: String) : NotableEvent {
    I_CATEGORY_CREATED("Category created: {}"),
    I_CATEGORY_DELETED("Category deleted: {}");

    override fun getTemplate(): String {
        return template
    }

    override fun getName(): String {
        return name
    }
}