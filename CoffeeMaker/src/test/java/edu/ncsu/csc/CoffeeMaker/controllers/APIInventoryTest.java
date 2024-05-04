package edu.ncsu.csc.CoffeeMaker.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.google.gson.JsonSyntaxException;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;

@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
public class APIInventoryTest extends APITest {
    @Autowired
    private IngredientService service;

    @Override
    protected String getEndpoint () {
        return "/api/v1/inventory";
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

    /**
     * Calls the API endpoint to get inventory.
     *
     * @throws Exception
     * @throws UnsupportedEncodingException
     * @throws JsonSyntaxException
     */
    @Test
    @Transactional
    public void testGetInventory () throws UnsupportedEncodingException, Exception {
        // Check initial inventory.
        assertEquals( 0, requestGetAllMap( String.class, Integer.class ).size() );

        // Add an ingredient.
        final Ingredient coffee = new Ingredient( "Coffee", 50 );
        service.save( coffee );

        // Verify new inventory.
        final Map<String, Integer> ingredients = requestGetAllMap( String.class, Integer.class );
        assertEquals( 1, ingredients.size() );
        assertTrue( ingredients.containsKey( coffee.getName() ) );
        assertEquals( coffee.getAmount(), ingredients.get( coffee.getName() ) );
    }

    /**
     * Calls the API endpoint to add inventory and verify the changes.
     *
     * @throws Exception
     * @throws UnsupportedEncodingException
     * @throws JsonSyntaxException
     */
    @Test
    @Transactional
    public void testAddInventory () throws UnsupportedEncodingException, Exception {
        // Check initial inventory.
        assertEquals( 0, requestGetAllMap( String.class, Integer.class ).size() );

        // Add an ingredient.
        final Ingredient coffee = new Ingredient( "Coffee", 50 );
        service.save( coffee );

        // Verify initial state.
        Map<String, Integer> ingredients = requestGetAllMap( String.class, Integer.class );
        assertEquals( 1, ingredients.size() );
        assertTrue( ingredients.containsKey( coffee.getName() ) );
        assertEquals( coffee.getAmount(), ingredients.get( coffee.getName() ) );

        // Restock inventory.
        requestPut( "", Map.of( coffee.getName(), 50 ) ).andExpect( status().isOk() );

        // Verify updated state.
        ingredients = requestGetAllMap( String.class, Integer.class );
        assertEquals( 1, ingredients.size() );
        assertEquals( 100, ingredients.get( coffee.getName() ) );

        // Restock with invalid values and a nonexistent ingredient.
        requestPut( "", Map.of( coffee.getName(), -1 ) ).andExpect( status().isBadRequest() );
        requestPut( "", Map.of( "invalid", 50 ) ).andExpect( status().isOk() );

        // Verify state has not changed.
        ingredients = requestGetAllMap( String.class, Integer.class );
        assertEquals( 1, ingredients.size() );
        assertEquals( 100, ingredients.get( coffee.getName() ) );
    }
}
