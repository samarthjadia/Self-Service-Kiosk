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
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@RunWith ( SpringRunner.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class GenerateRecipeWithIngredients {

    @Autowired
    private RecipeService     recipeService;

    @Autowired
    private IngredientService ingredientService;

    @Before
    public void setup () {
        recipeService.deleteAll();
        ingredientService.deleteAll();
    }

    private void addIngredients () {
        ingredientService.save( new Ingredient( "Coffee", 20 ) );
        ingredientService.save( new Ingredient( "Pumpkin Spice", 20 ) );
        ingredientService.save( new Ingredient( "Milk", 20 ) );
    }

    @Test
    @Transactional
    public void createRecipe () {
        addIngredients();

        final Recipe r1 = new Recipe();
        r1.setName( "Delicious Coffee" );
        r1.setPrice( 50 );
        r1.addIngredient( ingredientService.findByName( "Coffee" ), 10 );
        r1.addIngredient( ingredientService.findByName( "Pumpkin Spice" ), 3 );
        r1.addIngredient( ingredientService.findByName( "Milk" ), 2 );
        recipeService.save( r1 );

        final Recipe r2 = new Recipe();
        r2.setName( "Black Coffee" );
        r2.setPrice( 50 );
        r2.addIngredient( ingredientService.findByName( "Coffee" ), 10 );
        recipeService.save( r2 );

        assertEquals( r1, recipeService.findByName( "Delicious Coffee" ) );
        assertEquals( r2, recipeService.findByName( "Black Coffee" ) );
    }
}
