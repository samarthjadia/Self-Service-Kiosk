package edu.ncsu.csc.CoffeeMaker.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith ( SpringExtension.class )
class APIUserTest extends APITest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserService           userService;

    @MockBean
    private Authentication        authentication;

    @Autowired
    private PasswordEncoder       passwordEncoder;

    /**
     * Sets up the tests.
     */
    @Override
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        userService.deleteAll();
    }

    // Private method to create a user for testing
    private User createUser ( final String username, final String password, final Boolean isStaff ) {
        final User user = new User( username, password, isStaff );
        return user;
    }

    @Test
    @Transactional
    public void testRegisterUserValid () throws Exception {
        userService.deleteAll();

        Assertions.assertEquals( 0, userService.findAll().size(), "There should be no Users in the CoffeeMaker" );

        final User user = createUser( "username", "password", false );

        mvc.perform( post( "/api/v1/register" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( user ) ) ).andExpect( status().isOk() );

        final List<User> i = userService.findAll();
        Assertions.assertEquals( 1, i.size() );
        Assertions.assertEquals( user.getUsername(), i.get( 0 ).getUsername() );
    }

    @Test
    @Transactional
    public void testRegisterUserDuplicate () throws Exception {
        userService.deleteAll();

        Assertions.assertEquals( 0, userService.findAll().size(), "There should be no Users in the CoffeeMaker" );

        final User user = createUser( "username", "password", false );
        final User user2 = createUser( "username", "password", true );

        userService.save( user );

        mvc.perform( post( "/api/v1/register" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( user2 ) ) ).andExpect( status().is4xxClientError() );

        Assertions.assertEquals( 1, userService.findAll().size(), "There should only be one user in the CoffeeMaker" );
    }

    @Override
    protected String getEndpoint () {
        return "/api/v1/register";
    }

    @Test
    @Transactional
    public void testGetUsernameAfterLogin () throws Exception {
        // Register user
        final User user = createUser( "customer", "pass", false );
        // final String encodedPassword = passwordEncoder.encode( "pass" );
        mvc.perform( post( "/api/v1/register" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( user ) ) ).andExpect( status().isOk() );

        // Mock the behavior of the Authentication object
        when( authentication.getName() ).thenReturn( user.getUsername() );

        // Perform login with valid credentials
        mvc.perform( formLogin( "/login" ).user( "username", "customer" ).password( "password", "pass" ) );

        mvc.perform( get( "/api/v1/users/username" ).principal( authentication ) ).andExpect( status().isOk() )
                .andExpect( status().isOk() );
    }

}
