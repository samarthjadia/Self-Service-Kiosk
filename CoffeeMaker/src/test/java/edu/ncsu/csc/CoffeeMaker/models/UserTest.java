package edu.ncsu.csc.CoffeeMaker.models;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests the User Model
 */
class UserTest {

    private static String  username = "username";
    private static String  password = "password";
    private static Boolean isStaff  = true;

    /**
     * Tests to ensure constructor properly instantiates a valid User object
     * when all parameters are passed
     */
    @Test
    void testConstructorValid () {
        final User user = assertDoesNotThrow( () -> new User( username, password, isStaff ) );

        assertEquals( "username", user.getUsername() );
        assertEquals( "password", user.getPassword() );
        assertEquals( true, user.isStaff() );
    }

    /**
     * Tests the functionality of the setUsername method
     */
    @Test
    void testSetUsername () {
        final User user = assertDoesNotThrow( () -> new User( username, password, isStaff ) );

        assertEquals( "username", user.getUsername() );
        assertEquals( "password", user.getPassword() );
        assertEquals( true, user.isStaff() );

        // Change the username
        user.setUsername( "new" );
        // Verify the change
        assertEquals( "new", user.getUsername() );

        // Make sure nothing else changed
        assertEquals( "password", user.getPassword() );
        assertEquals( true, user.isStaff() );
    }

    /**
     * Tests the functionality of the setPassword method
     */
    @Test
    void testSetPassword () {
        final User user = assertDoesNotThrow( () -> new User( username, password, isStaff ) );

        assertEquals( "username", user.getUsername() );
        assertEquals( "password", user.getPassword() );
        assertEquals( true, user.isStaff() );

        // Change the username
        user.setPassword( "password2" );
        // Verify the change
        assertEquals( "password2", user.getPassword() );

        // Make sure nothing else changed
        assertEquals( "username", user.getUsername() );
        assertEquals( true, user.isStaff() );

        // Change the password back
        user.setPassword( password );
        assertEquals( "password", user.getPassword() );

        // Make sure nothing else has changed
        assertEquals( "username", user.getUsername() );
        assertEquals( true, user.isStaff() );

    }

    /**
     * Tests the functionality of the setStaff method
     */
    @Test
    void testSetStaff () {
        final User user = assertDoesNotThrow( () -> new User( username, password, isStaff ) );

        assertEquals( "username", user.getUsername() );
        assertEquals( "password", user.getPassword() );
        assertEquals( true, user.isStaff() );

        // Change the username
        user.setStaff( false );
        // Verify the change
        assertFalse( user.isStaff() );

        // Make sure nothing else changed
        assertEquals( "username", user.getUsername() );
        assertEquals( "password", user.getPassword() );

    }

    /**
     * Tests the make sure an error is thrown when an invalid username is passed
     * to the constructor
     */
    @Test
    void testConstructorInvalidUsernames () {
        final Exception e1 = assertThrows( IllegalArgumentException.class, () -> {
            new User( null, password, isStaff );
        } );

        assertEquals( "username is required", e1.getMessage() );

        final Exception e2 = assertThrows( IllegalArgumentException.class, () -> {
            new User( "", password, isStaff );
        } );

        assertEquals( "username is required", e2.getMessage() );
    }

    /**
     * Tests to make sure an error is thrown when an invalid password is passed
     * to the constructor
     */
    @Test
    void testConstructorInvalidPasswords () {
        final Exception e1 = assertThrows( IllegalArgumentException.class, () -> {
            new User( username, null, isStaff );
        } );

        assertEquals( "password is required", e1.getMessage() );

        final Exception e2 = assertThrows( IllegalArgumentException.class, () -> {
            new User( username, "", isStaff );
        } );

        assertEquals( "password is required", e2.getMessage() );
    }

    /**
     * Tests the equals method of the User class
     */
    @Test
    void testEquals () {
        final User user1 = assertDoesNotThrow( () -> new User( username, password, isStaff ) );
        final User user2 = assertDoesNotThrow( () -> new User( username, "different", true ) );
        final User user3 = assertDoesNotThrow( () -> new User( username, password, true ) );
        final User user4 = assertDoesNotThrow( () -> new User( "differentguy", password, isStaff ) );

        assertTrue( user1.equals( user2 ) );
        assertTrue( user2.equals( user1 ) );

        assertTrue( user1.equals( user3 ) );
        assertTrue( user3.equals( user1 ) );

        assertTrue( user2.equals( user3 ) );
        assertTrue( user3.equals( user2 ) );

        assertFalse( user2.equals( user4 ) );
        assertFalse( user3.equals( user4 ) );
        assertFalse( user1.equals( user4 ) );

        assertFalse( user4.equals( user1 ) );
        assertFalse( user4.equals( user2 ) );
        assertFalse( user4.equals( user3 ) );

    }

    /**
     * Tests the toString method of the User class
     */
    @Test
    void testToString () {
        final User user1 = assertDoesNotThrow( () -> new User( username, password, isStaff ) );

        assertEquals( "User = [username]", user1.toString() );
    }

}
