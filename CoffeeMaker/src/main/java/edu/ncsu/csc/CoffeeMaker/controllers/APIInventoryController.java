package edu.ncsu.csc.CoffeeMaker.controllers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;

/**
 * This is the controller that holds the REST endpoints that handle add and
 * update operations for the Inventory.
 *
 * Spring will automatically convert all of the ResponseEntity and List results
 * to JSON
 *
 * @author V Mehra (ymehra)
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIInventoryController extends APIController {

    /**
     * IngredientService object, to be autowired in by Spring to allow for
     * manipulating the Ingredient model
     */
    @Autowired
    private IngredientService service;

    /**
     * REST API endpoint to provide GET access to the CoffeeMaker's model of
     * ingredients as a Map. This will convert the ingredient inventory to JSON.
     *
     * @return response to the request
     */
    @GetMapping ( BASE_PATH + "/inventory" )
    public Map<String, Integer> getInventory () {
        return service.findAll().stream().collect( Collectors.toMap( Ingredient::getName, Ingredient::getAmount ) );
    }

    /**
     * REST API endpoint to provide update access to CoffeeMaker's list of
     * ingredients. This will update the ingredient inventory of the CoffeeMaker
     * by adding amounts from the list of Ingredients provided to the
     * CoffeeMaker's stored inventory.
     *
     * @param inventory
     *            map of ingredient names and amounts to add
     * @return response to the request
     */
    @PutMapping ( BASE_PATH + "/inventory" )
    public ResponseEntity updateInventory ( @RequestBody final Map<String, Integer> inventory ) {
        final List<Ingredient> current = service.findAll();
        for ( final Ingredient i : current ) {
            if ( inventory.containsKey( i.getName() ) && !i.add( inventory.get( i.getName() ) ) ) {
                return new ResponseEntity(
                        errorResponse( new StringBuilder( "Invalid ingredient quantity for ingredient " )
                                .append( i.getName() ).toString() ),
                        HttpStatus.BAD_REQUEST );
            }
        }

        service.saveAll( current );
        return new ResponseEntity( getInventory(), HttpStatus.OK );
    }
}
