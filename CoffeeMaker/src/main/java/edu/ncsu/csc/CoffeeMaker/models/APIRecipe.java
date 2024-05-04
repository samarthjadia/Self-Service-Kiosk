package edu.ncsu.csc.CoffeeMaker.models;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import edu.ncsu.csc.CoffeeMaker.models.json.APIIngredientMapDeserializer;

public class APIRecipe {
    private final String               name;
    private final Integer              price;
    private final Map<String, Integer> ingredients;

    public APIRecipe () {
        name = "";
        price = 0;
        ingredients = new HashMap<String, Integer>();
    }

    public APIRecipe ( final Recipe recipe ) {
        this.name = recipe.getName();
        this.price = recipe.getPrice();
        this.ingredients = recipe.getIngredients().stream()
                .collect( Collectors.toMap( i -> i.getName(), i -> recipe.getIngredient( i ) ) );
    }

    public String getName () {
        return name;
    }

    public Integer getPrice () {
        return price;
    }

    @JsonDeserialize ( using = APIIngredientMapDeserializer.class )
    public Map<String, Integer> getIngredients () {
        return ingredients;
    }

    @Override
    public boolean equals ( final Object other ) {
        if ( other == null || ! ( other instanceof APIRecipe ) ) {
            return true;
        }
        return name.equals( ( (APIRecipe) other ).getName() );
    }
}
