package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.TestConfig;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class RecipeTest {

    @Autowired
    private RecipeService    service;

    private Ingredient       chocolate;
    private Ingredient       sugar;
    private Ingredient       pumpkin;
    private Ingredient       milk;
    // private Ingredient coffee;

    private List<Ingredient> ingredientsList;

    @BeforeEach
    public void setup () {
        service.deleteAll();
        chocolate = new Ingredient( "CHOCOLATE", 3 );
        sugar = new Ingredient( "SUGAR", 0 );
        pumpkin = new Ingredient( "PUMPKIN_SPICE", 1 );
        milk = new Ingredient( "MILK", 1 );
        // coffee = new Ingredient( IngredientType.COFFEE, 0 );
        ingredientsList = new ArrayList<Ingredient>();
        ingredientsList.add( chocolate );
        // ingredientsList.add( coffee );
        ingredientsList.add( milk );
        ingredientsList.add( pumpkin );
        ingredientsList.add( sugar );
    }

    @Test
    @Transactional
    public void testAddRecipe () {

        final Recipe r1 = new Recipe();
        r1.setName( "Black Coffee" );
        r1.setPrice( 1 );

        // Create ingredient objects to add to recipe
        final Ingredient ingredient1 = new Ingredient( "PUMPKIN_SPICE", 5 );
        final Ingredient ingredient2 = new Ingredient( "MILK", 10 );

        // Add ingredients
        r1.addIngredient( ingredient1 );
        r1.addIngredient( ingredient2 );
        // Save recipe
        service.save( r1 );

        // Create another recipe
        final Recipe r2 = new Recipe();
        r2.setName( "Mocha" );
        r2.setPrice( 1 );

        // Create ingredient objects to add to recipe
        final Ingredient ingredient3 = new Ingredient( "PUMPKIN_SPICE", 1 );
        final Ingredient ingredient4 = new Ingredient( "COFFEE", 7 );

        // Add ingredients
        r2.addIngredient( ingredient3 );
        r2.addIngredient( ingredient4 );

        // Save recipe
        service.save( r2 );

        final List<Recipe> recipes = service.findAll();
        Assertions.assertEquals( 2, recipes.size(),
                "Creating two recipes should result in two recipes in the database" );

        Assertions.assertEquals( r1, recipes.get( 0 ), "The retrieved recipe should match the created one" );
    }

    /* tests the deleteIngredient method in Recipe */
    @Test
    @Transactional
    public void testDeleteIngredientInRecipe () {

        final Recipe r1 = new Recipe();
        r1.setName( "Black Coffee" );
        r1.setPrice( 1 );

        // Create ingredient objects to add to recipe
        final Ingredient ingredient1 = new Ingredient( "PUMPKIN_SPICE", 5 );
        final Ingredient ingredient2 = new Ingredient( "MILK", 10 );

        // Add ingredients
        r1.addIngredient( ingredient1 );
        r1.addIngredient( ingredient2 );
        // Save recipe
        service.save( r1 );
        // find recipe in database
        final Recipe r = service.findByName( "Black Coffee" );
        Assertions.assertEquals( 2, r.getIngredients().size() );
        // delete pumpkin spice from the recipe
        r.deleteIngredient( ingredient1 );

        Assertions.assertEquals( 1, r.getIngredients().size() );

        Assertions.assertEquals( ingredient2, r.getIngredients().get( 0 ) );
    }

    @Test
    @Transactional
    public void testNoRecipes () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = new Recipe();
        r1.setName( "Tasty Drink" );
        r1.setPrice( 12 );

        // Create ingredient objects to add to recipe
        final Ingredient ingredient1 = new Ingredient( "PUMPKIN_SPICE", 5 );
        final Ingredient ingredient2 = new Ingredient( "MILK", 10 );

        // Add ingredients
        r1.addIngredient( ingredient1 );
        r1.addIngredient( ingredient2 );

        final Recipe r2 = new Recipe();
        r2.setName( "Mocha" );
        r2.setPrice( 1 );

        // Create ingredient objects to add to recipe
        final Ingredient ingredient3 = new Ingredient( "MILK", -3 );
        final Ingredient ingredient4 = new Ingredient( "SUGAR", 2 );

        // Add ingredients
        r2.addIngredient( ingredient3 );
        r2.addIngredient( ingredient4 );

        final List<Recipe> recipes = List.of( r1, r2 );

        try {
            service.saveAll( recipes );
            Assertions.assertEquals( 0, service.count(),
                    "Trying to save a collection of elements where one is invalid should result in neither getting saved" );
        }
        catch ( final Exception e ) {
            Assertions.assertTrue( e instanceof ConstraintViolationException );
        }

    }

    @Test
    @Transactional
    public void testAddRecipe1 () {

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";

        final Recipe r1 = createRecipe( name, 50, ingredientsList );

        service.save( r1 );

        Assertions.assertEquals( 1, service.findAll().size(), "There should only one recipe in the CoffeeMaker" );
        Assertions.assertNotNull( service.findByName( name ) );

    }

    /* Test2 is done via the API for different validation */

    @Test
    @Transactional
    public void testAddRecipe3 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Recipe r1 = createRecipe( name, -50, ingredientsList );

        try {
            service.save( r1 );

            Assertions.assertNull( service.findByName( name ),
                    "A recipe was able to be created with a negative price" );
        }
        catch ( final ConstraintViolationException cvee ) {
            // expected
        }

    }

    @Test
    @Transactional
    public void testAddRecipe4 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        chocolate.setAmount( -3 );
        sugar.setAmount( 2 );
        final Recipe r1 = createRecipe( name, 50, ingredientsList );

        try {
            service.save( r1 );

            Assertions.assertNull( service.findByName( name ),
                    "A recipe was able to be created with a negative amount of coffee" );
        }
        catch ( final ConstraintViolationException cvee ) {
            // expected
        }

    }

    @Test
    @Transactional
    public void testAddRecipe5 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        milk.setAmount( -1 );
        sugar.setAmount( 2 );
        final Recipe r1 = createRecipe( name, 50, ingredientsList );

        try {
            service.save( r1 );

            Assertions.assertNull( service.findByName( name ),
                    "A recipe was able to be created with a negative amount of milk" );
        }
        catch ( final ConstraintViolationException cvee ) {
            // expected
        }

    }

    @Test
    @Transactional
    public void testAddRecipe6 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        pumpkin.setAmount( -1 );
        sugar.setAmount( 2 );
        final Recipe r1 = createRecipe( name, 50, ingredientsList );

        try {
            service.save( r1 );

            Assertions.assertNull( service.findByName( name ),
                    "A recipe was able to be created with a negative amount of sugar" );
        }
        catch ( final ConstraintViolationException cvee ) {
            // expected
        }

    }

    @Test
    @Transactional
    public void testAddRecipe7 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        sugar.setAmount( -2 );
        final Recipe r1 = createRecipe( name, 50, ingredientsList );

        try {
            service.save( r1 );

            Assertions.assertNull( service.findByName( name ),
                    "A recipe was able to be created with a negative amount of chocolate" );
        }
        catch ( final ConstraintViolationException cvee ) {
            // expected
        }

    }

    @Test
    @Transactional
    public void testAddRecipe13 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        sugar.setAmount( 0 );
        final Recipe r1 = createRecipe( "Coffee", 50, ingredientsList );
        service.save( r1 );
        sugar.setAmount( 0 );
        final List<Ingredient> list2 = new ArrayList<Ingredient>();
        list2.add( new Ingredient( "MILK", 20 ) );
        list2.add( new Ingredient( "COFFEE", 15 ) );
        list2.add( new Ingredient( "SUGAR", 10 ) );
        final Recipe r2 = createRecipe( "Mocha", 50, list2 );
        service.save( r2 );

        Assertions.assertEquals( 2, service.count(),
                "Creating two recipes should result in two recipes in the database" );

    }

    @Test
    @Transactional
    public void testAddRecipe14 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        sugar.setAmount( 0 );
        final Recipe r1 = createRecipe( "Coffee", 50, ingredientsList );
        service.save( r1 );
        final List<Ingredient> list2 = new ArrayList<Ingredient>();
        list2.add( new Ingredient( "MILK", 20 ) );
        list2.add( new Ingredient( "COFFEE", 15 ) );
        list2.add( new Ingredient( "SUGAR", 10 ) );
        final Recipe r2 = createRecipe( "Mocha", 50, list2 );
        service.save( r2 );

        final List<Ingredient> list3 = new ArrayList<Ingredient>();
        list2.add( new Ingredient( "CHOCOLATE", 20 ) );
        list2.add( new Ingredient( "COFFEE", 15 ) );
        list2.add( new Ingredient( "SUGAR", 10 ) );
        final Recipe r3 = createRecipe( "Latte", 60, list3 );
        service.save( r3 );

        Assertions.assertEquals( 3, service.count(),
                "Creating three recipes should result in three recipes in the database" );

    }

    @Test
    @Transactional
    public void testDeleteRecipe1 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, ingredientsList );
        service.save( r1 );

        Assertions.assertEquals( 1, service.count(), "There should be one recipe in the database" );

        service.delete( r1 );
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
    }

    @Test
    @Transactional
    public void testDeleteRecipe2 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, ingredientsList );
        service.save( r1 );
        final List<Ingredient> list2 = new ArrayList<Ingredient>();
        list2.add( new Ingredient( "MILK", 20 ) );
        list2.add( new Ingredient( "COFFEE", 15 ) );
        list2.add( new Ingredient( "SUGAR", 10 ) );
        final Recipe r2 = createRecipe( "Mocha", 50, list2 );
        service.save( r2 );
        final List<Ingredient> list3 = new ArrayList<Ingredient>();
        list2.add( new Ingredient( "COFFEE", 5 ) );
        list2.add( new Ingredient( "PUMPKIN_SPICE", 15 ) );
        list2.add( new Ingredient( "SUGAR", 10 ) );
        final Recipe r3 = createRecipe( "Latte", 60, list3 );
        service.save( r3 );
        Assertions.assertEquals( 3, service.count(), "There should be three recipes in the database" );

        service.deleteAll();

        Assertions.assertEquals( 0, service.count(), "`service.deleteAll()` should remove everything" );

    }

    /*
     * Test deleting recipe that is not in the database; Database should just
     * remain the same. Also tests the case that where it was deleted by a
     * concurrent user because if it was, then it would attempt to attempt to
     * delete a non-existent recipe; which is exactly what this test method is
     * doing.
     */
    @Test
    @Transactional
    public void testDeleteRecipe3 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, ingredientsList );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Hot Chocolate", 50, ingredientsList );

        Assertions.assertEquals( 1, service.count(), "There should be one recipe in the database" );

        service.delete( r2 );

        Assertions.assertEquals( 1, service.count(),
                "The recipe being deleted does not exist in the database, so nothing should be deleted" );
    }

    /* Tests the use case where user deletes 2 out of 3 recipes */
    @Test
    @Transactional
    public void testDeleteRecipe4 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, ingredientsList );
        service.save( r1 );
        final List<Ingredient> list2 = new ArrayList<Ingredient>();
        list2.add( new Ingredient( "MILK", 20 ) );
        list2.add( new Ingredient( "COFFEE", 15 ) );
        list2.add( new Ingredient( "SUGAR", 10 ) );
        final Recipe r2 = createRecipe( "Mocha", 50, list2 );
        service.save( r2 );
        final List<Ingredient> list3 = new ArrayList<Ingredient>();
        list2.add( new Ingredient( "MILK", 20 ) );
        list2.add( new Ingredient( "PUMPKIN_SPICE", 5 ) );
        list2.add( new Ingredient( "SUGAR", 10 ) );

        final Recipe r3 = createRecipe( "Latte", 60, list3 );
        service.save( r3 );

        Assertions.assertEquals( 3, service.count(), "There should be three recipes in the database" );
        service.delete( r1 );
        Assertions.assertEquals( 2, service.count(), "There should be two recipes in the database" );
        service.delete( r3 );
        Assertions.assertEquals( 1, service.count(), "There should be one recipe in the database" );
        Assertions.assertTrue( service.findAll().get( 0 ).equals( r2 ),
                "There only recipe in the databse should equal recipe 2 because it was theonly recipe not deleted" );
    }
    //
    // /* This tests the functionality of updating a recipe's fields. */
    // @Test
    // @Transactional
    // public void testUpdateRecipe1 () {
    // Assertions.assertEquals( 0, service.findAll().size(), "There should be no
    // Recipes in the CoffeeMaker" );
    //
    // final Recipe r1 = createRecipe( "Coffee", 50, ingredientsList );
    // service.save( r1 );
    // Assertions.assertEquals( 1, service.count(), "There should be one recipe
    // in the database" );
    //
    // final Recipe updatedRecipe = createRecipe( "Coffee 2.0", 1,
    // ingredientsList );
    // // Call updateRecipe() to change fields
    // r1.updateRecipe( updatedRecipe );
    // // Save in database with mutated fields
    // Assertions.assertEquals( 1, r1.getPrice() );
    // Assertions.assertEquals( 2, r1.getCoffee() );
    // Assertions.assertEquals( 3, r1.getMilk() );
    // Assertions.assertEquals( 4, r1.getSugar() );
    // Assertions.assertEquals( 5, r1.getChocolate() );
    // }

    // @Test
    // @Transactional
    // public void testEditRecipe1 () {
    // Assertions.assertEquals( 0, service.findAll().size(), "There should be no
    // Recipes in the CoffeeMaker" );
    //
    // final Recipe r1 = createRecipe( "Coffee", 50, ingredientsList );
    // service.save( r1 );
    //
    // r1.setPrice( 70 );
    //
    // service.save( r1 );
    //
    // final Recipe retrieved = service.findByName( "Coffee" );
    //
    // Assertions.assertEquals( 70, (int) retrieved.getPrice() );
    // Assertions.assertEquals( 3, (int) retrieved.getCoffee() );
    // Assertions.assertEquals( 1, (int) retrieved.getMilk() );
    // Assertions.assertEquals( 1, (int) retrieved.getSugar() );
    // Assertions.assertEquals( 0, (int) retrieved.getChocolate() );
    //
    // Assertions.assertEquals( 1, service.count(), "Editing a recipe shouldn't
    // duplicate it" );
    //
    // }

    // /**
    // * This tests the checkRecipe method
    // */
    // @Test
    // @Transactional
    // public void testCheckRecipe () {
    // final Recipe r1 = createRecipe( "Coffee", 50, 0, 0, 0, 0 );
    // service.save( r1 );
    //
    // final Recipe retrieved = service.findByName( "Coffee" );
    //
    // Assertions.assertTrue( retrieved.checkRecipe() );
    //
    // final Recipe r2 = createRecipe( "Coffee 2", 50, 0, 0, 0, 1 );
    //
    // service.save( r2 );
    //
    // final Recipe retrieved2 = service.findByName( "Coffee 2" );
    //
    // Assertions.assertFalse( retrieved2.checkRecipe() );
    //
    // final Recipe r3 = createRecipe( "Coffee 3", 50, 1, 0, 0, 0 );
    //
    // Assertions.assertFalse( r3.checkRecipe() );
    //
    // final Recipe r4 = createRecipe( "Coffee", 50, 0, 1, 0, 0 );
    //
    // Assertions.assertFalse( r4.checkRecipe() );
    //
    // final Recipe r5 = createRecipe( "Coffee", 50, 0, 0, 1, 0 );
    //
    // Assertions.assertFalse( r5.checkRecipe() );
    //
    // }

    /**
     * Tests toString method
     */
    @Test
    @Transactional
    public void testToString () {
        final Recipe r1 = createRecipe( "Coffee", 50, ingredientsList );
        service.save( r1 );
        final Recipe retrieved = service.findByName( "Coffee" );
        Assertions.assertEquals(
                "Coffee with ingredients [Ingredient [ingredient=CHOCOLATE, amount=3], Ingredient [ingredient=MILK, amount=1], Ingredient [ingredient=PUMPKIN_SPICE, amount=1], Ingredient [ingredient=SUGAR, amount=0]]",
                retrieved.toString() );
    }

    /**
     * method to create a recipe
     *
     * @param name
     *            name of recipe
     * @param price
     *            price of recipe
     * @param listOfIngredients
     *            list of ingredients
     * @return recipe object
     */
    private Recipe createRecipe ( final String name, final Integer price, final List<Ingredient> listOfIngredients ) {
        final Recipe recipe = new Recipe();
        recipe.setName( name );
        recipe.setPrice( price );

        final Iterator<Ingredient> itr = listOfIngredients.iterator();

        while ( itr.hasNext() ) {

            recipe.addIngredient( itr.next() );
        }

        return recipe;
    }

    /* This tests the equals method in Recipe */
    @Test
    @Transactional
    public void testEquals () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final Recipe r1 = createRecipe( "Coffee", 10, ingredientsList );
        final Recipe r2 = createRecipe( "Coffee", 10, ingredientsList );
        final Recipe r3 = createRecipe( "different", 10, ingredientsList );
        final Recipe r4 = createRecipe( null, 10, ingredientsList );
        chocolate.setAmount( 4 );
        milk.setAmount( 1 );
        pumpkin.setAmount( 1 );
        sugar.setAmount( 2 );
        final Recipe r5 = createRecipe( "Coffee", 7, ingredientsList );

        assertTrue( r1.equals( r2 ) );
        assertTrue( r2.equals( r1 ) );
        assertFalse( r1.equals( r3 ) );
        assertFalse( r3.equals( r1 ) );
        assertFalse( r1.equals( r4 ) );
        assertFalse( r4.equals( r1 ) );
        assertTrue( r5.equals( r1 ) );
        assertTrue( r1.equals( r5 ) );
    }

}
