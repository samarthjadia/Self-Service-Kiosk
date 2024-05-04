/**
 *
 */
package edu.ncsu.csc.CoffeeMaker.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.models.Order;
import edu.ncsu.csc.CoffeeMaker.repositories.OrderRepository;

/**
 * Service class for managing Orders
 *
 * @author Samarth Jadia (ssjadia)
 */
@Component
@Transactional
public class OrderService extends Service<Order, Long> {

    @Autowired
    OrderRepository or;

    @Override
    public OrderRepository getRepository () {
        return or;
    }

    /**
     * Find an order with the provided name.
     *
     * @param name
     *            Name of the order to find
     * @return found order, null if none
     */
    public Order findByName ( final String name ) {
        return or.findByName( name );
    }

    /**
     * Places a new order by saving it in the database
     *
     * @param order
     *            the order object to be created/placed
     */
    public void placeOrder ( final Order order ) {
        or.save( order );
    }

}
