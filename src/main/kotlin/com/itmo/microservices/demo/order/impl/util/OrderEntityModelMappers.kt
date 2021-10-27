import com.itmo.microservices.demo.order.api.model.OrderDto
import com.itmo.microservices.demo.order.impl.entity.OrderEntity
import com.itmo.microservices.demo.tasks.api.model.TaskModel

fun OrderDto.toEntity(): OrderEntity = OrderEntity(
    id = this.id,
    userId = this.userId,
    timeCreated = this.timeCreated,
    status = this.status,
    itemsMap = this.itemsMap,
    deliveryDuration = this.deliveryDuration,
    paymentHistory = this.paymentHistory
)

fun OrderEntity.toModel(): OrderDto = OrderDto(
    id = this.id,
    userId = this.userId,
    timeCreated = this.timeCreated,
    status = this.status,
    itemsMap = this.itemsMap,
    deliveryDuration = this.deliveryDuration,
    paymentHistory = this.paymentHistory
)