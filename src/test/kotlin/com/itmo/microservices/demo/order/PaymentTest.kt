import com.itmo.microservices.demo.order.api.model.BusketModel
import com.itmo.microservices.demo.order.api.model.OrderModel
import com.itmo.microservices.demo.order.impl.entity.OrderEntity
import com.itmo.microservices.demo.order.impl.service.OrderServiceImpl
import com.itmo.microservices.demo.order.impl.util.toModel
import com.itmo.microservices.demo.payments.api.model.PaymentModel
import com.itmo.microservices.demo.payments.impl.entity.Payment
import com.itmo.microservices.demo.payments.impl.repository.PaymentRepository
import com.itmo.microservices.demo.payments.impl.service.DefaultPaymentService
import com.itmo.microservices.demo.payments.impl.util.toModel
import com.itmo.microservices.demo.users.impl.repository.UserRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

class PaymentTest {

    private val paymentRepository = Mockito.mock(PaymentRepository::class.java)
    private val paymentId = UUID.randomUUID()

    private fun paymentMock(): Payment {
        return Payment(date = Date(2021, 3, 4), status = 0, username = "user1" ).also { it.id = paymentId }
    }

    @Test
    fun getUserTransactionsInfoTest() {
        val paymentService = DefaultPaymentService(paymentRepository)
        Mockito.`when`(paymentRepository.findAllByUsername("user1")).thenReturn(mutableListOf(paymentMock()))
        val userDetails = Mockito.mock(UserDetails::class.java)
        Mockito.`when`(userDetails.username).thenReturn("user1")
        val actual = paymentService.getUserTransactionsInfo(userDetails)
        val expected = mutableListOf(paymentMock().toModel())
        Assertions.assertEquals(actual, expected)
    }

    @Test
    fun refundTest(){
        val paymentService = DefaultPaymentService(paymentRepository)
        Mockito.`when`(paymentRepository.findByIdOrNull(paymentId)).thenReturn(paymentMock().also { it.status = 1 })
        val userDetails = Mockito.mock(UserDetails::class.java)
        Mockito.`when`(userDetails.username).thenReturn("user1")
        paymentService.refund(paymentId, userDetails)
        val actual = paymentRepository.findByIdOrNull(paymentId)!!.toModel()
        val expected = paymentMock().also { it.status = 1 }.toModel()
        Assertions.assertEquals(actual, expected)
        
    }

    @Test
    fun payTest(){
        val paymentService = DefaultPaymentService(paymentRepository)

        val actual = paymentMock().toModel()
        val paymentEntity = Payment(actual.date, actual.status, actual.username).also { it.id = actual.id }

        Mockito.`when`(paymentRepository.save(Mockito.any())).thenReturn(paymentEntity)
        val user = Mockito.mock(UserDetails::class.java)
        Mockito.`when`(user.username).thenReturn("user1")

        val expected = paymentService.pay(user)

        Assertions.assertEquals(actual.username, expected.username)
    }



}