package edu.ncsu.csc.CoffeeMaker.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Inventory for the coffee maker. Inventory is tied to the database using
 * Hibernate libraries. See InventoryRepository and InventoryService for the
 * other two pieces used for database support.
 *
 * @author Kai Presler-Marshall
 * @author Sean Turner
 * @author Ethan Haske
 * @author Samarth Jadia
 */
@Entity
public class Inventory extends DomainObject {

    /** Id for inventory entry */
    @Id
    @GeneratedValue
    private Long             id;

    /**
     * Represents the entire list of ingredients in the inventory
     */
    @ElementCollection
    @OneToMany ( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    private List<Ingredient> inventoryList;

    /**
     * Returns the inventory as a list of Ingredient objects
     *
     * @return the list of ingredients in the inventory
     */
    public List<Ingredient> getInventoryList () {
        return inventoryList;
    }

    /**
     * Sets the private field called inventoryList to the parameter passed
     *
     * @param inventoryList
     *            the list of Ingredient objects
     */
    public void setInventoryList ( final List<Ingredient> inventoryList ) {
        this.inventoryList = inventoryList;
    }

    /**
     * Empty constructor for Hibernate
     */
    public Inventory () {
        // Intentionally empty so that Hibernate can instantiate
        // Inventory object.
        this.inventoryList = new ArrayList<Ingredient>();
    }

    /**
     * Use this to create the inventory with an initial list of ingredients.
     *
     * @param ingredients
     *            the list of ingredients to be initialized in the inventory
     */
    public Inventory ( final List<Ingredient> ingredients ) {
        if ( ingredients != null ) {
            this.inventoryList = ingredients;
        }
    }

    /**
     * Returns the ID of the entry in the DB
     *
     * @return long
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Set the ID of the Inventory (Used by Hibernate)
     *
     * @param id
     *            the ID
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Returns true if there are enough ingredients to make the beverage.
     *
     * @param r
     *            recipe to check if there are enough ingredients
     * @return true if enough ingredients to make the beverage
     */
    public boolean enoughIngredients ( final Recipe r ) {
        final List<Ingredient> ingredients = r.getIngredients();
        for ( int i = 0; i < ingredients.size(); i++ ) {
            final int idx = getIngredientByName( ingredients.get( i ).getName() );
            if ( idx == -1 || this.inventoryList.get( idx ).getAmount() < ingredients.get( i ).getAmount() ) {
                return false;
            }
        }
        return true;
    }

    /**
     * Finds the ingredient in inventory by given name
     *
     * @param name
     *            Name of the ingredient to get
     *
     * @return index of the ingredient in inventory, -1 if not found
     */
    private int getIngredientByName ( final String name ) {
        for ( int i = 0; i < inventoryList.size(); i++ ) {
            if ( this.inventoryList.get( i ).getName().equals( name ) ) {
                return i;
            }
        }
        return -1;

    }

    /**
     * Removes the ingredients used to make the specified recipe. Assumes that
     * the user has checked that there are enough ingredients to make
     *
     * @param r
     *            recipe to make
     * @return true if recipe is made.
     */
    public boolean useIngredients ( final Recipe r ) {
        if ( !enoughIngredients( r ) ) {
            return false;
        }
        final List<Ingredient> ingredients = r.getIngredients();
        for ( int i = 0; i < ingredients.size(); i++ ) {
            final int idx = getIngredientByName( ingredients.get( i ).getName() );
            final int initialAmount = this.inventoryList.get( idx ).getAmount();
            final int recipeAmount = ingredients.get( i ).getAmount();
            this.inventoryList.get( idx ).setAmount( initialAmount - recipeAmount );
        }
        return true;
    }

    /**
     * Adds ingredients to the inventory
     *
     * @param ingredients
     *            A list of the ingredient units being added to the inventory
     * @return true if successful
     */
    public boolean updateIngredientAmounts ( final List<Ingredient> ingredients ) {
        for ( int i = 0; i < ingredients.size(); i++ ) {
            if ( ingredients.get( i ).getAmount() < 0 ) {
                throw new IllegalArgumentException( "Amount cannot be negative" );
            }
        }
        if ( inventoryList.size() == 0 ) {
            // Initializing First and Only Inventory
            // This would only run once in the programs whole cycle
            for ( int i = 0; i < ingredients.size(); i++ ) {
                inventoryList.add( ingredients.get( i ) );
            }
        }
        else {
            // Updating the current ingredient units that only currently exist
            // in the inventory
            // Would run every time you update a unit for an ingredient that
            // exists in the inventory
            for ( int i = 0; i < ingredients.size(); i++ ) {
                final int idx = getIngredientByName( ingredients.get( i ).getName() );
                final int initialAmount = this.inventoryList.get( idx ).getAmount();
                final int recipeAmount = ingredients.get( i ).getAmount();
                this.inventoryList.get( idx ).setAmount( initialAmount + recipeAmount );
            }
        }
        return true;
    }

    /**
     * Check the that ingredient units can be converted to a integer and is
     * greater than or equal to 0.
     *
     * @param ingredient
     *            the ingredient amount being checked
     * @return integer representation of ingredient amount.
     */
    public Integer checkIngredientUnits ( final String ingredient ) {
        Integer amount = 0;
        try {
            amount = Integer.parseInt( ingredient );
        }
        catch ( final NumberFormatException e ) {
            throw new IllegalArgumentException( "Units of ingredient must be a positive integer." );
        }
        if ( amount < 0 ) {
            throw new IllegalArgumentException( "Units of ingredient must be a positive integer." );

        }

        return amount;
    }

    /**
     * Takes an ingredient object as a parameter and attempts to add it to the
     * inventory. It only successfully adds an ingredient if it does not already
     * exist in the inventory.
     *
     * @param ingredient
     *            the ingredient to add to the inventory
     * @return false if the ingredient is null or already exists in the
     *         inventory, true otherwise.
     */
    public boolean addIngredient ( final Ingredient ingredient ) {
        if ( ingredient == null || getIngredientByName( ingredient.getName() ) != -1 ) {
            return false;
        }
        this.inventoryList.add( ingredient );
        return true;
    }

    /**
     * Returns a string describing the current contents of the inventory.
     *
     * @return String
     */
    @Override
    public String toString () {
        final StringBuffer buf = new StringBuffer();
        for ( final Ingredient ingredient : this.inventoryList ) {
            buf.append( ingredient.getName() + ": " );
            buf.append( ingredient.getAmount() );
            buf.append( "\n" );
        }
        return buf.toString();
    }

}
