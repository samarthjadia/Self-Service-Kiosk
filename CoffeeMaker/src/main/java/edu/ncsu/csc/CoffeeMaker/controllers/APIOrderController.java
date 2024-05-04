package edu.ncsu.csc.CoffeeMaker.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.Order;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.OrderService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

/**
 * The controller class defining the end-points for handling orders
 *
 * @author Samarth Jadia (ssjadia), efhaske, sturner4
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIOrderController extends APIController {

    /**
     * OrderService object, to be autowired in by Spring to allow for
     * manipulating the Order model
     */
    @Autowired
    private OrderService  orderService;

    /**
     * RecipeService object, to be autowired in by Spring to allow for
     * manipulating the Recipe model.
     */
    @Autowired
    private RecipeService recipeService;

    /**
     * End-point for placing an order
     *
     * @param order
     *            the order being placed
     * @return ResponseEntity indicating success or failure
     */
    @PostMapping ( BASE_PATH + "/orders" )
    public ResponseEntity createOrder ( @RequestBody final Order order ) {
        final String r = order.getRecipe();
        // Check if recipe exists
        final Recipe dbr = recipeService.findByName( r );
        if ( dbr == null ) {
            return new ResponseEntity(
                    errorResponse( new StringBuilder( "No recipe found with name " ).append( r ).toString() ),
                    HttpStatus.NOT_FOUND );
        }
        // Check for funds
        if ( order.getPayment() < dbr.getPrice() ) {
            return new ResponseEntity( errorResponse(
                    new StringBuilder( "Insufficient funds. Order costs $" ).append( dbr.getPrice() ).toString() ),
                    HttpStatus.CONFLICT );
        }

        orderService.placeOrder( order );
        return new ResponseEntity( successResponse( new StringBuilder( "Order Succesfully placed! Your change is $" )
                .append( order.getPayment() - dbr.getPrice() ).toString() ), HttpStatus.OK );
    }

    /**
     * End-point for retrieving a list of all orders in the system
     *
     * @return List of all orders in the system
     */
    @GetMapping ( BASE_PATH + "/orders" )
    public List<Order> getOrders () {
        // Retrieve list of orders
        return orderService.findAll();
    }

    /**
     * End-point for retrieving a list of unfulfilled orders or fulfilled
     * orders, depending on the type the request specified in the PathVariable.
     *
     * @param fulfilled
     *            boolean indicating to retrieve fulfilled or unfulfilled orders
     *
     * @return List of unfulfilled or fulfilled orders
     */
    @GetMapping ( BASE_PATH + "/orders/status/{fulfilled}" )
    public List<Order> getFulfillingOrders ( @PathVariable ( "fulfilled" ) final boolean fulfilled ) {
        // Get all orders
        final List<Order> orders = orderService.findAll();
        // Remove all the orders that don't match the status provided (fulfilled
        // or unfulfilled)
        for ( int i = orders.size() - 1; i >= 0; i-- ) {
            if ( orders.get( i ).isFulfilled() != fulfilled ) {
                orders.remove( i );
            }
        }
        return orders;
    }

    /**
     * End-point for retrieving a list of customer orders, specified by the
     * username.
     *
     * @param username
     *            the username of the customer whose orders they're requesting
     *
     * @return List of unfulfilled/fulfilled orders
     */
    @GetMapping ( BASE_PATH + "/orders/{username}" )
    public List<Order> getCustomerOrders ( @PathVariable ( "username" ) final String username ) {
        final List<Order> orders = orderService.findAll();
        for ( int i = orders.size() - 1; i >= 0; i-- ) {
            if ( !orders.get( i ).getUsername().equals( username ) ) {
                orders.remove( i );
            }
        }
        return orders;
    }

    /**
     * End-point for marking an order as fulfilled, essentially updating the
     * order's fulfilled field to true.
     *
     * @param order
     *            the order being updated (fulfilled)
     * @return ResponseEntity indicating success or failure
     */
    @PutMapping ( BASE_PATH + "/orders" )
    public ResponseEntity updateOrder ( @RequestBody final Order order ) {
        final Order dbo = orderService.findById( order.getId() );
        // Check if order exists in the system
        if ( dbo == null ) {
            return new ResponseEntity(
                    errorResponse(
                            new StringBuilder( "No order found with name " ).append( order.getName() ).toString() ),
                    HttpStatus.NOT_FOUND );
        }
        // Make sure no order details are changed (except fulfilled or received)
        if ( !dbo.getName().equals( order.getName() ) || dbo.getPayment() != order.getPayment()
                || !dbo.getRecipe().equals( order.getRecipe() ) || !dbo.getUsername().equals( order.getUsername() ) ) {
            return new ResponseEntity( errorResponse(
                    new StringBuilder( "Error: Only allowed to change fulfilled or received field" ).toString() ),
                    HttpStatus.CONFLICT );
        }

        // Make sure that a field is changed
        if ( dbo.isFulfilled() == order.isFulfilled() && dbo.isReceived() == order.isReceived() ) {
            return new ResponseEntity(
                    errorResponse( new StringBuilder( "Nothing has been changed to the order" ).toString() ),
                    HttpStatus.CONFLICT );
        }

        // Cannot pickup an order if it is not fulfilled
        if ( order.isReceived() && !dbo.isFulfilled() ) {
            return new ResponseEntity( errorResponse( new StringBuilder( "Order is not ready for pickup" ).toString() ),
                    HttpStatus.CONFLICT );
        }

        if ( dbo.isFulfilled() != order.isFulfilled() ) {
            orderService.save( order );
            return new ResponseEntity( successResponse( "Order has been fulfilled!" ), HttpStatus.OK );
        }
        else {
            orderService.save( order );
            return new ResponseEntity( successResponse( "Order has been picked up!" ), HttpStatus.OK );
        }

    }

}
