package edu.ncsu.csc.CoffeeMaker.datagen;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import edu.ncsu.csc.CoffeeMaker.TestConfig;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;

@RunWith ( SpringRunner.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class GenerateIngredients {
    @Autowired
    private IngredientService ingredientService;

    @Before
    public void setup () {
        ingredientService.deleteAll();
    }

    @Test
    @Transactional
    public void testCreateIngredients () {
        ingredientService.save( new Ingredient( "Coffee", 10 ) );
        ingredientService.save( new Ingredient( "Milk", 15 ) );

        assertEquals( 2, ingredientService.count() );
        assertEquals( 10, ingredientService.findByName( "Coffee" ).getAmount() );
        assertEquals( 15, ingredientService.findByName( "Milk" ).getAmount() );
    }
}
