package edu.ncsu.csc.CoffeeMaker.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.repositories.UserRepository;

/**
 * This class handles authentication for customer/staff login. It sets
 * restrictions on what pages customers can access and what pages staff can
 * access.
 *
 * @author Samarth Jadia (ssjadia), efhaske, sturner4
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /** Local instance of UserService with injected userServiced */
    @Autowired
    private UserDetailsService userService;

    /** Local instance of userRepository which is used for testing purposes */
    @Autowired
    UserRepository             ur;

    /**
     * Local instance of password encoder with bean injected from rest of
     * program to make sure encryption is symmetric
     */
    @Autowired
    PasswordEncoder            passwordEncoder;

    /**
     * Configures security for HTTP requests.
     *
     * @param http
     *            the HttpSecurity object to configure
     * @throws Exception
     *             if an error occurs during configuration
     */
    @Override
    protected void configure ( final HttpSecurity http ) throws Exception {
        http.cors().disable().csrf().disable().authorizeRequests()
                .antMatchers( "/privacyPolicy.html", "/privacyPolicy", "/register", "/register.html", "/", "/login",
                        "/api/v1/register" )
                .permitAll().antMatchers( "/customer/**", "/customer.html" ).hasRole( "CUSTOMER" )
                .antMatchers( "/staff", "/staff.html", "/inventory", "/inventory.html", "/editrecipe",
                        "editrecipe.html", "/deleterecipe", "/deleterecipe.html", "/recipe", "/recipe.html",
                        "/ingredient", "/ingredient.html" )
                .hasRole( "STAFF" ).anyRequest().authenticated()

                .and().formLogin().loginPage( "/login" )

                .successHandler( authenticationSuccessHandler() ).permitAll().and().logout().permitAll();
    }

    /**
     * Configures the AuthenticationManagerBuilder to use a custom
     * UserDetailsService with user details loaded from the database
     *
     * @param auth
     *            the AuthenticationManagerBuilder object to configure
     * @throws Exception
     *             if an error occurs during configuration
     */

    @Override
    protected void configure ( final AuthenticationManagerBuilder auth ) throws Exception {
        // Create temp staff user for testing
        final User user = new User( "staff", passwordEncoder.encode( "pass" ), true );
        if ( ur.findByUsername( user.getUsername() ) == null ) {
            ur.save( user );
        }
        auth.userDetailsService( userService ).passwordEncoder( passwordEncoder() );
    }

    /**
     * Creates a bean for password encoder.
     *
     * @return instance of PasswordEncoder implementation, specifically
     *         BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder () {
        return new BCryptPasswordEncoder();
    }

    /**
     * Creates a custom authentication success handler bean. This handler
     * defines the behavior to be executed for successful authentication.
     *
     * @return instance of AuthenticationSuccessHandler, specifically
     *         CustomAuthenticationSuccessHandler for customized handling of
     *         authentication success events.
     */
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler () {
        return new CustomAuthenticationSuccessHandler();
    }

    /**
     * Custom implementation of AuthenticationSuccessHandler. This class defines
     * behavior to be executed upon successful authentication.
     *
     * @author ssjadia, sturner4, efhaske
     */
    public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
        /**
         * Redirects users based on their roles after successful authentication.
         *
         * [Generative AI assistance was used to help implement this method]
         *
         * @param request
         *            the HTTP request
         * @param response
         *            the HTTP response
         * @param authentication
         *            authentication object containing user details
         * @throws IOException
         *             if I/O error occurs
         * @throws ServletException
         *             if servlet error occurs
         */
        @Override
        public void onAuthenticationSuccess ( final HttpServletRequest request, final HttpServletResponse response,
                final Authentication authentication ) throws IOException, ServletException {

            // Iterate over authorities to determine user's role
            for ( final GrantedAuthority authority : authentication.getAuthorities() ) {
                System.out.println( "User authority: " + authority.getAuthority() );
                // Redirect user to customer page if role is CUSTOMER
                if ( authority.getAuthority().equals( "ROLE_CUSTOMER" ) ) {
                    System.out.println( "Redirecting customer..." );
                    response.sendRedirect( "/customer" );
                    return;
                }
                // Redirect user to staff page if role is STAFF
                else if ( authority.getAuthority().equals( "ROLE_STAFF" ) ) {
                    System.out.println( "Redirecting staff..." );
                    response.sendRedirect( "/staff" );
                    return;
                }
            }
            // Return user to login page if role was not found
            System.out.println( "Role not found, redirecting to /" );
            response.sendRedirect( "/" );
        }
    }
}
