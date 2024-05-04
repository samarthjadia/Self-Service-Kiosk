package edu.ncsu.csc.CoffeeMaker.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class IngredientTest {

    @Test
    public void testGetters () {
        // Test new ingredient
        final Ingredient ing = new Ingredient( "Coffee", 5 );
        assertNull( ing.getId() );
        assertEquals( "Coffee", ing.getName() );
        assertEquals( 5, ing.getAmount() );

        // Test empty ingredient
        final Ingredient emptyIng = new Ingredient();
        assertNull( emptyIng.getId() );
        assertNull( emptyIng.getName() );
        assertNull( emptyIng.getAmount() );
    }

    @Test
    public void testUseIngredients () {
        final Ingredient ing = new Ingredient( "Coffee", 2 );

        // Create recipe.
        final Recipe r = new Recipe();
        r.setName( "Black Coffee" );
        r.setPrice( 4 );
        r.addIngredient( ing, 3 );

        // Test not enough ingredients.
        assertFalse( ing.useIngredients( r ) );
        assertEquals( 2, ing.getAmount() );

        // Test unreferenced ingredient.
        assertTrue( new Ingredient( "Sugar", 1 ).enoughIngredients( r ) );

        // Add invalid ingredient stock.
        assertFalse( ing.add( -1 ) );

        // Add ingredient stock.
        assertTrue( ing.add( 8 ) );
        assertEquals( 10, ing.getAmount() );

        // Test enough ingredients.
        assertTrue( ing.useIngredients( r ) );
        assertEquals( 7, ing.getAmount() );
    }

    @Test
    public void testToString () {
        assertEquals( "Coffee", new Ingredient( "Coffee", 6 ).toString() );
    }
}
