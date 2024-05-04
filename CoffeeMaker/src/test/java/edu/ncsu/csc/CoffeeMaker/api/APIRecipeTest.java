package edu.ncsu.csc.CoffeeMaker.api;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith ( SpringExtension.class )
public class APIRecipeTest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private RecipeService         service;

    @Autowired
    private IngredientService     ingredientService;

    @Autowired
    private InventoryService      inventoryService;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        service.deleteAll();
        ingredientService.deleteAll();
        inventoryService.deleteAll();
    }

    // Private method to create a recipe for testing
    private Recipe createRecipe ( final String name, final Integer price, final List<Ingredient> ingredients ) {
        final Recipe recipe = new Recipe();
        recipe.setName( name );
        recipe.setPrice( price );
        for ( final Ingredient i : ingredients ) {
            recipe.addIngredient( i );
        }
        return recipe;
    }

    @Test
    @Transactional
    public void ensureRecipe () throws Exception {
        service.deleteAll();

        final Recipe recipe = new Recipe();
        final Ingredient recipeCoffee = new Ingredient( "COFFEE", 7 );
        final Ingredient recipeSugar = new Ingredient( "SUGAR", 3 );
        final Ingredient recipeMilk = new Ingredient( "MILK", 3 );
        final Ingredient recipePumpkinSpice = new Ingredient( "PUMPKIN_SPICE", 2 );

        recipe.setName( "Coffee" );
        recipe.setPrice( 50 );
        recipe.addIngredient( recipeCoffee );
        recipe.addIngredient( recipeSugar );
        recipe.addIngredient( recipeMilk );
        recipe.addIngredient( recipePumpkinSpice );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipe ) ) ).andExpect( status().isOk() );

    }

    @Test
    @Transactional
    public void testCreateRecipeSavesToDatabase () throws Exception {

        service.deleteAll();

        final Recipe recipe = new Recipe();
        final Ingredient recipeCoffee = new Ingredient( "COFFEE", 7 );
        final Ingredient recipeSugar = new Ingredient( "SUGAR", 3 );
        final Ingredient recipeMilk = new Ingredient( "MILK", 3 );
        final Ingredient recipePumpkinSpice = new Ingredient( "PUMPKIN_SPICE", 2 );

        recipe.setName( "Coffee" );
        recipe.setPrice( 50 );
        recipe.addIngredient( recipeCoffee );
        recipe.addIngredient( recipeSugar );
        recipe.addIngredient( recipeMilk );
        recipe.addIngredient( recipePumpkinSpice );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipe ) ) );

        Assertions.assertEquals( 1, (int) service.count() );

    }

    @Test
    @Transactional
    public void testAddDuplicateRecipe () throws Exception {

        /* Tests a recipe with a duplicate name to make sure it's rejected */

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final List<Ingredient> ingredients = new ArrayList<Ingredient>();
        ingredients.add( new Ingredient( "COFFEE", 7 ) );
        ingredients.add( new Ingredient( "MILK", 4 ) );
        ingredients.add( new Ingredient( "SUGAR", 2 ) );
        ingredients.add( new Ingredient( "PUMPKIN_SPICE", 1 ) );

        final String name = "Coffee";
        final Recipe r1 = createRecipe( name, 50, ingredients );

        service.save( r1 );

        final Recipe r2 = createRecipe( name, 50, ingredients );
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r2 ) ) ).andExpect( status().is4xxClientError() );

        Assertions.assertEquals( 1, service.findAll().size(), "There should only one recipe in the CoffeeMaker" );
    }

    @Test
    @Transactional
    public void testAddRecipeAtCapacity () throws Exception {

        /* Tests to make sure that our cap of 3 recipes is enforced */

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final List<Ingredient> ingredients = new ArrayList<Ingredient>();
        ingredients.add( new Ingredient( "COFFEE", 7 ) );
        ingredients.add( new Ingredient( "MILK", 4 ) );
        ingredients.add( new Ingredient( "SUGAR", 2 ) );
        ingredients.add( new Ingredient( "PUMPKIN_SPICE", 1 ) );
        final List<Ingredient> ingredients2 = new ArrayList<Ingredient>();
        ingredients2.add( new Ingredient( "COFFEE", 8 ) );
        ingredients2.add( new Ingredient( "MILK", 5 ) );
        ingredients2.add( new Ingredient( "SUGAR", 3 ) );
        ingredients2.add( new Ingredient( "PUMPKIN_SPICE", 2 ) );
        final List<Ingredient> ingredients3 = new ArrayList<Ingredient>();
        ingredients3.add( new Ingredient( "COFFEE", 9 ) );
        ingredients3.add( new Ingredient( "MILK", 6 ) );
        ingredients3.add( new Ingredient( "SUGAR", 4 ) );
        ingredients3.add( new Ingredient( "PUMPKIN_SPICE", 3 ) );

        final Recipe r1 = createRecipe( "Coffee", 50, ingredients );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Milkshake", 51, ingredients2 );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Tea", 52, ingredients3 );
        service.save( r3 );

        Assertions.assertEquals( 3, service.count(),
                "Creating three recipes should result in three recipes in the database" );

        final List<Ingredient> ingredients4 = new ArrayList<Ingredient>();
        ingredients4.add( new Ingredient( "COFFEE", 9 ) );
        ingredients4.add( new Ingredient( "MILK", 6 ) );
        ingredients4.add( new Ingredient( "SUGAR", 4 ) );
        ingredients4.add( new Ingredient( "PUMPKIN_SPICE", 3 ) );
        final Recipe r4 = createRecipe( "Smoothie", 53, ingredients4 );
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r4 ) ) ).andExpect( status().isInsufficientStorage() );

        Assertions.assertEquals( 3, service.count(), "Creating a fourth recipe should not get saved" );
    }

    /* This tests the VALID case of deleting a recipe using the API */
    @Test
    @Transactional
    public void testDeleteRecipeValid () throws Exception {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        // Create Recipe to be added to the database
        final List<Ingredient> ingredients = new ArrayList<Ingredient>();
        ingredients.add( new Ingredient( "COFFEE", 7 ) );
        ingredients.add( new Ingredient( "MILK", 4 ) );
        ingredients.add( new Ingredient( "SUGAR", 2 ) );
        ingredients.add( new Ingredient( "PUMPKIN_SPICE", 1 ) );
        final Recipe r1 = createRecipe( "Coffee", 50, ingredients );
        // Add Recipe to Database
        service.save( r1 );
        Assertions.assertEquals( 1, service.count(),
                "Creating one recipe should result in one recipe in the database" );

        mvc.perform( delete( "/api/v1/recipes/Coffee" ) ).andExpect( status().isOk() );
        Assertions.assertEquals( 0, service.count(),
                "Deleting the only recipe in the DB should result in 0 recipes in the database" );

    }

    /* This tests the INVALID case of deleting a recipe using the API */
    @Test
    @Transactional
    public void testDeleteRecipeInvalid () throws Exception {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        // Create Recipe to be added to the database
        final List<Ingredient> ingredients = new ArrayList<Ingredient>();
        ingredients.add( new Ingredient( "COFFEE", 7 ) );
        ingredients.add( new Ingredient( "MILK", 4 ) );
        ingredients.add( new Ingredient( "SUGAR", 2 ) );
        ingredients.add( new Ingredient( "PUMPKIN_SPICE", 1 ) );
        final Recipe r1 = createRecipe( "Coffee", 50, ingredients );
        // Add Recipe to Database
        service.save( r1 );
        Assertions.assertEquals( 1, service.count(),
                "Creating one recipe should result in one recipe in the database" );

        // Try to delete a recipe in the database that does not exist should
        // return a status of Not Found
        mvc.perform( delete( "/api/v1/recipes/Coffee1" ) ).andExpect( status().isNotFound() );
        Assertions.assertEquals( 1, service.count(),
                "An unsuccesful deletion should result in the database being unchanged" );

    }

    /*
     * This method tests the VALID case for getRecipe() method in the
     * APIRecipeController
     */
    @Test
    @Transactional
    public void testGetRecipeValid () throws Exception {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        // Create Recipe to be added to the database
        final List<Ingredient> ingredients = new ArrayList<Ingredient>();
        ingredients.add( new Ingredient( "COFFEE", 7 ) );
        ingredients.add( new Ingredient( "MILK", 4 ) );
        ingredients.add( new Ingredient( "SUGAR", 2 ) );
        ingredients.add( new Ingredient( "PUMPKIN_SPICE", 1 ) );
        final Recipe r1 = createRecipe( "Coffee", 50, ingredients );
        // Add Recipe to Database
        service.save( r1 );
        Assertions.assertEquals( 1, service.count(),
                "Creating one recipe should result in one recipe in the database" );

        mvc.perform( get( "/api/v1/recipes/Coffee" ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.name" ).value( "Coffee" ) );

    }

    /*
     * This method tests the INVALID case for getRecipe() method in the
     * APIRecipeController
     */
    @Test
    @Transactional
    public void testGetRecipeInvalid () throws Exception {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        // Create Recipe to be added to the database
        final List<Ingredient> ingredients = new ArrayList<Ingredient>();
        ingredients.add( new Ingredient( "COFFEE", 7 ) );
        ingredients.add( new Ingredient( "MILK", 4 ) );
        ingredients.add( new Ingredient( "SUGAR", 2 ) );
        ingredients.add( new Ingredient( "PUMPKIN_SPICE", 1 ) );
        final Recipe r1 = createRecipe( "Coffee", 50, ingredients );
        // Add Recipe to Database
        service.save( r1 );
        Assertions.assertEquals( 1, service.count(),
                "Creating one recipe should result in one recipe in the database" );

        mvc.perform( get( "/api/v1/recipes/FutureCoffee" ) ).andExpect( status().isNotFound() );
    }

    /*
     * This method tests getRecipes() method in the APIRecipeController
     */
    @Test
    @Transactional
    public void testGetRecipes () throws Exception {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        // Create Recipe to be added to the database
        final List<Ingredient> ingredients = new ArrayList<Ingredient>();
        ingredients.add( new Ingredient( "COFFEE", 7 ) );
        ingredients.add( new Ingredient( "MILK", 4 ) );
        ingredients.add( new Ingredient( "SUGAR", 2 ) );
        ingredients.add( new Ingredient( "PUMPKIN_SPICE", 1 ) );
        final Recipe r1 = createRecipe( "Coffee", 50, ingredients );
        // Add Recipe to Database
        service.save( r1 );
        Assertions.assertEquals( 1, service.count(),
                "Creating one recipe should result in one recipe in the database" );

        mvc.perform( get( "/api/v1/recipes" ) ).andExpect( status().isOk() ).andExpect( jsonPath( "$", hasSize( 1 ) ) );
    }

    /*
     * This method tests the VALID case for getIngredient() method in the
     * APIIngredientController
     */
    @Test
    @Transactional
    public void testGetIngredientValid () throws Exception {
        Assertions.assertEquals( 0, ingredientService.findAll().size(),
                "There should be no Recipes in the CoffeeMaker" );

        final Ingredient ingredient1 = new Ingredient( "COFFEE", 10 );

        ingredientService.save( ingredient1 );
        Assertions.assertEquals( 1, ingredientService.count(),
                "Creating one recipe should result in one recipe in the database" );

        mvc.perform( get( "/api/v1/ingredients/COFFEE" ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.name" ).value( "COFFEE" ) );

    }

    /*
     * This method tests the INVALID case for getIngredient() method in the
     * APIIngredientController
     */
    @Test
    @Transactional
    public void testGetIngredientInvalid () throws Exception {
        Assertions.assertEquals( 0, ingredientService.findAll().size(),
                "There should be no Recipes in the CoffeeMaker" );

        Assertions.assertEquals( 0, ingredientService.count(),
                "Creating one recipe should result in one recipe in the database" );

        mvc.perform( get( "/api/v1/ingredients/COFFEE" ) ).andExpect( status().isNotFound() );
    }

    /*
     * This method tests the VALID case for getIngredients() method in the
     * APIIngredientController
     */
    @Test
    @Transactional
    public void testGetIngredientsValid () throws Exception {
        Assertions.assertEquals( 0, ingredientService.findAll().size(),
                "There should be no Recipes in the CoffeeMaker" );

        final Ingredient ingredient1 = new Ingredient( "COFFEE", 10 );
        final Ingredient ingredient2 = new Ingredient( "MILK", 10 );
        final Inventory i = inventoryService.getInventory();
        i.addIngredient( ingredient1 );
        i.addIngredient( ingredient2 );
        inventoryService.save( i );
        Assertions.assertEquals( 2, ingredientService.count(),
                "Adding 2 ingredients should result in 2 ingredients in the inventory" );

        mvc.perform( get( "/api/v1/ingredients" ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$", hasSize( 2 ) ) );
    }

    /**
     * Checks the VALID case of creating an ingredient and saving it to the
     * database.
     */
    @Test
    @Transactional
    public void testCreateIngredientSavesToDatabaseValid () throws Exception {

        ingredientService.deleteAll();

        final Ingredient ingredient1 = new Ingredient( "COFFEE", 10 );

        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( ingredient1 ) ) );

        Assertions.assertEquals( 1, (int) ingredientService.count() );

    }

    /**
     * Checks the INVALID case of adding a duplicate ingredient
     */
    @Test
    @Transactional
    public void testCreateIngredientSavesToDatabaseInvalid () throws Exception {

        ingredientService.deleteAll();

        final Ingredient ingredient1 = new Ingredient( "COFFEE", 10 );
        final Ingredient ingredient2 = new Ingredient( "COFFEE", 20 );

        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( ingredient1 ) ) );

        Assertions.assertEquals( 1, (int) ingredientService.count() );

        // Adding duplicate ingredient
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( ingredient2 ) ) ).andExpect( status().isConflict() );
        // Check to see that there is still only one
        Assertions.assertEquals( 1, (int) ingredientService.count() );

    }

    /**
     * Checks the VALID case of deleting an existing ingredient in the database.
     */
    @Test
    @Transactional
    public void testDeleteIngredientValid () throws Exception {

        ingredientService.deleteAll();

        final Ingredient ingredient1 = new Ingredient( "COFFEE", 10 );
        ingredientService.save( ingredient1 );

        Assertions.assertEquals( 1, (int) ingredientService.count() );

        mvc.perform( delete( "/api/v1/ingredients/COFFEE" ) ).andExpect( status().isOk() );

        // Check to see that there is zero ingredients after deletion
        Assertions.assertEquals( 0, (int) ingredientService.count() );
    }

    /**
     * Checks the INVALID case of deleting a non existent ingredient.
     */
    @Test
    @Transactional
    public void testDeleteIngredientInvalid () throws Exception {

        ingredientService.deleteAll();

        mvc.perform( delete( "/api/v1/ingredients/COFFEE" ) ).andExpect( status().isNotFound() );

        Assertions.assertEquals( 0, (int) ingredientService.count() );
    }

    /**
     * Checks the VALID case of updating an ingredient's units.
     */
    @Test
    @Transactional
    public void testUpdateIngredientsVALID () throws Exception {

        ingredientService.deleteAll();

        final Ingredient ingredient1 = new Ingredient( "COFFEE", 10 );

        ingredientService.save( ingredient1 );
        Assertions.assertEquals( 1, (int) ingredientService.count() );
        final Ingredient ingredientUpdate = new Ingredient( "COFFEE", 20 );

        // Adding duplicate ingredient
        mvc.perform( put( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( ingredientUpdate ) ) ).andExpect( status().isOk() );
        // Check to see that there is still only one
        Assertions.assertEquals( 1, (int) ingredientService.count() );
    }

    /*
     * This method tests the VALID case for getInventory() method in the
     * APIInventoryController
     */
    @Test
    @Transactional
    public void testGetInventory () throws Exception {
        Assertions.assertEquals( 0, inventoryService.findAll().size(), "no inventory should be there" );

        final Inventory i = new Inventory();
        final List<Ingredient> ingredients1 = new ArrayList<Ingredient>();
        final Ingredient ingredient1 = new Ingredient( "COFFEE", 10 );
        ingredients1.add( ingredient1 );
        inventoryService.save( i );

        mvc.perform( get( "/api/v1/inventory" ) ).andExpect( status().isOk() );

    }

}
