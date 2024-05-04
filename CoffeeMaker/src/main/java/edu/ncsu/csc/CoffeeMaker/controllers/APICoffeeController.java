package edu.ncsu.csc.CoffeeMaker.controllers;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

/**
 *
 * The APICoffeeController is responsible for making coffee when a user submits
 * a request to do so.
 *
 * Spring will automatically convert all of the ResponseEntity and List results
 * to JSON
 *
 * @author Samarth Jadia (ssjadia), Kai Presler-Marshall
 *
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APICoffeeController extends APIController {
    /**
     * IngredientService object, to be autowired in by Spring to allow for
     * manipulating the Ingredient model.
     */
    @Autowired
    private IngredientService ingredientService;

    /**
     * RecipeService object, to be autowired in by Spring to allow for
     * manipulating the Recipe model.
     */
    @Autowired
    private RecipeService     recipeService;

    /**
     * REST API method to make a beverage by completing a POST request with the
     * ID of the recipe as the path variable and the amount that has been paid
     * as the body of the response.
     *
     * @param name
     *            recipe name
     * @param amtPaid
     *            amount paid
     * @return The change the customer is due if successful
     */
    @PostMapping ( BASE_PATH + "/makecoffee/{name}" )
    public ResponseEntity makeCoffee ( @PathVariable ( "name" ) final String name, @RequestBody final int amtPaid ) {
        final Recipe recipe = recipeService.findByName( name );
        if ( recipe == null ) {
            return new ResponseEntity( errorResponse( "No recipe selected" ), HttpStatus.NOT_FOUND );
        }

        final int change = make( recipe, amtPaid );
        return change == amtPaid
                ? new ResponseEntity(
                        errorResponse( amtPaid < recipe.getPrice() ? "Not enough money paid" : "Not enough inventory" ),
                        HttpStatus.CONFLICT )
                : new ResponseEntity<String>( successResponse( String.valueOf( change ) ), HttpStatus.OK );
    }

    /**
     * Helper method to make a beverage. Assumes the recipe is not null.
     *
     * @param toPurchase
     *            recipe that we want to make
     * @param amtPaid
     *            money that the user has given the machine
     * @return change if there was enough money to make the beverage, throws
     *         exceptions if not
     */
    private int make ( final Recipe toPurchase, final int amtPaid ) {
        // Not enough money paid.
        if ( amtPaid < toPurchase.getPrice() ) {
            return amtPaid;
        }

        // Not enough inventory.
        for ( final Ingredient i : toPurchase.getIngredients() ) {
            if ( !i.enoughIngredients( toPurchase ) ) {
                return amtPaid;
            }
        }

        // Make beverage.
        toPurchase.getIngredients().forEach( i -> i.useIngredients( toPurchase ) );
        ingredientService.saveAll( toPurchase.getIngredients().stream().collect( Collectors.toList() ) );
        return amtPaid - toPurchase.getPrice();
    }
}
