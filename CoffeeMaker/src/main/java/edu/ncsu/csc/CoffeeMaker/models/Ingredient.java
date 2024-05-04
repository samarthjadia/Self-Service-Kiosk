package edu.ncsu.csc.CoffeeMaker.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Ingredient extends DomainObject {
    /** Ingredient ID */
    @Id
    @GeneratedValue
    private Long    id;

    /** Ingredient name */
    private String  name;

    /** Stored amount in inventory */
    private Integer amount;

    /**
     * For use by Hibernate.
     */
    public Ingredient () {
        super();
    }

    /**
     * @param ingredient
     * @param amount
     */
    public Ingredient ( final String name, final Integer stored ) {
        super();
        this.name = name;
        setAmount( stored );
    }

    /**
     * Gets the ID of the ingredient in the DB.
     *
     * @return Long
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Set the ID of the Ingredient (Used by Hibernate).
     *
     * @param id
     *            the ID
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Gets the name of the ingredient.
     *
     * @return String
     */
    public String getName () {
        return name;
    }

    /**
     * Gets the amount of this ingredient stored in the inventory.
     *
     * @return amount stored
     */
    public Integer getAmount () {
        return amount;
    }

    /**
     * Sets the amount of this ingredient stored in the inventory.
     *
     * @param amount
     *            amount to set inventory storage to
     */
    private void setAmount ( final Integer amount ) {
        this.amount = amount;
    }

    /**
     * Returns true if there is enough of this ingredient to make the recipe.
     *
     * @param r
     *            recipe to use to check if there is enough of this ingredient
     * @return true if enough to make the beverage
     */
    public boolean enoughIngredients ( final Recipe r ) {
        final Integer i = r.getIngredient( this );
        return i == null || i < getAmount();
    }

    /**
     * Removes the amount of this ingredient used to make the specified recipe.
     * Assumes that the user has checked that there are enough of each
     * ingredient to make the recipe.
     *
     * @param r
     *            recipe to make
     * @return true if recipe is made.
     */
    public boolean useIngredients ( final Recipe r ) {
        if ( enoughIngredients( r ) ) {
            setAmount( getAmount() - r.getIngredient( this ) );
            return true;
        }
        return false;
    }

    /**
     * Increases ingredient stock.
     *
     * @param amount
     *            amount to add
     * @return true if successful, false if invalid amount
     */
    public boolean add ( final Integer amount ) {
        if ( amount < 0 ) {
            return false;
        }

        setAmount( getAmount() + amount );
        return true;
    }

    @Override
    public int hashCode () {
        return name.hashCode();
    }

    /**
     * Returns a string describing this ingredient entry.
     *
     * @return String
     */
    @Override
    public String toString () {
        return getName();
    }
}
