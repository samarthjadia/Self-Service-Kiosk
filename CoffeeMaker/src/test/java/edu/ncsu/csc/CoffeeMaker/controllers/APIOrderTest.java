/**
 *
 */
package edu.ncsu.csc.CoffeeMaker.controllers;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Order;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;
import edu.ncsu.csc.CoffeeMaker.services.OrderService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

/**
 * Test Class for Order controller
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith ( SpringExtension.class )
class APIOrderTest {

    @Autowired
    private OrderService          orderService;

    @Autowired
    private RecipeService         recipeService;

    @Autowired
    private IngredientService     ingredientService;

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        orderService.deleteAll();
        recipeService.deleteAll();
        ingredientService.deleteAll();

        ingredientService.save( new Ingredient( "Coffee", 20 ) );
        ingredientService.save( new Ingredient( "Milk", 20 ) );
        ingredientService.save( new Ingredient( "Sugar", 20 ) );
        ingredientService.save( new Ingredient( "Chocolate", 20 ) );

        final Recipe r = new Recipe();
        r.setName( "Mocha" );
        r.setPrice( 10 );
        r.addIngredient( ingredientService.findByName( "Coffee" ), 3 );
        r.addIngredient( ingredientService.findByName( "Milk" ), 4 );
        r.addIngredient( ingredientService.findByName( "Sugar" ), 8 );
        r.addIngredient( ingredientService.findByName( "Chocolate" ), 5 );

        recipeService.save( r );
    }

    /**
     * Tests successfully placing an order / saving an order as unfulfilled in
     * the database.
     *
     * @throws Exception
     */
    @Test
    @Transactional
    void placeOrderTestValid () throws Exception {
        final Order mySpecialOrder = new Order( "lebronjames", "lebron", "Mocha", 11, false, false );

        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( mySpecialOrder ) ) ).andExpect( status().isOk() );
    }

    /**
     * Tests placing an order with insufficient funds. (INVALID)
     *
     * @throws Exception
     */
    @Test
    @Transactional
    void placeOrderTestInsufficientFundsInvalid () throws Exception {
        final Order mySpecialOrder = new Order( "lebronjames", "lebron", "Mocha", 9, false, false );

        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( mySpecialOrder ) ) ).andExpect( status().isConflict() );
    }

    /**
     * Tests placing an order with a non-existant recipe. (INVALID)
     *
     * @throws Exception
     */
    @Test
    @Transactional
    void placeOrderTestInvalidRecipeInvalid () throws Exception {
        final Order mySpecialOrder = new Order( "lebronjames", "lebron", "Cheese", 11, false, false );

        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( mySpecialOrder ) ) ).andExpect( status().isNotFound() );
    }

    /**
     * Tests getting a list of orders from every customer. Also tests getting an
     * empty list of orders.
     *
     * @throws Exception
     */
    @Test
    @Transactional
    void getOrdersTestValid () throws Exception {
        // Check for empty list of orders, as there are no current orders
        mvc.perform( get( "/api/v1/orders" ) ).andExpect( status().isOk() ).andExpect( jsonPath( "$", hasSize( 0 ) ) );

        // Create Orders
        final Order order1 = new Order( "lebronjames", "lebron", "Mocha", 11, false, false );
        final Order order2 = new Order( "lebronjames", "lebron", "Mocha", 15, false, false );
        final Order order3 = new Order( "dloading", "dlo", "Mocha", 12, false, false );
        final Order order4 = new Order( "dloading", "dlo", "Mocha", 11, false, false );
        final Order order5 = new Order( "dloading", "dlo", "Mocha", 12, false, false );

        // Place orders
        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order1 ) ) ).andExpect( status().isOk() );
        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order2 ) ) ).andExpect( status().isOk() );
        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order3 ) ) ).andExpect( status().isOk() );
        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order4 ) ) ).andExpect( status().isOk() );
        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order5 ) ) ).andExpect( status().isOk() );

        // Get orders
        mvc.perform( get( "/api/v1/orders" ) ).andExpect( status().isOk() ).andExpect( jsonPath( "$", hasSize( 5 ) ) );
    }

    /**
     * Tests getting fulfilled and unfulfillled orders.
     *
     * @throws Exception
     */
    @Test
    @Transactional
    void getFullfillingOrdersValid () throws Exception {

        // Create Order
        final Order order1 = new Order( "lebronjames", "lebron", "Mocha", 11, false, false );

        final Order order2 = new Order( "lebronjames", "lebron", "Mocha", 11, false, false );

        // Place order
        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order1 ) ) ).andExpect( status().isOk() );
        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order2 ) ) ).andExpect( status().isOk() );

        // Get unfulfilled orders; should be 2
        mvc.perform( get( "/api/v1/orders/status/0" ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$", hasSize( 2 ) ) );

        // Get fulfilled orders; should be 0
        mvc.perform( get( "/api/v1/orders/status/1" ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$", hasSize( 0 ) ) );
    }

    /**
     * Tests gets orders from a specific customer. Also tests getting customer
     * orders for 2 different customers but with the same name.
     *
     * @throws Exception
     */
    @Test
    @Transactional
    void getCustomerOrdersTestValid () throws Exception {

        // Create Order
        final Order order1 = new Order( "lebronjames", "lebron", "Mocha", 11, false, false );
        final Order order2 = new Order( "lebronjames", "bronny", "Mocha", 11, false, false );

        final Order order3 = new Order( "lebronjamesAlias", "lebron", "Mocha", 11, false, false );

        // Place order
        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order1 ) ) ).andExpect( status().isOk() );
        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order2 ) ) ).andExpect( status().isOk() );
        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order3 ) ) ).andExpect( status().isOk() );

        // Get orders from each user
        mvc.perform( get( "/api/v1/orders/lebronjames" ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$", hasSize( 2 ) ) );
        mvc.perform( get( "/api/v1/orders/lebronjamesAlias" ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$", hasSize( 1 ) ) );
    }

    /**
     * Tests fulfilling an order and picking up an order. (VALID)
     *
     * @throws Exception
     */
    @Test
    @Transactional
    void updateOrderTestValid () throws Exception {

        // Create Order
        final Order order1 = new Order( "lebronjames", "lebron", "Mocha", 11, false, false );

        // Place order
        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order1 ) ) ).andExpect( status().isOk() );

        // Get unfulfilled orders; should be 1
        final MvcResult result = mvc.perform( get( "/api/v1/orders/status/0" ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$", hasSize( 1 ) ) ).andReturn();
        final String jsonResponse = result.getResponse().getContentAsString();

        final ObjectMapper objectMapper = new ObjectMapper();
        final List<Order> orders = Arrays.asList( objectMapper.readValue( jsonResponse, Order[].class ) );
        assertEquals( (Integer) 1, orders.size() );
        // Fulfill order
        orders.get( 0 ).setFulfilled( true );
        mvc.perform( put( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( orders.get( 0 ) ) ) ).andExpect( status().isOk() );

        // Get unfulfilled orders; should be 0
        mvc.perform( get( "/api/v1/orders/status/0" ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$", hasSize( 0 ) ) );
        // Get fulfilled orders; should be 1
        mvc.perform( get( "/api/v1/orders/status/1" ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$", hasSize( 1 ) ) );

    }

    /**
     * Tests updating an order that does not exist. (INVALID)
     *
     * @throws Exception
     */
    @Test
    @Transactional
    void updateOrderDoesNotExistTestInvalid () throws Exception {

        // Create Order
        final Order order1 = new Order( "lebronjames", "lebron", "Mocha", 11, false, false );

        // Updated order that hasn't been placed
        mvc.perform( put( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order1 ) ) ).andExpect( status().isNotFound() );

        // Ensure that there are no orders in the system
        mvc.perform( get( "/api/v1/orders" ) ).andExpect( status().isOk() ).andExpect( jsonPath( "$", hasSize( 0 ) ) );

    }

    /**
     * Tests updating an order and also changing another element of the order
     * (payment) which is invalid. (INVALID)
     *
     * @throws Exception
     */
    @Test
    @Transactional
    void updateOrderChangePaymentTestInvalid () throws Exception {

        // Create Order
        final Order order1 = new Order( "lebronjames", "lebron", "Mocha", 11, false, false );

        // Place order
        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order1 ) ) ).andExpect( status().isOk() );

        // Get unfulfilled orders; should be 1
        final MvcResult result = mvc.perform( get( "/api/v1/orders/status/0" ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$", hasSize( 1 ) ) ).andReturn();
        final String jsonResponse = result.getResponse().getContentAsString();

        final ObjectMapper objectMapper = new ObjectMapper();
        final List<Order> orders = Arrays.asList( objectMapper.readValue( jsonResponse, Order[].class ) );
        assertEquals( (Integer) 1, orders.size() );
        // Fulfill order
        orders.get( 0 ).setFulfilled( true );
        orders.get( 0 ).setPayment( 12 ); // Change payment
        mvc.perform( put( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( orders.get( 0 ) ) ) ).andExpect( status().isConflict() );

        // Get unfulfilled orders; should be 1
        mvc.perform( get( "/api/v1/orders/status/0" ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$", hasSize( 1 ) ) );

    }

    /**
     * Tests updating an order but not changing anything. (INVALID)
     *
     * @throws Exception
     */
    @Test
    @Transactional
    void updateOrderNoChangesTestInvalid () throws Exception {

        // Create Order
        final Order order1 = new Order( "lebronjames", "lebron", "Mocha", 11, false, false );

        // Place order
        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order1 ) ) ).andExpect( status().isOk() );

        // Get unfulfilled orders; should be 1
        final MvcResult result = mvc.perform( get( "/api/v1/orders/status/0" ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$", hasSize( 1 ) ) ).andReturn();
        final String jsonResponse = result.getResponse().getContentAsString();

        final ObjectMapper objectMapper = new ObjectMapper();
        final List<Order> orders = Arrays.asList( objectMapper.readValue( jsonResponse, Order[].class ) );
        assertEquals( (Integer) 1, orders.size() );

        // Send request with nothing changed
        mvc.perform( put( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( orders.get( 0 ) ) ) ).andExpect( status().isConflict() );

        // Get unfulfilled orders; should be 0
        mvc.perform( get( "/api/v1/orders/status/0" ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$", hasSize( 1 ) ) );

    }

    /**
     * Tests picking up an order before it is fulfilled. (INVALID)
     *
     * @throws Exception
     */
    @Test
    @Transactional
    void updateOrderPickupWhenNotReadTestInvalid () throws Exception {

        // Create Order
        final Order order1 = new Order( "lebronjames", "lebron", "Mocha", 11, false, false );

        // Place order
        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order1 ) ) ).andExpect( status().isOk() );

        // Get unfulfilled orders; should be 1
        final MvcResult result = mvc.perform( get( "/api/v1/orders/status/0" ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$", hasSize( 1 ) ) ).andReturn();
        final String jsonResponse = result.getResponse().getContentAsString();

        final ObjectMapper objectMapper = new ObjectMapper();
        final List<Order> orders = Arrays.asList( objectMapper.readValue( jsonResponse, Order[].class ) );
        assertEquals( (Integer) 1, orders.size() );

        // Send pickup request without fulfilling
        orders.get( 0 ).setReceived( true );
        mvc.perform( put( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( orders.get( 0 ) ) ) ).andExpect( status().isConflict() );

        // Get fulfilled orders; should be 0
        mvc.perform( get( "/api/v1/orders/status/1" ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$", hasSize( 0 ) ) );
    }

    /**
     * Tests picking up a fulfilled order. (VALID)
     *
     * @throws Exception
     */
    @Test
    @Transactional
    void updateOrderPickupOrderValid () throws Exception {

        // Create Order (set fulfilled field to true)
        final Order order1 = new Order( "lebronjames", "lebron", "Mocha", 11, true, false );

        // Place order
        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order1 ) ) ).andExpect( status().isOk() );

        // Get fulfilled orders; should be 1
        final MvcResult result = mvc.perform( get( "/api/v1/orders/status/1" ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$", hasSize( 1 ) ) ).andReturn();
        final String jsonResponse = result.getResponse().getContentAsString();

        final ObjectMapper objectMapper = new ObjectMapper();
        final List<Order> orders = Arrays.asList( objectMapper.readValue( jsonResponse, Order[].class ) );
        assertEquals( (Integer) 1, orders.size() );

        // Send pickup request without fulfilling
        orders.get( 0 ).setReceived( true );
        mvc.perform( put( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( orders.get( 0 ) ) ) ).andExpect( status().isOk() );

        // Get fulfilled orders; should be still be 1
        mvc.perform( get( "/api/v1/orders/status/1" ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$", hasSize( 1 ) ) );
    }

}
