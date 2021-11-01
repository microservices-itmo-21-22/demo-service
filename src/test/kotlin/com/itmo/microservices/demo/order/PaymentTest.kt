import com.itmo.microservices.demo.payments.impl.repository.PaymentRepository
import org.mockito.Mockito
import java.util.*

class PaymentTest {

    private val paymentRepository = Mockito.mock(PaymentRepository::class.java)
    private val paymentId = UUID.randomUUID()

}