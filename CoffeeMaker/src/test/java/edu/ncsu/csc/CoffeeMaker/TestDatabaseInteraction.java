package edu.ncsu.csc.CoffeeMaker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )

public class TestDatabaseInteraction {

    @Autowired
    private RecipeService recipeService;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        recipeService.deleteAll();
    }

    /**
     * Tests the RecipeService class
     */
    @Test
    @Transactional
    public void testRecipes () {
        final Recipe r = new Recipe();

        // Create ingredients for the recipe
        final Ingredient chocolate = new Ingredient( "CHOCOLATE", 5 );
        final Ingredient coffee = new Ingredient( "COFFEE", 5 );
        final Ingredient milk = new Ingredient( "MILK", 5 );
        final Ingredient sugar = new Ingredient( "SUGAR", 5 );
        // Add ingredients to recipe
        r.addIngredient( chocolate );
        r.addIngredient( coffee );
        r.addIngredient( milk );
        r.addIngredient( sugar );
        r.setPrice( 5 );
        r.setName( "Mocha" );

        recipeService.save( r );

        final List<Recipe> dbRecipes = recipeService.findAll();

        assertEquals( 1, dbRecipes.size() );

        final Recipe dbRecipe = dbRecipes.get( 0 );

        assertEquals( r.getName(), dbRecipe.getName() );
        // Return the list of ingredients for the recipe
        final List<Ingredient> mochaIngredients = r.getIngredients();
        // Check the size of the list
        assertEquals( 4, mochaIngredients.size() );
        assertEquals( chocolate, dbRecipe.getIngredient( "CHOCOLATE" ) );
        assertEquals( coffee, dbRecipe.getIngredient( "COFFEE" ) );
        assertEquals( milk, dbRecipe.getIngredient( "MILK" ) );
        assertEquals( sugar, dbRecipe.getIngredient( "SUGAR" ) );
        assertEquals( r.getPrice(), dbRecipe.getPrice() );
    }

    /**
     * Tests the RecipeService class
     */
    @Test
    @Transactional
    public void testRecipesFindByName () {
        final Recipe r = new Recipe();

        // Create ingredients for the recipe
        final Ingredient chocolate = new Ingredient( "CHOCOLATE", 5 );
        final Ingredient coffee = new Ingredient( "COFFEE", 5 );
        final Ingredient milk = new Ingredient( "MILK", 5 );
        final Ingredient sugar = new Ingredient( "SUGAR", 5 );
        // Add ingredients to recipe
        r.addIngredient( chocolate );
        r.addIngredient( coffee );
        r.addIngredient( milk );
        r.addIngredient( sugar );
        r.setPrice( 5 );
        r.setName( "Mocha" );

        recipeService.save( r );

        final Recipe dbRecipe = recipeService.findByName( "Mocha" );

        assertNotNull( dbRecipe );
        assertEquals( r.getName(), dbRecipe.getName() );
        assertEquals( r.getPrice(), dbRecipe.getPrice() );
        // Return the list of ingredients for the recipe
        final List<Ingredient> mochaIngredients = r.getIngredients();
        // Check the size of the list
        assertEquals( 4, mochaIngredients.size() );
        assertEquals( chocolate, dbRecipe.getIngredient( "CHOCOLATE" ) );
        assertEquals( coffee, dbRecipe.getIngredient( "COFFEE" ) );
        assertEquals( milk, dbRecipe.getIngredient( "MILK" ) );
        assertEquals( sugar, dbRecipe.getIngredient( "SUGAR" ) );

    }

    /**
     * Tests the Service class
     */
    @Test
    @Transactional
    public void testRecipesSave () {
        final Recipe r = new Recipe();

        // Create ingredients for the recipe
        final Ingredient chocolate = new Ingredient( "CHOCOLATE", 5 );
        final Ingredient coffee = new Ingredient( "COFFEE", 5 );
        final Ingredient milk = new Ingredient( "MILK", 5 );
        final Ingredient sugar = new Ingredient( "SUGAR", 5 );
        // Add ingredients to recipe
        r.addIngredient( chocolate );
        r.addIngredient( coffee );
        r.addIngredient( milk );
        r.addIngredient( sugar );
        r.setPrice( 5 );
        r.setName( "Mocha" );

        recipeService.save( r );

        final Recipe dbRecipe = recipeService.findByName( "Mocha" );

        dbRecipe.setPrice( 15 );
        dbRecipe.editUnits( sugar, 12 );
        recipeService.save( dbRecipe );

        assertEquals( (Integer) 15, dbRecipe.getPrice() );
        assertEquals( (Integer) 12, dbRecipe.getIngredient( "SUGAR" ).getAmount() );

        final List<Recipe> dbRecipes = recipeService.findAll();

        assertEquals( 1, dbRecipes.size() );
        assertEquals( "Mocha", dbRecipes.get( 0 ).getName() );
    }

    /**
     * Tests the finByID method in Service Class
     */
    @Test
    @Transactional
    public void testRecipesFindByID () {
        final Recipe r = new Recipe();

        // Create ingredients for the recipe
        final Ingredient chocolate = new Ingredient( "CHOCOLATE", 5 );
        final Ingredient coffee = new Ingredient( "COFFEE", 5 );
        final Ingredient milk = new Ingredient( "MILK", 5 );
        final Ingredient sugar = new Ingredient( "SUGAR", 5 );
        // Add ingredients to recipe
        r.addIngredient( chocolate );
        r.addIngredient( coffee );
        r.addIngredient( milk );
        r.addIngredient( sugar );
        r.setPrice( 5 );
        r.setName( "Mocha" );

        recipeService.save( r );

        final Recipe dbRecipe = recipeService.findById( r.getId() );
        assertNotNull( dbRecipe );
        assertEquals( r.getName(), dbRecipe.getName() );
        // Return the list of ingredients for the recipe
        final List<Ingredient> mochaIngredients = r.getIngredients();
        // Check the size of the list
        assertEquals( 4, mochaIngredients.size() );
        assertEquals( chocolate, dbRecipe.getIngredient( "CHOCOLATE" ) );
        assertEquals( coffee, dbRecipe.getIngredient( "COFFEE" ) );
        assertEquals( milk, dbRecipe.getIngredient( "MILK" ) );
        assertEquals( sugar, dbRecipe.getIngredient( "SUGAR" ) );
        assertEquals( r.getPrice(), dbRecipe.getPrice() );
    }

}
