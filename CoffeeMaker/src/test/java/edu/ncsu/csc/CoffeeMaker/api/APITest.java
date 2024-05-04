package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;

@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
public class APITest {
    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
    }

    @Test
    @Transactional
    public void testMakeCoffee () {
        try {
            String recipe = mvc.perform( get( "/api/v1/recipes" ) ).andDo( print() ).andExpect( status().isOk() )
                    .andReturn().getResponse().getContentAsString();

            if ( !recipe.contains( "Mocha" ) ) {
                final Recipe r = new Recipe();
                final Ingredient recipeCoffee = new Ingredient( "COFFEE", 7 );
                final Ingredient recipeSugar = new Ingredient( "SUGAR", 3 );
                final Ingredient recipeMilk = new Ingredient( "MILK", 3 );
                final Ingredient recipePumpkinSpice = new Ingredient( "PUMPKIN_SPICE", 2 );

                r.setName( "Mocha" );
                r.setPrice( 7 );
                r.addIngredient( recipeCoffee );
                r.addIngredient( recipeSugar );
                r.addIngredient( recipeMilk );
                r.addIngredient( recipePumpkinSpice );

                mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( r ) ) ).andExpect( status().isOk() );

            }

            recipe = mvc.perform( get( "/api/v1/recipes" ) ).andDo( print() ).andExpect( status().isOk() ).andReturn()
                    .getResponse().getContentAsString();

            assertTrue( recipe.contains( "Mocha" ) );
            final List<Ingredient> initialInventory = new ArrayList<Ingredient>();
            initialInventory.add( new Ingredient( "COFFEE", 50 ) );
            initialInventory.add( new Ingredient( "MILK", 50 ) );
            initialInventory.add( new Ingredient( "SUGAR", 50 ) );
            initialInventory.add( new Ingredient( "PUMPKIN_SPICE", 50 ) );
            final Inventory i = new Inventory( initialInventory );
            i.setId( (long) 69 );
            mvc.perform( put( "/api/v1/inventory" ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( i ) ) ).andExpect( status().isOk() );

            // Error is Occurring on this block of code
            mvc.perform( post( String.format( "/api/v1/makecoffee/Mocha" ) ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( 100 ) ) ).andExpect( status().isOk() )
                    .andExpect( jsonPath( "$.message" ).value( 93 ) );
            // Until here. Its trying to invoke toString on an ingredient but
            // its getting null

        }
        catch ( final Exception e ) {
            e.printStackTrace();
            fail();
        }
    }

}
