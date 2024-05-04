package edu.ncsu.csc.CoffeeMaker.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Min;

/**
 * Recipe for the coffee maker. Recipe is tied to the database using Hibernate
 * libraries. See RecipeRepository and RecipeService for the other two pieces
 * used for database support.
 *
 * @author Kai Presler-Marshall
 */
@Entity
public class Recipe extends DomainObject {

    /** Recipe id */
    @Id
    @GeneratedValue
    private Long                   id;

    /** Recipe name */
    private String                 name;

    /** Recipe price */
    @Min ( 0 )
    private Integer                price;
    /** List of ingredients in Recipe */
    @OneToMany ( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    private final List<Ingredient> ingredients;

    /**
     * Creates a default recipe for the coffee maker.
     */
    public Recipe () {
        this.name = "";
        this.ingredients = new ArrayList<Ingredient>();
    }

    /**
     * This method adds an ingredient object to the ingredients list.
     *
     * @param ingredient
     *            the ingredient being added
     * @throws IllegalArgumentException
     *             if ingredient is NULL
     */
    public void addIngredient ( final Ingredient ingredient ) {
        if ( ingredient != null ) {
            this.ingredients.add( ingredient );
        }
        else {
            throw new IllegalArgumentException( "Invalid Ingredient Object." );
        }
    }

    /**
     * This method edits the units of an ingredient object in the ingredients
     * list.
     *
     * @param ingredient
     *            the ingredient being added
     * @param units
     *            the units of the ingredient
     * @throws IllegalArgumentException
     *             if ingredient is not in the list of ingredients
     */
    public void editUnits ( final Ingredient ingredient, final int units ) {
        if ( ingredient == null || !this.getIngredient( ingredient.getName() ).equals( ingredient ) ) {
            throw new IllegalArgumentException( "Ingredient does not exist for recipe." );
        }
        else {
            this.getIngredient( ingredient.getName() ).setAmount( units );
        }
    }

    /**
     * This method deletes an ingredient object from the ingredients list.
     *
     * @param ingredient
     *            the ingredient being deleted
     * @throws IllegalArgumentException
     *             if there is no ingredient to delete.
     */
    public void deleteIngredient ( final Ingredient ingredient ) {
        if ( ingredients.size() < 0 ) {
            throw new IllegalArgumentException( "No ingredients to delete." );
        }
        else {
            // Look for the ingredient in the list
            for ( int i = 0; i < ingredients.size(); i++ ) {
                // If the ingredient is found remove it from the list
                if ( ingredients.get( i ).equals( ingredient ) ) {
                    ingredients.remove( i );
                }
            }
        }
    }

    /**
     * This method returns the list of ingredients for the recipe.
     *
     * @return list of ingredients in the recipe
     */
    public List<Ingredient> getIngredients () {
        return this.ingredients;
    }

    /**
     * This method returns one ingredient from the recipe based on its type
     *
     * @param typeOfIng
     *            the type of ingredient
     *
     * @return the ingredient specified from the recipe, null if the ingredient
     *         does not exist.
     */
    public Ingredient getIngredient ( final String typeOfIng ) {
        final Iterator<Ingredient> itr = ingredients.iterator();

        // Search for the ingredient in the ingredients list
        while ( itr.hasNext() ) {
            // Store the current ingredient
            final Ingredient curr = itr.next();
            if ( curr.getName().equals( typeOfIng ) ) {
                return curr;
            }
        }

        return null;
    }

    /**
     * Get the ID of the Recipe
     *
     * @return the ID
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Set the ID of the Recipe (Used by Hibernate)
     *
     * @param id
     *            the ID
     */
    @SuppressWarnings ( "unused" )
    private void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Returns name of the recipe.
     *
     * @return Returns the name.
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the recipe name.
     *
     * @param name
     *            The name to set.
     */
    public void setName ( final String name ) {
        this.name = name;
    }

    /**
     * Returns the price of the recipe.
     *
     * @return Returns the price.
     */
    public Integer getPrice () {
        return price;
    }

    /**
     * Sets the recipe price.
     *
     * @param price
     *            The price to set.
     */
    public void setPrice ( final Integer price ) {
        this.price = price;
    }

    /**
     * Returns the name of the recipe along with the ingredients list.
     *
     * @return String representation of a recipe object.
     */
    @Override
    public String toString () {
        final StringBuilder ingredients1 = new StringBuilder();
        ingredients1.append( this.name + " with ingredients " + this.ingredients.toString() );
        return ingredients1.toString();
    }

    /**
     * Produces the hashcode for a recipe object
     *
     * @return int value that represents hashcode
     */
    @Override
    public int hashCode () {
        final int prime = 31;
        Integer result = 1;
        result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
        return result;
    }

    /**
     * Checks if to recipe objects are equal
     *
     * @param obj
     *            Object to compare to
     *
     * @return True if the objects are equal, else false
     */
    @Override
    public boolean equals ( final Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final Recipe other = (Recipe) obj;
        if ( name == null ) {
            if ( other.name != null ) {
                return false;
            }
        }
        else if ( !name.equals( other.name ) ) {
            return false;
        }
        return true;
    }

}
