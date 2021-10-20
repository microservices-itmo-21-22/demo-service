package com.itmo.microservices.demo.categories.impl.repository

import com.itmo.microservices.demo.categories.impl.entity.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CategoryRepository : JpaRepository<Category, UUID> {

}