package edu.ncsu.csc.CoffeeMaker.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;

/**
 * This class is the controller for Ingredient Functionality
 *
 * @author ssjadia, efhaske, sturner4
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIIngredientController extends APIController {

    /**
     * IngredientService object, to be autowired in by Spring to allow for
     * manipulating the Ingredient model
     */
    @Autowired
    private IngredientService ingredientService;
    /** Inventory Service class to access the inventory */
    @Autowired
    private InventoryService  inventoryService;

    /**
     * REST API method to provide GET access to all ingredients in the system
     *
     * @return JSON representation of all ingredients
     */
    @GetMapping ( BASE_PATH + "/ingredients" )
    public List<Ingredient> getIngredients () {
        final Inventory inventory = inventoryService.getInventory();
        return inventory.getInventoryList();
    }

    /**
     * REST API method to provide GET access to a specific ingredient, as
     * indicated by the path variable provided (the name of the ingredient
     * desired)
     *
     * @param name
     *            ingredient name
     * @return response to the request
     */
    @GetMapping ( BASE_PATH + "/ingredients/{name}" )
    public ResponseEntity getIngredient ( @PathVariable final String name ) {
        final Ingredient ingr = ingredientService.findByName( name );
        if ( null == ingr ) {
            return new ResponseEntity( errorResponse( "No ingredient found with name " + name ), HttpStatus.NOT_FOUND );
        }
        return new ResponseEntity( ingr, HttpStatus.OK );
    }

    /**
     * REST API method to provide POST access to the Ingredient model. This is
     * used to create a new Ingredient by automatically converting the JSON
     * RequestBody provided to a Ingredient object. Invalid JSON will fail.
     *
     * @param ingredient
     *            The valid ingredient to be saved.
     * @return ResponseEntity indicating success if the Recipe could be saved to
     *         the inventory, or an error if it could not be
     */
    @PostMapping ( BASE_PATH + "/ingredients" )
    public ResponseEntity createIngredient ( @RequestBody final Ingredient ingredient ) {
        if ( null != ingredientService.findByName( ingredient.getName().toString() ) ) {
            return new ResponseEntity(
                    errorResponse( "Ingredient with the name " + ingredient.getName().toString() + " already exists" ),
                    HttpStatus.CONFLICT );
        }
        try {
            final Inventory inventory = inventoryService.getInventory();
            inventory.addIngredient( ingredient );
            inventoryService.save( inventory );

            // // add ingredient
            // ingredientService.save( ingredient );
            return new ResponseEntity( successResponse( ingredient.getName().toString() + " successfully created" ),
                    HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity( errorResponse(
                    "Error occurred while saving your Ingredient in Inventory" + ingredient.getName().toString() ),
                    HttpStatus.INSUFFICIENT_STORAGE );
        }
    }

    /**
     * REST API method to allow deleting an ingredient from the CoffeeMaker's
     * Inventory, by making a DELETE request to the API endpoint and indicating
     * the ingredient to delete (as a path variable)
     *
     * @param name
     *            The name of the ingredient to delete
     * @return Success if the ingredient could be deleted; an error if the
     *         ingredient does not exist
     */
    @DeleteMapping ( BASE_PATH + "/ingredients/{name}" )
    public ResponseEntity deleteIngredient ( @PathVariable final String name ) {

        final Ingredient ingr = ingredientService.findByName( name );

        if ( null == ingr ) {
            return new ResponseEntity( errorResponse( "No ingredient found for name: " + name ), HttpStatus.NOT_FOUND );
        }
        // Delete Ingredient
        ingredientService.delete( ingr );
        return new ResponseEntity( successResponse( name + " was deleted successfully" ), HttpStatus.OK );
    }

    /**
     * REST API method to allow updating an ingredient's units via a PUT request
     * to the API endpoint.
     *
     * @param ingredient
     *            the ingredient object that will be updated
     * @return Success if the ingredient units could be updated; an error if the
     *         ingredient units cannot be updated
     */
    @PutMapping ( BASE_PATH + "/ingredients" )
    public ResponseEntity updateIngredientUnits ( @RequestBody final Ingredient ingredient ) {
        if ( null == ingredientService.findByName( ingredient.getName().toString() ) ) {
            return new ResponseEntity(
                    errorResponse( "Ingredient with the name " + ingredient.getName().toString() + " does not exist." ),
                    HttpStatus.CONFLICT );
        }
        // update ingredient
        final Ingredient oldIngr = ingredientService.findByName( ingredient.getName().toString() );
        oldIngr.setAmount( ingredient.getAmount() );
        ingredientService.save( oldIngr );
        return new ResponseEntity( successResponse( "Ingredient's units were updated to " + ingredient.getAmount() ),
                HttpStatus.OK );
    }

}
