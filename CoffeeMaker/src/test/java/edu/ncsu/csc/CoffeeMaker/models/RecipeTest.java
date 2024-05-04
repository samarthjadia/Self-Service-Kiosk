package edu.ncsu.csc.CoffeeMaker.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

public class RecipeTest {

    private static Ingredient Coffee;
    private static Ingredient Milk;
    private static Ingredient Sugar;
    private static Ingredient Chocolate;

    private static void createIngredients () {
        Coffee = new Ingredient( "Coffee", 50 );
        Milk = new Ingredient( "Milk", 50 );
        Sugar = new Ingredient( "Sugar", 50 );
        Chocolate = new Ingredient( "Chocolate", 50 );
    }

    private static Recipe createRecipe () {
        final Recipe r = new Recipe();
        createIngredients();
        r.setName( "Mocha" );
        r.setPrice( 3 );
        r.addIngredient( Coffee, 2 );
        r.addIngredient( Milk, 1 );
        r.addIngredient( Sugar, 1 );
        r.addIngredient( Chocolate, 1 );
        return r;
    }

    private static Recipe createEmptyRecipe () {
        final Recipe r = new Recipe();
        r.setName( "Empty" );
        r.setPrice( 1 );
        return r;
    }

    @Test
    public void testCheckRecipe () {
        // Test a normal recipe.
        assertEquals( 4, createRecipe().getIngredients().size() );

        // Test an empty recipe.
        assertEquals( 0, createEmptyRecipe().getIngredients().size() );
    }

    @Test
    public void testGetIngredients () {
        // Test getIngredient with a parameter
        final Recipe r = createRecipe();
        assertEquals( "Mocha", r.getName() );
        assertEquals( 3, r.getPrice() );
        assertEquals( 2, r.getIngredient( Coffee ) );
        assertEquals( 1, r.getIngredient( Milk ) );
        assertEquals( 1, r.getIngredient( Sugar ) );
        assertEquals( 1, r.getIngredient( Chocolate ) );

        // Test getIngredients which returns a set on ingredients
        final Set<Ingredient> ingredients = r.getIngredients();
        assertEquals( 4, ingredients.size() );
        assertTrue( ingredients.contains( Coffee ) );
        assertTrue( ingredients.contains( Milk ) );
        assertTrue( ingredients.contains( Sugar ) );
        assertTrue( ingredients.contains( Chocolate ) );
    }

    @Test
    public void testSetIngredients () {
        final Recipe r = createRecipe();
        final Map<Ingredient, Integer> ingredients = getIngredientMap( r );
        ingredients.put( Chocolate, 16 );
        r.setIngredients( ingredients );
        assertEquals( ingredients, getIngredientMap( r ) );
    }

    @Test
    public void testToString () {
        final Recipe r = createRecipe();
        assertEquals( "Mocha: {Chocolate=1, Coffee=2, Sugar=1, Milk=1}", r.toString() );

        assertEquals( "Empty recipe", new Recipe().toString() );
    }

    @SuppressWarnings ( "unlikely-arg-type" )
    @Test
    public void testEquals () {
        final Recipe r = createRecipe();
        assertTrue( r.equals( r ) );
        assertFalse( r.equals( null ) );
        assertFalse( r.equals( "some non-recipe" ) );

        final Recipe otherRecipe = createRecipe();
        otherRecipe.addIngredient( Chocolate, 2 );
        otherRecipe.addIngredient( Milk, 2 );
        assertTrue( r.equals( otherRecipe ) );

        final Recipe nullRecipe = createEmptyRecipe();
        nullRecipe.setName( null );
        assertFalse( nullRecipe.equals( r ) );
        assertFalse( r.equals( nullRecipe ) );

        r.setName( null );
        assertTrue( r.equals( nullRecipe ) );
    }

    private Map<Ingredient, Integer> getIngredientMap ( final Recipe r ) {
        return r.getIngredients().stream().collect( Collectors.toMap( n -> n, n -> r.getIngredient( n ) ) );
    }
}
