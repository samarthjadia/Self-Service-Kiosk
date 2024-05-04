package edu.ncsu.csc.CoffeeMaker.models;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Min;

/**
 * Ingredient for the CoffeeMaker. An ingredient can be added to a recipe and
 * the inventory. The ingredient has a name field to be used when referencing
 * the object and an amount field that represents its units. See
 * IngredientRepository and IngredientService for the other two pieces used for
 * database support.
 *
 * @author Sean Turner
 * @author Ethan Haske
 * @author Samarth Jadia
 */
@Entity
public class Ingredient extends DomainObject {
    /**
     * Unique id that is used to reference an instance of the Ingredient
     */
    @Id
    @GeneratedValue
    private Long    id;
    /**
     * Private field that keeps track of the ingredient name
     */
    private String  name;

    /**
     * Private field that keeps track of the ingredient amount
     */
    @Min ( 0 )
    private Integer amount;

    /**
     * Default constructor for Ingredient
     */
    public Ingredient () {
        super();
    }

    /**
     * Constructor that takes the name and amount of units for an Ingredient and
     * creates a new instance of the Object.
     *
     * @param ingredient
     *            the name of the ingredient
     * @param amount
     *            the amount of units for the ingredient
     */
    public Ingredient ( final String ingredient, final Integer amount ) {
        super();
        this.name = ingredient;
        this.amount = amount;
    }

    // /**
    // * Returns the name field of the ingredient
    // *
    // * @return the name of the ingredient
    // */
    // public String getIngredient () {
    // return name;
    // }

    /**
     * Returns the name field of the ingredient
     *
     * @return the name of the ingredient
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the name field of the ingredient
     *
     * @param name
     *            the name of the ingredient
     */
    public void setName ( final String name ) {
        this.name = name;
    }

    // /**
    // * Sets the name field of the ingredient
    // *
    // * @param ingredient
    // * the name of the ingredient
    // */
    // public void setIngredient ( final String ingredient ) {
    // this.name = ingredient;
    // }

    /**
     * Returns the field amount of the ingredient
     *
     * @return the amount of units that exists for the ingredient
     */
    public Integer getAmount () {
        return amount;
    }

    /**
     * Sets the amount of units for the ingredient
     *
     * @param amount
     *            the amount of units that exists for the ingredient
     */
    public void setAmount ( final Integer amount ) {
        // if ( amount < 0 ) {
        // throw new IllegalArgumentException( "Must be a positive integer" );
        // }
        this.amount = amount;
    }

    /**
     * Sets the id of the ingredient to be used by the database
     *
     * @param id
     *            the id of the ingredient
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Returns the id of the ingredient
     *
     * @return the id of the ingredient
     */
    @Override
    public Serializable getId () {
        return id;
    }

    /**
     * Returns a string representation of the ingredient Object
     *
     * @return the string representation of ingredient
     */
    @Override
    public String toString () {
        return "Ingredient [ingredient=" + name + ", amount=" + amount + "]";
    }
}
