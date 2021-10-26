import com.itmo.microservices.demo.order.api.model.OrderDto
import com.itmo.microservices.demo.order.impl.entity.OrderEntity
import com.itmo.microservices.demo.tasks.api.model.TaskModel

fun OrderEntity.toModel(): OrderDto = OrderDto(
    id = this.id?: throw NullPointerException("Id can not be null"),
    userId = this.userId?: throw NullPointerException("userId can not be null"),
    timeCreated = timeCreated,
    status = status?: throw NullPointerException("status can not be null"),
    itemsMap = ,
    deliveryDuration = deliveryDuration,

)