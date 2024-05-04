package edu.ncsu.csc.CoffeeMaker.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;

/**
 * This is the controller that holds the REST endpoints that handle GET and ADD
 * operations for ingredients.
 *
 * Spring will automatically convert all of the ResponseEntity and List results
 * to JSON
 *
 * @author V Mehra (ymehra)
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIIngredientController extends APIController {
    /**
     * IngredientService object, to be autowired in by Spring to allow for
     * manipulating the Ingredient model.
     */
    @Autowired
    private IngredientService service;

    /**
     * REST API endpoint to provide GET access to the CoffeeMaker's current list
     * of registered ingredient names in JSON format.
     *
     * @return JSON list of all ingredient names
     */
    @GetMapping ( BASE_PATH + "/ingredients" )
    public List<String> getIngredients () {
        return service.findAll().stream().map( Ingredient::getName ).collect( Collectors.toList() );
    }

    /**
     * REST API method to GET the amount of an ingredient stored in the
     * inventory.
     *
     * @param name
     *            ingredient name
     * @return response to the request
     */
    @GetMapping ( BASE_PATH + "/ingredients/{name}" )
    public ResponseEntity getIngredient ( @PathVariable ( "name" ) final String name ) {
        final Ingredient i = service.findByName( name );
        return null == i
                ? new ResponseEntity(
                        errorResponse(
                                new StringBuilder( "No ingredient found with name " ).append( name ).toString() ),
                        HttpStatus.NOT_FOUND )
                : new ResponseEntity( successResponse( String.valueOf( i.getAmount() ) ), HttpStatus.OK );
    }

    /**
     * REST API method to provide POST access to the Ingredient model. This is
     * used to create a new Ingredient by automatically converting the JSON
     * RequestBody provided to an Ingredient object. Invalid JSON will fail.
     *
     * @param ingredient
     *            The valid Ingredient to be saved.
     * @return ResponseEntity indicating success if the Ingredient could be
     *         saved to storage, or an error if it could not be
     */
    @PostMapping ( BASE_PATH + "/ingredients" )
    public ResponseEntity addIngredient ( @RequestBody final Ingredient ingredient ) {
        // Ingredient already exists.
        if ( null != service.findByName( ingredient.getName() ) ) {
            return new ResponseEntity( errorResponse( new StringBuilder( "Ingredient with the name " )
                    .append( ingredient.getName() ).append( " already exists" ).toString() ), HttpStatus.CONFLICT );
        }

        // Invalid quantity.
        if ( ingredient.getAmount() == null || ingredient.getAmount() <= 0 ) {
            return new ResponseEntity( errorResponse( new StringBuilder( "Invalid quantity in ingredient: " )
                    .append( ingredient.getAmount() ).toString() ), HttpStatus.BAD_REQUEST );
        }

        service.save( new Ingredient( ingredient.getName(), ingredient.getAmount() ) );
        return new ResponseEntity(
                successResponse(
                        new StringBuilder( ingredient.getName() ).append( " successfully created" ).toString() ),
                HttpStatus.OK );
    }
}
