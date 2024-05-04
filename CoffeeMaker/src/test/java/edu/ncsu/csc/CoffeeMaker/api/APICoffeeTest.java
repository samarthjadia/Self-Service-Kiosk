package edu.ncsu.csc.CoffeeMaker.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
public class APICoffeeTest {

    @Autowired
    private MockMvc          mvc;

    @Autowired
    private RecipeService    service;

    @Autowired
    private InventoryService iService;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        service.deleteAll();
        iService.deleteAll();

        final Inventory ivt = iService.getInventory();

        final Ingredient chocolate = new Ingredient( "CHOCOLATE", 50 );
        final Ingredient coffee = new Ingredient( "COFFEE", 50 );
        final Ingredient milk = new Ingredient( "MILK", 50 );
        final Ingredient sugar = new Ingredient( "SUGAR", 50 );
        final Ingredient pumpkinSpice = new Ingredient( "PUMPKIN_SPICE", 50 );

        ivt.addIngredient( chocolate );
        ivt.addIngredient( coffee );
        ivt.addIngredient( milk );
        ivt.addIngredient( sugar );
        ivt.addIngredient( pumpkinSpice );

        iService.save( ivt );

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
        service.save( recipe );

        final Recipe recipeTwo = new Recipe();
        final Ingredient recipeCoffee2 = new Ingredient( "COFFEE", 51 );
        recipeTwo.setName( "CoffeeDeluxe" );
        recipeTwo.setPrice( 50 );
        recipeTwo.addIngredient( recipeCoffee2 );
        service.save( recipeTwo );

    }

    @Test
    @Transactional
    public void testPurchaseBeverageSuccess () throws Exception {

        final String name = "Coffee";

        mvc.perform( post( String.format( "/api/v1/makecoffee/%s", name ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( 60 ) ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.message" ).value( 10 ) );

        mvc.perform( post( String.format( "/api/v1/makecoffee/%s", name ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( 50 ) ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.message" ).value( 0 ) );

    }

    @Test
    @Transactional
    public void testPurchaseBeverageInsufficientFunds () throws Exception {
        /* Insufficient amount paid */

        final String name = "Coffee";

        mvc.perform( post( String.format( "/api/v1/makecoffee/%s", name ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( 40 ) ) ).andExpect( status().is4xxClientError() )
                .andExpect( jsonPath( "$.message" ).value( "Not enough money paid" ) );

    }

    @Test
    @Transactional
    public void testPurchaseBeverage3 () throws Exception {
        /* Insufficient inventory */

        final String name = "CoffeeDeluxe";

        mvc.perform( post( String.format( "/api/v1/makecoffee/%s", name ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( 50 ) ) ).andExpect( status().is4xxClientError() )
                .andExpect( jsonPath( "$.message" ).value( "Not enough inventory" ) );

    }

}
