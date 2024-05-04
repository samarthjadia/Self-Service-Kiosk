package edu.ncsu.csc.CoffeeMaker.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderTest {

    private Recipe recipe1;
    private Order  order1;

    @BeforeEach
    void setUp () throws Exception {
        recipe1 = new Recipe();
        recipe1.setName( "Mocha" );
        recipe1.setPrice( 8 );
        recipe1.addIngredient( new Ingredient( "Milk", 20 ), 10 );
        recipe1.addIngredient( new Ingredient( "Sugar", 30 ), 5 );
        order1 = new Order( "john_doe", "john", "Mocha", 10, false, false );
    }

    /**
     * Tests the initial set up for the order object
     */
    @Test
    void testConstructor () {
        assertEquals( "john", order1.getName() );
        assertEquals( 10, order1.getPayment() );
        assertEquals( recipe1.getName(), order1.getRecipe() );
        assertEquals( "john_doe", order1.getUsername() );
    }

    /**
     * Tests to make sure the order status can be changed to fulfilled
     */
    @Test
    void testSetFulfilled () {
        assertEquals( "john", order1.getName() );
        assertEquals( 10, order1.getPayment() );
        assertEquals( recipe1.getName(), order1.getRecipe() );
        assertEquals( "john_doe", order1.getUsername() );

        order1.setFulfilled( true );
        assertTrue( order1.isFulfilled() );
    }

    /**
     * Tests setId() method
     */
    @Test
    void testSetId () {
        order1.setId( (long) 13 );
        assertEquals( "john", order1.getName() );
        assertEquals( 10, order1.getPayment() );
        assertEquals( recipe1.getName(), order1.getRecipe() );
        assertEquals( "john_doe", order1.getUsername() );
        assertEquals( 13, order1.getId() );

    }

    /**
     * Tests setUsername() method
     */
    @Test
    void testSetUsername () {
        order1.setUsername( "Efhaske" );
        assertEquals( "john", order1.getName() );
        assertEquals( 10, order1.getPayment() );
        assertEquals( recipe1.getName(), order1.getRecipe() );
        assertEquals( "Efhaske", order1.getUsername() );

    }

    /**
     * Tests setPayment() method
     */
    @Test
    void testSetPayment () {
        order1.setPayment( 5 );
        assertEquals( "john", order1.getName() );
        assertEquals( 5, order1.getPayment() );
        assertEquals( recipe1.getName(), order1.getRecipe() );
        assertEquals( "john_doe", order1.getUsername() );

    }

    /**
     * Tests setRecipe() method
     */
    @Test
    void testSetRecipe () {
        order1.setRecipe( "Latte" );
        assertEquals( "john", order1.getName() );
        assertEquals( 10, order1.getPayment() );
        assertEquals( "Latte", order1.getRecipe() );
        assertEquals( "john_doe", order1.getUsername() );

    }

    /**
     * Tests setReceived() method
     */
    @Test
    void testSetReceived () {
        order1.setReceived( true );
        assertEquals( "john", order1.getName() );
        assertEquals( 10, order1.getPayment() );
        assertEquals( recipe1.getName(), order1.getRecipe() );
        assertEquals( "john_doe", order1.getUsername() );
        assertTrue( order1.isReceived() );

    }

    /**
     * Tests setName() method
     */
    @Test
    void testSetName () {
        order1.setName( "Ethan" );
        assertEquals( "Ethan", order1.getName() );
        assertEquals( 10, order1.getPayment() );
        assertEquals( recipe1.getName(), order1.getRecipe() );
        assertEquals( "john_doe", order1.getUsername() );

    }

}
