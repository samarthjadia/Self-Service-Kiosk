package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.TestConfig;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class InventoryTest {

    @Autowired
    private InventoryService inventoryService;

    @BeforeEach
    public void setup () {
        inventoryService.deleteAll();
        final Inventory ivt = inventoryService.getInventory();
        ivt.addIngredient( new Ingredient( "CHOCOLATE", 500 ) );
        ivt.addIngredient( new Ingredient( "COFFEE", 500 ) );
        ivt.addIngredient( new Ingredient( "MILK", 500 ) );
        ivt.addIngredient( new Ingredient( "SUGAR", 500 ) );

        inventoryService.save( ivt );
    }

    @Test
    @Transactional
    public void testConsumeInventory () {
        final Inventory ivt = inventoryService.getInventory();
        // Create recipe to be used
        final Recipe recipe = new Recipe();
        recipe.setName( "Delicious Not-Coffee" );
        recipe.addIngredient( new Ingredient( "CHOCOLATE", 10 ) );
        recipe.addIngredient( new Ingredient( "MILK", 20 ) );
        recipe.addIngredient( new Ingredient( "SUGAR", 5 ) );
        recipe.addIngredient( new Ingredient( "COFFEE", 1 ) );

        // recipe.setChocolate( 10 );
        // recipe.setMilk( 20 );
        // recipe.setSugar( 5 );
        // recipe.setCoffee( 1 );

        recipe.setPrice( 5 );

        ivt.useIngredients( recipe );

        /*
         * Make sure that all of the inventory fields are now properly updated
         */
        final List<Ingredient> l = ivt.getInventoryList();
        Assertions.assertEquals( 490, (int) l.get( 0 ).getAmount() );
        Assertions.assertEquals( 480, (int) l.get( 2 ).getAmount() );
        Assertions.assertEquals( 495, (int) l.get( 3 ).getAmount() );
        Assertions.assertEquals( 499, (int) l.get( 1 ).getAmount() );
    }

    @Test
    @Transactional
    public void testAddInventoryValid () {
        Inventory ivt = inventoryService.getInventory();
        // Create list of ingredients that contain the amount to add
        final List<Ingredient> l = new ArrayList<Ingredient>();
        l.add( new Ingredient( "COFFEE", 5 ) );
        l.add( new Ingredient( "MILK", 3 ) );
        l.add( new Ingredient( "SUGAR", 7 ) );
        l.add( new Ingredient( "CHOCOLATE", 2 ) );
        // adds the amount for the ingredient in the inventory
        ivt.updateIngredientAmounts( l );

        /* Save and retrieve again to update with DB */
        inventoryService.save( ivt );

        ivt = inventoryService.getInventory();
        final List<Ingredient> inventoryList = ivt.getInventoryList();
        Assertions.assertEquals( 505, (int) inventoryList.get( 1 ).getAmount(),
                "Adding to the inventory should result in correctly-updated values for coffee" );
        Assertions.assertEquals( 503, (int) inventoryList.get( 2 ).getAmount(),
                "Adding to the inventory should result in correctly-updated values for milk" );
        Assertions.assertEquals( 507, (int) inventoryList.get( 3 ).getAmount(),
                "Adding to the inventory should result in correctly-updated values sugar" );
        Assertions.assertEquals( 502, (int) inventoryList.get( 0 ).getAmount(),
                "Adding to the inventory should result in correctly-updated values chocolate" );

    }

    @Test
    @Transactional
    public void testAddInventoryInvalidCoffee () {
        final Inventory ivt = inventoryService.getInventory();
        // Create list of ingredients that contain the amount to add
        final List<Ingredient> l = new ArrayList<Ingredient>();
        l.add( new Ingredient( "COFFEE", -5 ) );
        l.add( new Ingredient( "MILK", 3 ) );
        l.add( new Ingredient( "SUGAR", 7 ) );
        l.add( new Ingredient( "CHOCOLATE", 2 ) );

        try {
            // ivt.addIngredients( -5, 3, 7, 2 );
            // updates ingredient amounts
            ivt.updateIngredientAmounts( l );
        }
        catch ( final IllegalArgumentException iae ) {
            final List<Ingredient> inventoryList = ivt.getInventoryList();
            Assertions.assertEquals( 500, (int) inventoryList.get( 1 ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- coffee" );
            Assertions.assertEquals( 500, (int) inventoryList.get( 2 ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- milk" );
            Assertions.assertEquals( 500, (int) inventoryList.get( 3 ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- sugar" );
            Assertions.assertEquals( 500, (int) inventoryList.get( 0 ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- chocolate" );
        }
    }

    @Test
    @Transactional
    public void testAddInventoryInvalidMilk () {
        final Inventory ivt = inventoryService.getInventory();
        // Create list of ingredients that contain the amount to add
        final List<Ingredient> l = new ArrayList<Ingredient>();
        l.add( new Ingredient( "COFFEE", 5 ) );
        l.add( new Ingredient( "MILK", -3 ) );
        l.add( new Ingredient( "SUGAR", 7 ) );
        l.add( new Ingredient( "CHOCOLATE", 2 ) );
        try {
            // ivt.addIngredients( 5, -3, 7, 2 );
            // updates ingredient amounts
            ivt.updateIngredientAmounts( l );
        }
        catch ( final IllegalArgumentException iae ) {
            final List<Ingredient> inventoryList = ivt.getInventoryList();
            Assertions.assertEquals( 500, (int) inventoryList.get( 1 ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- coffee" );
            Assertions.assertEquals( 500, (int) inventoryList.get( 2 ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- milk" );
            Assertions.assertEquals( 500, (int) inventoryList.get( 3 ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- sugar" );
            Assertions.assertEquals( 500, (int) inventoryList.get( 0 ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- chocolate" );

        }

    }

    @Test
    @Transactional
    public void testAddInventoryInvalidSugar () {
        final Inventory ivt = inventoryService.getInventory();
        // Create list of ingredients that contain the amount to add
        final List<Ingredient> l = new ArrayList<Ingredient>();
        l.add( new Ingredient( "COFFEE", 5 ) );
        l.add( new Ingredient( "MILK", 3 ) );
        l.add( new Ingredient( "SUGAR", -7 ) );
        l.add( new Ingredient( "CHOCOLATE", 2 ) );
        try {
            // ivt.addIngredients( 5, 3, -7, 2 );
            // updates ingredient amounts
            ivt.updateIngredientAmounts( l );
        }
        catch ( final IllegalArgumentException iae ) {
            final List<Ingredient> inventoryList = ivt.getInventoryList();
            Assertions.assertEquals( 500, (int) inventoryList.get( 1 ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- coffee" );
            Assertions.assertEquals( 500, (int) inventoryList.get( 2 ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- milk" );
            Assertions.assertEquals( 500, (int) inventoryList.get( 3 ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- sugar" );
            Assertions.assertEquals( 500, (int) inventoryList.get( 0 ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- chocolate" );

        }

    }

    @Test
    @Transactional
    public void testAddInventoryInvalidChocolate () {
        final Inventory ivt = inventoryService.getInventory();
        // Create list of ingredients that contain the amount to add
        final List<Ingredient> l = new ArrayList<Ingredient>();
        l.add( new Ingredient( "COFFEE", 5 ) );
        l.add( new Ingredient( "MILK", 3 ) );
        l.add( new Ingredient( "SUGAR", 7 ) );
        l.add( new Ingredient( "CHOCOLATE", -2 ) );
        try {
            // ivt.addIngredients( 5, 3, 7, -2 );
            // updates ingredient amounts
            ivt.updateIngredientAmounts( l );
        }
        catch ( final IllegalArgumentException iae ) {
            final List<Ingredient> inventoryList = ivt.getInventoryList();
            Assertions.assertEquals( 500, (int) inventoryList.get( 1 ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- coffee" );
            Assertions.assertEquals( 500, (int) inventoryList.get( 2 ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- milk" );
            Assertions.assertEquals( 500, (int) inventoryList.get( 3 ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- sugar" );
            Assertions.assertEquals( 500, (int) inventoryList.get( 0 ).getAmount(),
                    "Trying to update the Inventory with an invalid value for coffee should result in no changes -- chocolate" );

        }

    }

    @Test
    @Transactional
    public void testCheckIngredients () {
        final Inventory ivt = inventoryService.getInventory();
        final List<Ingredient> i = ivt.getInventoryList();
        assertEquals( 500, i.get( 0 ).getAmount() );
        assertEquals( 500, i.get( 1 ).getAmount() );
        assertEquals( 500, i.get( 2 ).getAmount() );
        assertEquals( 500, i.get( 3 ).getAmount() );

        final Exception e1 = assertThrows( IllegalArgumentException.class, () -> ivt.checkIngredientUnits( "no" ) );
        assertEquals( "Units of ingredient must be a positive integer.", e1.getMessage() );
        final Exception e2 = assertThrows( IllegalArgumentException.class, () -> ivt.checkIngredientUnits( "-1" ) );
        assertEquals( "Units of ingredient must be a positive integer.", e2.getMessage() );

    }

    @Test
    @Transactional
    public void testToString () {
        final Inventory ivt = inventoryService.getInventory();

        assertEquals( "CHOCOLATE: 500\nCOFFEE: 500\nMILK: 500\nSUGAR: 500\n", ivt.toString() );
    }

    /*
     * Test making a coffee with the inventory not having enough ingredients
     * (coffee, milk, sugar, chocolate)
     */
    @Test
    @Transactional
    public void testEnoughIngredientsInvalid () {
        final Inventory ivt = inventoryService.getInventory();
        final Recipe r1 = new Recipe();
        r1.setName( "Bathtub of Coffee" );
        r1.setPrice( 5 );
        r1.addIngredient( new Ingredient( "CHOCOLATE", 501 ) );
        r1.addIngredient( new Ingredient( "MILK", 501 ) );
        r1.addIngredient( new Ingredient( "SUGAR", 501 ) );
        r1.addIngredient( new Ingredient( "COFFEE", 501 ) );
        // r1.setCoffee( 501 );
        // r1.setMilk( 501 );
        // r1.setSugar( 501 );
        // r1.setChocolate( 501 );
        // Inventory has 500 of each ingredient which is not enough for this
        // recipe (which needs 501 of each)
        Assertions.assertFalse( ivt.enoughIngredients( r1 ) );
    }

}
