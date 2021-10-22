package com.itmo.microservices.demo.categories.impl.service

import com.google.common.eventbus.EventBus
import com.itmo.microservices.commonlib.annotations.InjectEventLogger
import com.itmo.microservices.commonlib.logging.EventLogger
import com.itmo.microservices.demo.categories.api.messaging.CategoryDeletedEvent
import com.itmo.microservices.demo.categories.api.service.CategoryService
import com.itmo.microservices.demo.categories.impl.logging.CategoryServiceNotableEvents
import com.itmo.microservices.demo.categories.impl.repository.CategoryRepository
import com.itmo.microservices.demo.categories.impl.util.toEntity
import com.itmo.microservices.demo.categories.impl.util.toModel
import com.itmo.microservices.demo.common.exception.NotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import com.itmo.microservices.demo.categories.api.model.CategoryModel
import java.util.*

@Service
@Suppress("UnstableApiUsage")
class DefaultCategoryService (private val categoryRepository: CategoryRepository,
                               private val eventBus: EventBus
) : CategoryService {

    @InjectEventLogger
    private lateinit var eventLogger: EventLogger

    override fun allCategories(): List<CategoryModel> = categoryRepository.findAll()
        .map { it.toModel() }

    override fun getCategoryById(categoryId: UUID): CategoryModel =
        categoryRepository.findByIdOrNull(categoryId)?.toModel()
            ?: throw NotFoundException("Category $categoryId not found")

    override fun addCategory(category: CategoryModel) {
        val entity = category.toEntity()
        categoryRepository.save(entity)
        eventBus.post(CategoryDeletedEvent(entity.toModel()))
        eventLogger.info(
            CategoryServiceNotableEvents.I_CATEGORY_CREATED,
            entity
        )
    }

    override fun deleteCategoryById(categoryId: UUID) {
        val category = categoryRepository.findByIdOrNull(categoryId) ?: return
        categoryRepository.deleteById(categoryId)
        eventBus.post(CategoryDeletedEvent(category.toModel()))
        eventLogger.info(
            CategoryServiceNotableEvents.I_CATEGORY_DELETED,
            category
        )
    }
}