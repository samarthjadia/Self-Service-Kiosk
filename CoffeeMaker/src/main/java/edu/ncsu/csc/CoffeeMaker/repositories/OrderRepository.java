/**
 *
 */
package edu.ncsu.csc.CoffeeMaker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.CoffeeMaker.models.Order;

/**
 * Order repository
 *
 * @author Samarth Jadia (ssjadia), efhaske, sturner4
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
    /**
     * Finds an Ingredient object with the provided name. Spring will generate
     * code to make this happen.
     *
     * @param name
     *            Name of the ingredient
     * @return Found ingredient, null if none.
     */
    Order findByName ( String name );
}
