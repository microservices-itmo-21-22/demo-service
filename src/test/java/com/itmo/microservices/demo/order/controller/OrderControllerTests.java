package java.com.itmo.microservices.demo.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itmo.microservices.demo.order.api.controller.OrderController;
import com.itmo.microservices.demo.order.api.service.OrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(OrderController.class)
public class OrderControllerTests {
	@MockBean
	OrderService orderService;

	ObjectMapper mapper = new ObjectMapper();

	@Autowired
	MockMvc mockMvc;

	@Test
	public void testCreateNewOrder() throws Exception {
		UserDetails user = new User("username", "password", new ArrayList<>());
		mockMvc.perform(get("")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(user)))
				.andExpect(status().isOk());
	}
}
