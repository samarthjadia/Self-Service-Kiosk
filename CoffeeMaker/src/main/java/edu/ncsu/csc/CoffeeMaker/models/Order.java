/**
 *
 */
package edu.ncsu.csc.CoffeeMaker.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * DAO/Model class for Order object representing an order that a customer has
 * placed
 *
 * @author Samarth Jadia (ssjadia), efhaske, sturner4
 */
@Entity
@Table ( name = "orders" )
public class Order extends DomainObject {

    /** Order ID */
    @Id
    @GeneratedValue
    private Long    id;

    /** Order name */
    private String  username;

    /** Name of the order */
    private String  name;

    /** Payment received */
    private int     payment;

    /** Recipe for the order */
    private String  recipe;

    /** Boolean field indicating whether order has been fulfilled */
    private boolean fulfilled;

    /** Boolean field indicating whether order has been picked up */
    private boolean received;

    /**
     * For use by Hibernate.
     */
    public Order () {
        super();
    }

    /**
     * Constructor for Order
     *
     * @param username
     *            username of order
     * @param name
     *            name of order
     * @param recipe
     *            name of recipe
     * @param payment
     *            payment received of order
     * @param fulfilled
     *            whether order is fulfilled or not
     * @param received
     *            whether order has been picked up or not
     */
    public Order ( final String username, final String name, final String recipe, final int payment,
            final boolean fulfilled, final boolean received ) {
        super();
        this.username = username;
        this.name = name;
        this.recipe = recipe;
        this.payment = payment;
        this.fulfilled = fulfilled;
        this.received = received;
    }

    /**
     * @return the id
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * @return the username
     */
    public String getUsername () {
        return username;
    }

    /**
     * @param username
     *            the username to set
     */
    public void setUsername ( final String username ) {
        this.username = username;
    }

    /**
     * @return the name
     */
    public String getName () {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName ( final String name ) {
        this.name = name;
    }

    /**
     * @return the payment
     */
    public int getPayment () {
        return payment;
    }

    /**
     * @param payment
     *            the payment to set
     */
    public void setPayment ( final int payment ) {
        this.payment = payment;
    }

    /**
     * @return the fulfilled
     */
    public boolean isFulfilled () {
        return fulfilled;
    }

    /**
     * @param fulfilled
     *            the fulfilled to set
     */
    public void setFulfilled ( final boolean fulfilled ) {
        this.fulfilled = fulfilled;
    }

    /**
     * @return the received
     */
    public boolean isReceived () {
        return received;
    }

    /**
     * @param received
     *            the received to set
     */
    public void setReceived ( final boolean received ) {
        this.received = received;
    }

    /**
     * @return the recipe
     */
    public String getRecipe () {
        return recipe;
    }

    /**
     * @param recipe
     *            the recipe to set
     */
    public void setRecipe ( final String recipe ) {
        this.recipe = recipe;
    }

}
