package edu.ncsu.csc.CoffeeMaker.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
public class APICoffeeTest extends APITest {
    @Autowired
    private RecipeService     recipeService;

    @Autowired
    private IngredientService ingredientService;

    private static String     recipeName;

    @Override
    protected String getEndpoint () {
        return new StringBuilder( "/api/v1/makecoffee/" ).append( recipeName ).toString();
    }

    /**
     * Sets up the tests.
     */
    @Override
    @BeforeEach
    public void setup () {
        super.setup();

        ingredientService.deleteAll();
        ingredientService.save( new Ingredient( "Coffee", 5 ) );
        ingredientService.save( new Ingredient( "Milk", 5 ) );
        ingredientService.save( new Ingredient( "Sugar", 5 ) );

        recipeService.deleteAll();
        final Recipe r = new Recipe();
        r.setName( "Coffee" );
        r.setPrice( 5 );
        r.addIngredient( ingredientService.findByName( "Coffee" ), 4 );
        r.addIngredient( ingredientService.findByName( "Milk" ), 1 );
        r.addIngredient( ingredientService.findByName( "Sugar" ), 2 );
        recipeService.save( r );

        final Recipe r2 = new Recipe();
        r2.setName( "Trenta Coffee" );
        r2.setPrice( 8 );
        for ( final Ingredient i : r.getIngredients() ) {
            r2.addIngredient( ingredientService.findByName( i.getName() ), r.getIngredient( i ) * 2 );
        }
        recipeService.save( r2 );
    }

    @Test
    @Transactional
    public void testInvalidRecipe () throws Exception {
        recipeName = "invalid";
        requestPost( 800 ).andExpect( status().isNotFound() );
    }

    @Test
    @Transactional
    public void testTooPoor () throws Exception {
        recipeName = ( "Coffee" );
        requestPost( 3 ).andExpect( status().isConflict() ).andReturn().getResponse().getContentAsString()
                .contains( "\"message\":\"Not enough money paid\"" );
    }

    @Test
    @Transactional
    public void testNoInventory () throws Exception {
        recipeName = "Trenta Coffee";
        requestPost( 8 ).andExpect( status().isConflict() ).andReturn().getResponse().getContentAsString()
                .contains( "\"message\":\"Not enough inventory\"" );
    }

    @Test
    @Transactional
    public void testSuccessfulBeverageMade () throws Exception {
        recipeName = "Coffee";
        requestPost( 5 ).andExpect( status().isOk() );
    }
}
