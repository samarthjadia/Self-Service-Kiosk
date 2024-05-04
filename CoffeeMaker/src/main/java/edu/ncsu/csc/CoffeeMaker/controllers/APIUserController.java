package edu.ncsu.csc.CoffeeMaker.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

@SuppressWarnings ( { "unchecked", "rawtypes" } )

@RestController
public class APIUserController extends APIController {
    /**
     * UserService object, to be autowired in by Spring to allow for
     * manipulating the User model.
     */
    @Autowired
    private UserService     userService;

    /**
     * passwordEncoder object, to be autowired in by Spring to allow for
     * encrypting users passwords.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * REST API method to provide POST access to the User model. This is used to
     * register a new User into the system by automatically converting the JSON
     * RequestBody provided to a User object. Invalid JSON will fail.
     *
     * @param user
     *            user to be registered into the system
     * @return ResponseEntity that is success response if the user is
     *         registered. Else error response.
     */
    @PostMapping ( BASE_PATH + "/register" )
    public ResponseEntity registerUser ( @RequestBody final User user ) {
        // Checks to see if that username has already been taken
        if ( userService.userExists( user.getUsername() ) ) {
            return new ResponseEntity( errorResponse( new StringBuilder( "Username '" ).append( user.getUsername() )
                    .append( "' already exists" ).toString() ), HttpStatus.CONFLICT );
        }
        // Creates the new user and encodes their password
        final User newUser = new User( user.getUsername(), passwordEncoder.encode( user.getPassword() ), false );
        // saves the user into the database
        userService.createUser( newUser );
        // returns a successful response entity that the user had been created
        return new ResponseEntity(
                successResponse(
                        new StringBuilder( user.getUsername() ).append( " profile successfully created." ).toString() ),
                HttpStatus.OK );
    }

    /**
     * Gets the username for the currently logged in user
     *
     * @param authentication
     * @return The username of the user is logged in.
     */
    @GetMapping ( BASE_PATH + "/users/username" )
    public ResponseEntity getUsername ( final Authentication authentication ) {
        return new ResponseEntity( successResponse( authentication.getName() ), HttpStatus.OK );
    }

}
