package edu.ncsu.csc.CoffeeMaker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.CoffeeMaker.models.User;

/**
 * Repository for user
 *
 * @author ssjadia, efhaske, sturner4
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Finds a User object with the provided username. Spring will generate code
     * to make this happen.
     *
     * @param username
     *            username of the user
     * @return Found user, null if none.
     */
    User findByUsername ( String username );
}
