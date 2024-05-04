package edu.ncsu.csc.CoffeeMaker.controllers;

import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

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

import edu.ncsu.csc.CoffeeMaker.models.APIRecipe;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

/**
 * This is the controller that holds the REST endpoints that handle CRUD
 * operations for Recipes.
 *
 * Spring will automatically convert all of the ResponseEntity and List results
 * to JSON
 *
 * @author Kai Presler-Marshall
 * @author Michelle Lemons
 *
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIRecipeController extends APIController {
    /**
     * RecipeService object, to be autowired in by Spring to allow for
     * manipulating the Recipe model.
     */
    @Autowired
    private RecipeService     recipeService;

    /**
     * IngredientService object, to be autowired in by Spring to allow for
     * verifying ingredients.
     */
    @Autowired
    private IngredientService ingredientService;

    /**
     * REST API method to provide GET access to all recipes in the system.
     *
     * @return JSON representation of all recipes
     */
    @GetMapping ( BASE_PATH + "/recipes" )
    public List<APIRecipe> getRecipes () {
        return recipeService.findAll().stream().map( r -> new APIRecipe( r ) ).collect( Collectors.toList() );
    }

    /**
     * REST API method to provide GET access to a specific recipe, as indicated
     * by the path variable provided (the name of the recipe desired)
     *
     * @param name
     *            recipe name
     * @return response to the request
     */
    @GetMapping ( BASE_PATH + "/recipes/{name}" )
    public ResponseEntity getRecipe ( @PathVariable ( "name" ) final String name ) {
        final Recipe recipe = recipeService.findByName( name );
        return null == recipe
                ? new ResponseEntity(
                        errorResponse( new StringBuilder( "No recipe found with name " ).append( name ).toString() ),
                        HttpStatus.NOT_FOUND )
                : new ResponseEntity( new APIRecipe( recipe ), HttpStatus.OK );
    }

    /**
     * REST API method to provide POST access to the Recipe model. This is used
     * to create a new Recipe by automatically converting the JSON RequestBody
     * provided to a Recipe object. Invalid JSON will fail.
     *
     * @param recipe
     *            The valid Recipe to be saved.
     * @return ResponseEntity indicating success if the Recipe could be saved to
     *         the inventory, or an error if it could not be
     */
    @PostMapping ( BASE_PATH + "/recipes" )
    public ResponseEntity createRecipe ( @RequestBody final APIRecipe apiRecipe ) {
        // Recipe already exists.
        if ( null != recipeService.findByName( apiRecipe.getName() ) ) {
            return new ResponseEntity( errorResponse( new StringBuilder( "Recipe with the name " )
                    .append( apiRecipe.getName() ).append( " already exists" ).toString() ), HttpStatus.CONFLICT );
        }

        // Insufficient storage.
        if ( recipeService.findAll().size() >= 3 ) {
            return new ResponseEntity(
                    errorResponse( new StringBuilder( "Insufficient space in recipe book for recipe " )
                            .append( apiRecipe.getName() ).toString() ),
                    HttpStatus.INSUFFICIENT_STORAGE );
        }

        final ResponseEntity err = validateRecipe( apiRecipe );
        if ( err != null ) {
            return err;
        }

        // Create Recipe object
        final Recipe recipe = new Recipe();
        recipe.setName( apiRecipe.getName() );
        recipe.setPrice( apiRecipe.getPrice() );
        apiRecipe.getIngredients().forEach( ( s, i ) -> recipe.addIngredient( ingredientService.findByName( s ), i ) );

        recipeService.save( recipe );
        return new ResponseEntity(
                successResponse( new StringBuilder( recipe.getName() ).append( " successfully created" ).toString() ),
                HttpStatus.OK );
    }

    /**
     * REST API method to allow deleting a Recipe from the CoffeeMaker's
     * storage, by making a DELETE request to the API endpoint and indicating
     * the recipe to delete (as a path variable).
     *
     * @param name
     *            The name of the Recipe to delete
     * @return Success if the recipe could be deleted; an error if the recipe
     *         does not exist
     */
    @DeleteMapping ( BASE_PATH + "/recipes/{name}" )
    public ResponseEntity deleteRecipe ( @PathVariable final String name ) {
        final Recipe recipe = recipeService.findByName( name );
        if ( null == recipe ) {
            return new ResponseEntity(
                    errorResponse( new StringBuilder( "No recipe found for name " ).append( name ).toString() ),
                    HttpStatus.NOT_FOUND );
        }
        recipeService.delete( recipe );

        return new ResponseEntity(
                successResponse( new StringBuilder( name ).append( " was deleted successfully" ).toString() ),
                HttpStatus.OK );
    }

    /**
     * REST API method to allow editing a Recipe from the CoffeeMaker's storage,
     * by making a PUT request to the API endpoint and indicating a new recipe
     * to replace the contents of the old one.
     *
     * @param apiRecipe
     *            The valid Recipe to be saved.
     * @return ResponseEntity indicating success if the Recipe could be modified
     *         and saved to the inventory, or an error if it could not be
     */
    @PutMapping ( BASE_PATH + "/recipes" )
    public ResponseEntity editRecipe ( @RequestBody final APIRecipe apiRecipe ) {
        final Recipe recipe = recipeService.findByName( apiRecipe.getName() );

        // Recipe does not exist.
        if ( null == recipe ) {
            return new ResponseEntity( errorResponse( new StringBuilder( "Recipe with the name " )
                    .append( apiRecipe.getName() ).append( " was not found" ).toString() ), HttpStatus.NOT_FOUND );
        }

        final ResponseEntity err = validateRecipe( apiRecipe );
        if ( err != null ) {
            return err;
        }

        // Create new Recipe object to save.
        recipe.setName( apiRecipe.getName() );
        recipe.setPrice( apiRecipe.getPrice() );
        recipe.setIngredients( apiRecipe.getIngredients().entrySet().stream()
                .collect( Collectors.toMap( e -> ingredientService.findByName( e.getKey() ), e -> e.getValue() ) ) );

        recipeService.save( recipe );
        return new ResponseEntity(
                successResponse( new StringBuilder( recipe.getName() ).append( " successfully modified" ).toString() ),
                HttpStatus.OK );
    }

    private ResponseEntity validateRecipe ( final APIRecipe recipe ) {
        // Invalid price.
        if ( recipe.getPrice() < 0 ) {
            return new ResponseEntity(
                    errorResponse( new StringBuilder( "Invalid price " ).append( recipe.getPrice() ).toString() ),
                    HttpStatus.BAD_REQUEST );
        }

        // Invalid ingredients.
        final String invalidIngredient = verifyIngredients( recipe );
        if ( null != invalidIngredient ) {
            return new ResponseEntity( errorResponse(
                    new StringBuilder( "Invalid ingredient in recipe: " ).append( invalidIngredient ).toString() ),
                    HttpStatus.BAD_REQUEST );
        }

        return null;
    }

    private String verifyIngredients ( final APIRecipe recipe ) {
        final List<String> ingredients = ingredientService.findAll().stream().map( Ingredient::getName )
                .collect( Collectors.toList() );
        for ( final Entry<String, Integer> e : recipe.getIngredients().entrySet() ) {
            if ( !ingredients.contains( e.getKey() ) || e.getValue() <= 0 ) {
                return e.getKey();
            }
        }
        return null;
    }
}
