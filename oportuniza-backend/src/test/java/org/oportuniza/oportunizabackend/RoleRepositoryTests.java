package org.oportuniza.oportunizabackend;

import org.junit.jupiter.api.Test;
import org.oportuniza.oportunizabackend.users.model.User;
import org.oportuniza.oportunizabackend.users.repository.RoleRepository;
import org.oportuniza.oportunizabackend.users.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class RoleRepositoryTests {
    @Autowired
    private RoleRepository repo;

    @Test
    public void testCreateRoles() {
        Role admin = new Role("ROLE_ADMIN");
        Role user = new Role("ROLE_USER");
        repo.saveAll(List.of(admin, user));
        long count = repo.count();
        assertEquals(2, count);
    }
}