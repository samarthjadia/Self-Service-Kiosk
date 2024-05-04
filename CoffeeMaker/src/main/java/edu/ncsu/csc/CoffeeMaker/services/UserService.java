package edu.ncsu.csc.CoffeeMaker.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.repositories.UserRepository;

/**
 * Service class for managing users and providing user details for
 * authentication. This class extends Service and implements UserDetailsService
 * (needed DTO for Spring Security authentication).
 *
 * @author Samarth Jadia (ssjadia), efhaske, sturner4
 */
@Component
@Transactional
public class UserService extends Service<User, Long> implements UserDetailsService {

    /** Instance of UserRepository */
    @Autowired
    private UserRepository ur;

    /**
     * Retrieves repository for managing Users.
     *
     * @return JpaRepository instance responsible for User management
     */
    @Override
    protected JpaRepository<User, Long> getRepository () {
        return ur;
    }

    /**
     * Retrieves user details by username for authentication.
     *
     * @param username
     *            the username of the user to retrieve details for
     * @return the UserDetails object containing user details
     * @throws UsernameNotFoundException
     *             if the user with the given username isn't found
     */
    @Override
    public UserDetails loadUserByUsername ( final String username ) throws UsernameNotFoundException {
        final User user = ur.findByUsername( username );
        if ( user == null ) {
            throw new UsernameNotFoundException( "User not found with username: " + username );
        }

        final String role = ( user.isStaff() == true ) ? "STAFF" : "CUSTOMER";

        // Had to do it this way because spring User import collides with out
        // Customer User object
        return org.springframework.security.core.userdetails.User.withUsername( user.getUsername() )
                .password( user.getPassword() ).roles( role ).build();
    }

    /**
     * Checks if a user exists with the given username. This method is used in
     * the APIUserController when creating a new user to check if a username
     * already exists in the system.
     *
     * @param username
     *            the username to check for existence
     * @return true if a user exists with the given username, false if not
     */
    public boolean userExists ( final String username ) {
        return ( ur.findByUsername( username ) != null );
    }

    /**
     * Creates a new user by saving the parameter user object to the database.
     * This method is used in the APIUserController when creating a new user.
     *
     * @param user
     *            the user object to be created
     */
    public void createUser ( final User user ) {
        ur.save( user );
    }

}
