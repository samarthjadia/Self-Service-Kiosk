package edu.ncsu.csc.CoffeeMaker.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.ncsu.csc.CoffeeMaker.models.APIRecipe;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith ( SpringExtension.class )
public class APIRecipeTest extends APITest {
    @Autowired
    private RecipeService     recipeService;

    @Autowired
    private IngredientService ingredientService;

    @Override
    protected String getEndpoint () {
        return "/api/v1/recipes";
    }

    /**
     * Sets up the tests.
     */
    @Override
    @BeforeEach
    public void setup () {
        super.setup();

        // Empty DB of recipes.
        recipeService.deleteAll();
    }

    private void addIngredients () {
        // Instantiate DB with ingredients.
        ingredientService.deleteAll();
        ingredientService.save( new Ingredient( "Coffee", 20 ) );
        ingredientService.save( new Ingredient( "Milk", 20 ) );
        ingredientService.save( new Ingredient( "Sugar", 20 ) );
        ingredientService.save( new Ingredient( "Chocolate", 20 ) );
    }

    private APIRecipe createMocha () {
        final Recipe r = new Recipe();
        r.setName( "Mocha" );
        r.setPrice( 10 );
        r.addIngredient( ingredientService.findByName( "Coffee" ), 3 );
        r.addIngredient( ingredientService.findByName( "Milk" ), 4 );
        r.addIngredient( ingredientService.findByName( "Sugar" ), 8 );
        r.addIngredient( ingredientService.findByName( "Chocolate" ), 5 );
        return new APIRecipe( r );
    }

    private APIRecipe createCoffee () {
        final Recipe r = new Recipe();
        r.setName( "Coffee" );
        r.setPrice( 5 );
        r.addIngredient( ingredientService.findByName( "Coffee" ), 4 );
        r.addIngredient( ingredientService.findByName( "Milk" ), 1 );
        r.addIngredient( ingredientService.findByName( "Sugar" ), 2 );
        return new APIRecipe( r );
    }

    private APIRecipe createBlackCoffee () {
        final Recipe r = new Recipe();
        r.setName( "Black Coffee" );
        r.setPrice( 5 );
        r.addIngredient( ingredientService.findByName( "Coffee" ), 4 );
        return new APIRecipe( r );
    }

    private APIRecipe createChocolateMilk () {
        final Recipe r = new Recipe();
        r.setName( "Chocolate Milk" );
        r.setPrice( 3 );
        r.addIngredient( ingredientService.findByName( "Milk" ), 3 );
        r.addIngredient( ingredientService.findByName( "Chocolate" ), 1 );
        return new APIRecipe( r );
    }

    private APIRecipe createInvalidIngredientQuantity () {
        final Recipe r = new Recipe();
        r.setName( "Antimatter Black Coffee" );
        r.setPrice( 180000 );
        r.addIngredient( ingredientService.findByName( "Coffee" ), -4 );
        return new APIRecipe( r );
    }

    private APIRecipe createSalt () {
        final Recipe r = new Recipe();
        r.setName( "salt." );
        r.setPrice( 0 );
        r.addIngredient( new Ingredient( "Salt", 500000 ), 100 );
        return new APIRecipe( r );
    }

    private APIRecipe createMoneyLeakage () {
        final Recipe r = new Recipe();
        r.setName( "Free Money!" );
        r.setPrice( -1000 );
        r.addIngredient( ingredientService.findByName( "Sugar" ), 10000 );
        return new APIRecipe( r );
    }

    @Test
    @Transactional
    public void testGetRecipes () throws Exception {
        addIngredients();
        assertEquals( 0, requestGetAllList( APIRecipe.class ).size() );

        requestGet( "invalid" ).andExpect( status().isNotFound() );

        final APIRecipe mocha = createMocha();
        requestPost( mocha ).andExpect( status().isOk() );
        assertEquals( mocha, requestGetSuccess( mocha.getName(), APIRecipe.class ) );

        final APIRecipe coffee = createCoffee();
        requestPost( coffee ).andExpect( status().isOk() );
        assertEquals( coffee, requestGetSuccess( coffee.getName(), APIRecipe.class ) );

        final APIRecipe chocolateMilk = createChocolateMilk();
        requestPost( chocolateMilk ).andExpect( status().isOk() );
        assertEquals( chocolateMilk, requestGetSuccess( chocolateMilk.getName(), APIRecipe.class ) );

        final List<APIRecipe> recipes = requestGetAllList( APIRecipe.class );
        assertEquals( 3, recipes.size() );
        assertTrue( recipes.contains( mocha ) );
        assertTrue( recipes.contains( coffee ) );
        assertTrue( recipes.contains( chocolateMilk ) );
    }

    @Test
    @Transactional
    public void testCreateRecipe () throws Exception {
        addIngredients();
        final List<APIRecipe> expectedStored = new ArrayList<APIRecipe>();
        expectedStored.add( createMocha() );
        expectedStored.add( createCoffee() );
        expectedStored.add( createChocolateMilk() );

        // Invalid.
        requestPost( createInvalidIngredientQuantity() ).andExpect( status().isBadRequest() );
        requestPost( createSalt() ).andExpect( status().isBadRequest() );
        requestPost( createMoneyLeakage() ).andExpect( status().isBadRequest() );

        // Valid and successful.
        for ( final APIRecipe r : expectedStored ) {
            requestPost( r ).andExpect( status().isOk() );
        }

        // Edge cases.
        requestPost( expectedStored.get( 0 ) ).andExpect( status().isConflict() );
        requestPost( createBlackCoffee() ).andExpect( status().isInsufficientStorage() );

        final List<APIRecipe> recipes = requestGetAllList( APIRecipe.class );
        assertEquals( expectedStored.size(), recipes.size() );
        assertTrue( recipes.containsAll( expectedStored ) );
    }

    @Test
    @Transactional
    public void testDeleteRecipe () throws Exception {
        addIngredients();
        requestDelete( "invalid" ).andExpect( status().isNotFound() );

        requestPost( createCoffee() );
        requestDelete( "Coffee" ).andExpect( status().isOk() );
        assertEquals( 0, requestGetAllList( APIRecipe.class ).size() );

        requestDelete( "Coffee" ).andExpect( status().isNotFound() );
        assertEquals( 0, requestGetAllList( APIRecipe.class ).size() );
    }

    @Test
    @Transactional
    public void testEditRecipe () throws Exception {
        addIngredients();
        final List<APIRecipe> expectedStored = new ArrayList<APIRecipe>();
        expectedStored.add( createMocha() );
        expectedStored.add( createBlackCoffee() );
        expectedStored.add( createChocolateMilk() );
        for ( final APIRecipe r : expectedStored ) {
            requestPost( r ).andExpect( status().isOk() );
        }

        // Invalid.
        requestPut( "", createCoffee() ).andExpect( status().isNotFound() );

        final APIRecipe badIngredients = createMocha();
        badIngredients.getIngredients().put( "Coffee", -1 );
        requestPut( "", badIngredients ).andExpect( status().isBadRequest() );

        final Recipe badPrice = new Recipe();
        badPrice.setName( "Black Coffee" );
        badPrice.setPrice( -1 );
        badPrice.addIngredient( ingredientService.findByName( "Coffee" ), 4 );
        requestPut( "", new APIRecipe( badPrice ) ).andExpect( status().isBadRequest() );

        // Valid and successful.
        final Recipe chocolateMilk = new Recipe();
        chocolateMilk.setName( "Chocolate Milk" );
        chocolateMilk.setPrice( 3 );
        chocolateMilk.addIngredient( ingredientService.findByName( "Milk" ), 3 );
        chocolateMilk.addIngredient( ingredientService.findByName( "Chocolate" ), 2 );
        expectedStored.remove( expectedStored.get( 2 ) );
        expectedStored.add( new APIRecipe( chocolateMilk ) );
        requestPut( "", expectedStored.get( 2 ) ).andExpect( status().isOk() );

        final List<APIRecipe> recipes = requestGetAllList( APIRecipe.class );
        assertEquals( expectedStored.size(), recipes.size() );
        assertTrue( recipes.containsAll( expectedStored ) );
    }
}
