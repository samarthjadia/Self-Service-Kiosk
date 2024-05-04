package edu.ncsu.csc.CoffeeMaker.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.repositories.IngredientRepository;

/**
 * Service class for ingredient functionality
 *
 * @author Samarth Jadia (ssjadia)
 */
@Component
@Transactional
public class IngredientService extends Service<Ingredient, Long> {
    @Autowired
    IngredientRepository ir;

    @Override
    public IngredientRepository getRepository () {
        return ir;
    }

    /**
     * Find an ingredient with the provided name.
     *
     * @param name
     *            Name of the ingredient to find
     * @return found ingredient, null if none
     */
    public Ingredient findByName ( final String name ) {
        return ir.findByName( name );
    }
}
