package edu.ncsu.csc.CoffeeMaker.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;

@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
public class APIIngredientTest extends APITest {
    @Autowired
    private IngredientService service;

    @Override
    protected String getEndpoint () {
        return "/api/v1/ingredients";
    }

    /**
     * Sets up the tests.
     */
    @Override
    @BeforeEach
    public void setup () {
        super.setup();
        service.deleteAll();
    }

    private void confirmGetIngredient ( final Ingredient i ) throws UnsupportedEncodingException, Exception {
        assertTrue( requestGet( i.getName() ).andExpect( status().isOk() ).andReturn().getResponse()
                .getContentAsString()
                .contains( new StringBuilder( "\"message\":\"" ).append( i.getAmount() ).append( "\"" ).toString() ) );
    }

    @Test
    @Transactional
    public void testAddValidIngredient () throws Exception {
        final Ingredient honey = new Ingredient( "Honey", 20 );
        requestPost( honey ).andExpect( status().isOk() );
        assertEquals( List.of( honey.getName() ), requestGetAllList( String.class ) );
        confirmGetIngredient( honey );
    }

    @Test
    @Transactional
    public void testAddIngredientWithInvalidUnit () throws Exception {
        requestPost( new Ingredient( "Maple Syrup", -5 ) ).andExpect( status().isBadRequest() );
        requestPost( new Ingredient( "Orange Juice", null ) ).andExpect( status().isBadRequest() );
        assertTrue( requestGetAllList( String.class ).isEmpty() );
    }

    @Test
    @Transactional
    public void testAddDuplicateIngredient () throws Exception {
        final Ingredient sugar = new Ingredient( "Sugar", 10 );
        requestPost( sugar ).andExpect( status().isOk() );

        requestPost( new Ingredient( "Sugar", 20 ) ).andExpect( status().isConflict() );
        assertEquals( List.of( sugar.getName() ), requestGetAllList( String.class ) );
        confirmGetIngredient( sugar );
    }

    @Test
    public void testGetIngredients () throws Exception {
        // Add some ingredients.
        final List<Ingredient> ingredients = List.of( new Ingredient( "Vanilla", 10 ), new Ingredient( "Caramel", 5 ) );
        service.saveAll( ingredients );

        assertEquals( ingredients.stream().map( i -> i.getName() ).collect( Collectors.toList() ),
                requestGetAllList( String.class ) );
    }

    @Test
    public void testGetIngredient () throws Exception {
        final Ingredient vanilla = new Ingredient( "Vanilla", 10 );
        service.save( vanilla );
        confirmGetIngredient( vanilla );
        requestGet( "invalid" ).andExpect( status().isNotFound() );
    }

}
