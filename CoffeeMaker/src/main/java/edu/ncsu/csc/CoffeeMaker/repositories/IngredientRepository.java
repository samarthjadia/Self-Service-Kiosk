package edu.ncsu.csc.CoffeeMaker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;

/**
 * Repository for ingredient manipulation
 *
 * @author Samarth Jadia (ssjadia)
 */
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    /**
     * Finds an Ingredient object with the provided name. Spring will generate
     * code to make this happen.
     *
     * @param name
     *            Name of the ingredient
     * @return Found ingredient, null if none.
     */
    Ingredient findByName ( String name );
}
