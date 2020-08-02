package com.coffee.ordertdd.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coffee.ordertdd.model.Order;
import com.coffee.ordertdd.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class OrdertddApplicationTests {

	@MockBean
	private OrderService orderService;

	@Autowired
	private MockMvc mvc;

	@Test
	@DisplayName("Test Order found - GET /Orders/1")
	void getOrderById() throws Exception {

		// Prepare mock Order
		Order mockOrder = Order.builder()
																					.id(1)
																					.name("My order")
																					.description("Details of my order")
																					.quantity(5)
																					.version(1)
																					.build();

		// Prepare moked service method
		Mockito.doReturn(mockOrder).when(orderService).findById(mockOrder.getId());

		// Perform GET request
		mvc.perform(MockMvcRequestBuilders.get("/orders/{id}", 1))
				// Validate 200 OK and JSON response type received
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

				// Validate response headers
				.andExpect(header().string(HttpHeaders.ETAG, "\"1\""))
				.andExpect(header().string(HttpHeaders.LOCATION, "/orders/1"))

				// Validate response body
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.name", is("My order")))
				.andExpect(jsonPath("$.description", is("Details of my order")))
				.andExpect(jsonPath("$.quantity", is(5)))
				.andExpect(jsonPath("$.version", is(1)));

	}

	@Test
	@DisplayName("Tests all orders found - GET /orders")
	void getAllOrders() throws Exception{

		// Prepare mock orders
		Order firstOrder = Order.builder()
				.id(1)
				.name("First order")
				.description("First order description")
				.quantity(8)
				.version(1)
				.build();

		Order secondOrder = Order.builder()
				.id(2)
				.name("Second order")
				.description("Second order description")
				.quantity(10)
				.version(1)
				.build();

		List<Order> orders = new ArrayList<>();
		orders.add(firstOrder);
		orders.add(secondOrder);

		// Prepare mock service method
		Mockito.doReturn(orders).when(orderService).findAll();

		// Perform GET request
		mvc.perform(MockMvcRequestBuilders.get("/orders"))
				// Validate 200 OK and JSON response type received
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

				// Validate response body
				.andExpect(jsonPath("[0].name", is("First order")))
				.andExpect(jsonPath("[1].name", is("Second order")));
	}

	@Test
	@DisplayName("Add a new order = POST /orders")
	void addNewOrder() throws Exception {
		// Prepare mock order
		Order newOrder = Order.builder()
				.id(1)
				.name("New order")
				.description("New order description")
				.quantity(8)
				.build();

		Order mockOrder = Order.builder()
				.id(1)
				.name("New order")
				.description("New order description")
				.quantity(8)
				.version(1)
				.build();

		// Prepare mock service method
		Mockito.doReturn(mockOrder).when(orderService).save(newOrder);

		// Perform POST request
		mvc.perform(MockMvcRequestBuilders.post("/orders")
								.contentType(MediaType.APPLICATION_JSON_VALUE)
								.content(new ObjectMapper().writeValueAsString(newOrder)))

				// Validate 201 CREATED and JSON response type received
				.andExpect(header().string(HttpHeaders.ETAG, "\"1\""))
				.andExpect(header().string(HttpHeaders.LOCATION, "/orders/1"))

				// Validate response body
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.name", is("New order")))
				.andExpect(jsonPath("$.quantity", is(8)))
				.andExpect(jsonPath("$.version", is(1)));
	}

	@Test
	@DisplayName("Update an existing order with success - PUT /orders/1")
	public void testUpdatingOrderWithSuccess() throws Exception {
		// Prepare mock order
		Order orderToUpdate = Order.builder().name("New name").description("New description").quantity(20).build();
		Order mockOrder = Order.builder().id(1).name("Mock order").description("Mock order desc").quantity(10).version(1).build();

		// Prepare mock service methods
		Mockito.doReturn(mockOrder).when(orderService).findById(1);
		Mockito.doReturn(mockOrder).when(orderService).update(ArgumentMatchers.any());

		// Perform PUT request
		mvc.perform(put("/orders/{id}", 1)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.header(HttpHeaders.IF_MATCH, 1)
				.content(new ObjectMapper().writeValueAsString(orderToUpdate)))

				// Validate 200 OK and JSON response type received
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

				// Validate response headers
				.andExpect(header().string(HttpHeaders.ETAG, "\"2\""))
				.andExpect(header().string(HttpHeaders.LOCATION, "/orders/1"))

				// Validate response body
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.name", is("New name")))
				.andExpect(jsonPath("$.quantity", is(20)));
	}

	@Test
	@DisplayName("Version mismatch while updating existing order - PUT /orders/1")
	public void testVersionMismatchWhileUpdating() throws Exception {
		// Prepare mock order
		Order orderToUpdate = Order.builder().name("New name").description("New description").quantity(20).build();
		Order mockOrder = Order.builder().id(1).name("Mock order").description("Mock order desc").quantity(10).version(2).build();

		// Prepare mock service method
		Mockito.doReturn(mockOrder).when(orderService).findById(1);

		// Perform PUT request
		mvc.perform(put("/orders/{id}", 1)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.header(HttpHeaders.IF_MATCH, 1)
				.content(new ObjectMapper().writeValueAsString(orderToUpdate)))

				// Validate 409 CONFLICT received
				.andExpect(status().isConflict());
	}

	@Test
	@DisplayName("order not found while updating - PUT /orders/1")
	public void testOrderNotFoundWhileUpdating() throws Exception{
		// Prepare mock order
		Order orderToUpdate = Order.builder().name("New name").description("New description").quantity(20).build();

		// Prepare mock service method
		Mockito.doReturn(null).when(orderService).findById(1);

		// Perform PUT request
		mvc.perform(put("/orders/{id}", 1)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.header(HttpHeaders.IF_MATCH, 1)
				.content(new ObjectMapper().writeValueAsString(orderToUpdate)))

				// Validate 404 NOT_FOUND received
				.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("Delete a order successfully - DELETE /orders/1")
	public void testOrderDeletedSuccessfully() throws Exception {
		// Prepare mock order
		Order existingOrder = Order.builder().id(1).name("New name").description("New description").quantity(20).version(1).build();

		// Prepare mock service method
		Mockito.doReturn(existingOrder).when(orderService).findById(1);

		// Perform DELETE request
		mvc.perform(delete("/orders/{id}", 1))
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("Fail to delete an non-existing order - DELETE /orders/1")
	public void testFailureToDeleteNonExistingOrder() throws Exception {
		// Prepare mock service method
		Mockito.doReturn(null).when(orderService).findById(1);

		// Perform DELETE request
		mvc.perform(delete("/orders/{id}", 1))
				.andExpect(status().isNotFound());
	}

}
