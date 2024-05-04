package edu.ncsu.csc.CoffeeMaker.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller class for the URL mappings for CoffeeMaker. The controller returns
 * the approprate HTML page in the /src/main/resources/templates folder. For a
 * larger application, this should be split across multiple controllers.
 *
 * @author Kai Presler-Marshall
 */
@Controller
public class MappingController {
    /**
     * On a GET request to /staff, the UserController will return
     * /src/main/resources/templates/staff.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/staff", "/staff.html" } )
    public String staff ( final Model model ) {
        return "staff";
    }

    /**
     * On a GET request to the privacy policy.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/privacyPolicy", "/privacyPolicy.html" } )
    public String privacyPolicy ( final Model model ) {
        return "privacyPolicy";
    }

    /**
     * On a GET request to /customer, the UserController will return
     * /src/main/resources/templates/customer.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/customer", "customer.html" } )
    public String customer ( final Model model ) {
        return "customer";
    }

    /**
     * On a GET request to /login, the UserController will return
     * /src/main/resources/templates/login.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/login", "/" } )
    public String login ( final Model model ) {
        return "login";
    }

    /**
     * On a GET request to /register, the UserController will return
     * /src/main/resources/templates/register.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/register", "/register.html" } )
    public String register ( final Model model ) {
        return "register";
    }

    /**
     * On a GET request to /ingredient, the IngredientController will return
     * /src/main/resources/templates/ingredient.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/ingredient", "/ingredient.html" } )
    public String addIngredientPage ( final Model model ) {
        return "ingredient";
    }

    /**
     * On a GET request to /recipe, the RecipeController will return
     * /src/main/resources/templates/recipe.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/recipe", "/recipe.html" } )
    public String addRecipePage ( final Model model ) {
        return "recipe";
    }

    /**
     * On a GET request to /deleterecipe, the DeleteRecipeController will return
     * /src/main/resources/templates/deleterecipe.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/deleterecipe", "/deleterecipe.html" } )
    public String deleteRecipeForm ( final Model model ) {
        return "deleterecipe";
    }

    /**
     * On a GET request to /editrecipe, the EditRecipeController will return
     * /src/main/resources/templates/editrecipe.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/editrecipe", "/editrecipe.html" } )
    public String editRecipeForm ( final Model model ) {
        return "editrecipe";
    }

    /**
     * Handles a GET request for inventory. The GET request provides a view to
     * the client that includes the list of the current ingredients in the
     * inventory and a form where the client can enter more ingredients to add
     * to the inventory.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/inventory", "/inventory.html" } )
    public String inventoryForm ( final Model model ) {
        return "inventory";
    }

    /**
     * On a GET request to /placeorder, the OrderController will return
     * /src/main/resources/templates/placeorder.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/placeorder", "/placeorder.html" } )
    public String placeOrderForm ( final Model model ) {
        return "placeorder";
    }

    /**
     * On a GET request to /fulfillorder, the OrderController will return
     * /src/main/resources/templates/fulfillorder.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/fulfillorder", "/fulfillorder.html" } )
    public String fulfillOrderPage ( final Model model ) {
        return "fulfillorder";
    }

    /**
     * On a GET request to /pickuporder, the OrderController will return
     * /src/main/resources/templates/pickuporder.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/pickuporder", "/pickuporder.html" } )
    public String pickupOrder ( final Model model ) {
        return "pickuporder";
    }

    /**
     * On a GET request to /orderhistory, the OrderController will return
     * /src/main/resources/templates/orderhistory.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/orderhistory", "/orderhistory.html" } )
    public String orderhistory ( final Model model ) {
        return "orderhistory";
    }
}
