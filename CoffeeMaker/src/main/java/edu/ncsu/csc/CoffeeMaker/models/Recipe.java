package edu.ncsu.csc.CoffeeMaker.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyClass;
import javax.persistence.MapKeyJoinColumn;
import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Recipe for the coffee maker. Recipe is tied to the database using Hibernate
 * libraries. See RecipeRepository and RecipeService for the other two pieces
 * used for database support.
 *
 * @author Samarth Jadia (ssjadia), Kai Presler-Marshall
 */
@Entity
public class Recipe extends DomainObject {
    /** Recipe id */
    @Id
    @GeneratedValue
    private Long                           id;

    /** Recipe name */
    private String                         name;

    /** Recipe price */
    @Min ( 0 )
    private int                            price;

    /** Map of ingredients */
    @ElementCollection ( fetch = FetchType.EAGER )
    @CollectionTable ( name = "recipe_ingredients",
            joinColumns = @JoinColumn ( name = "recipe_id", referencedColumnName = "id" ) )
    @Column ( name = "amount" )
    @MapKeyClass ( Ingredient.class )
    @MapKeyJoinColumn ( name = "ingredient_id", referencedColumnName = "id" )
    private final Map<Ingredient, Integer> ingredients;

    /**
     * Creates a default recipe for the coffee maker.
     */
    public Recipe () {
        this.name = "";
        this.ingredients = new HashMap<Ingredient, Integer>();
    }

    /**
     * Get the ID of the Recipe.
     *
     * @return the ID
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Set the ID of the Recipe (Used by Hibernate).
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
     * @return Recipe name
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
     * @return Recipe price
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
     * Returns the name of the recipe.
     *
     * @return String
     */
    @Override
    public String toString () {
        return name.length() == 0 || ingredients.size() == 0 ? "Empty recipe"
                : new StringBuilder( name ).append( ": " ).append( ingredients.toString() ).toString();
    }

    /**
     * Adds an ingredient.
     */
    public void addIngredient ( final Ingredient ingredient, final Integer amount ) {
        ingredients.put( ingredient, amount );
    }

    /**
     * Gets all ingredients.
     */
    @JsonIgnore
    public Set<Ingredient> getIngredients () {
        return ingredients.keySet();
    }

    /**
     * Sets internal Ingredient map to the given map.
     *
     * @param ingredients
     *            ingredients and values to replace recipe ingredients with.
     */
    public void setIngredients ( final Map<Ingredient, Integer> ingredients ) {
        this.ingredients.clear();
        ingredients.entrySet().forEach( e -> this.ingredients.put( e.getKey(), e.getValue() ) );
    }

    /**
     * Gets the amount of an ingredient required for a recipe.
     *
     * @param ingredient
     *            ingredient to check
     * @return amount required
     */
    public Integer getIngredient ( final Ingredient ingredient ) {
        return ingredients.get( ingredient );
    }

    @Override
    public int hashCode () {
        final int prime = 31;
        Integer result = 1;
        result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
        return result;
    }

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
