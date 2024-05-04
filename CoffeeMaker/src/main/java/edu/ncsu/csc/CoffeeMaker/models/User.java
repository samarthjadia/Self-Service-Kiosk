package edu.ncsu.csc.CoffeeMaker.models;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Model class for User Roles
 *
 * @author ssjadia, efhaske, sturner4
 */
@Entity
@Table ( name = "users" )
public class User extends DomainObject {
    /** User id */
    @Id
    @GeneratedValue
    private Long    id;

    /** User username */
    private String  username;

    /** User password */
    private String  password;

    /** Boolean to determine whether User is a staff member or customer */
    private boolean staff;

    /**
     * Empty user constructor
     */
    public User () {

    }

    /**
     * Constructs an User Object based on the username, password, and staff
     * parameters. If the staff parameter is true the user will be a staff user,
     * if the staff parameter is true then the user will be a customer.
     *
     * @param username
     *            the username of the user
     * @param password
     *            the password of the user
     * @param staff
     *            the boolean value determining whether the user is staff or not
     */
    public User ( final String username, final String password, final boolean staff ) {
        super();
        this.setUsername( username );
        this.setPassword( password );
        this.setStaff( staff );
    }

    @Override
    public Serializable getId () {
        return id;
    }

    /**
     * gets the Users username
     *
     * @return username as a string
     */
    public String getUsername () {
        return username;
    }

    /**
     * Set the ID of the User (Used by Hibernate).
     *
     * @param id
     *            the ID
     */
    @SuppressWarnings ( "unused" )
    private void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Returns the User's username as a String
     */
    @Override
    public String toString () {
        return "User = [" + username + "]";
    }

    /**
     * sets the Users username
     *
     * @param username
     *            username that is to be set
     * @throws IllegalArgumentException
     *             if username is empty or null
     */
    public void setUsername ( final String username ) {
        if ( username == null || username == "" ) {
            throw new IllegalArgumentException( "username is required" );
        }
        this.username = username;
    }

    /**
     * gets the users password
     *
     * @return password as a string
     */
    public String getPassword () {
        return password;
    }

    /**
     * sets the users password
     *
     * @param password
     *            password to be set
     * @throws IllegalArgumentException
     *             when the password is null or empty
     */
    public void setPassword ( final String password ) {
        if ( password == null || password == "" ) {
            throw new IllegalArgumentException( "password is required" );
        }
        this.password = password;
    }

    /**
     * gets the staff boolean
     *
     * @return boolean if the user is a staff or not.
     */
    public boolean isStaff () {
        return staff;
    }

    /**
     * sets the user if staff or not.
     *
     * @param staff
     */
    public void setStaff ( final boolean staff ) {
        this.staff = staff;
    }

    /**
     * Determines whether two users are the same based on username
     *
     * @return true if users are the same, false otherwise
     */
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
        final User other = (User) obj;
        return Objects.equals( username, other.username );
    }

}
