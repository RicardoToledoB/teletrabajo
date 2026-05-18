package com.teletrabajo.security.seeder;

import com.teletrabajo.entity.RoleEntity;
import com.teletrabajo.entity.UserEntity;
import com.teletrabajo.entity.UserRoleEntity;
import com.teletrabajo.repository.RoleRepository;
import com.teletrabajo.repository.UserRepository;
import com.teletrabajo.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.data-seeder.enabled", havingValue = "true")
public class DataSeeder implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    public void run(String... args) {
        RoleEntity admin = findOrCreateRole("ADMIN");
        RoleEntity administrativo = findOrCreateRole("ADMINISTRATIVO");
        RoleEntity supervisor = findOrCreateRole("SUPERVISOR");
        RoleEntity jefatura = findOrCreateRole("JEFATURA");

        UserEntity u1 = findOrCreateUser("admin@demo.com", "admin", "Admin123$", "Admin");
        UserEntity u2 = findOrCreateUser("operador@demo.com", "operador", "Operador123$", "Operador");
        UserEntity u3 = findOrCreateUser("supervisor@demo.com", "supervisor", "Supervisor123$", "Supervisor");
        UserEntity u4 = findOrCreateUser("jefatura@demo.com", "jefatura", "Jefatura123$", "Jefatura");

        assignRoleIfMissing(u1, admin);
        assignRoleIfMissing(u2, administrativo);
        assignRoleIfMissing(u3, supervisor);
        assignRoleIfMissing(u4, jefatura);
    }

    private RoleEntity findOrCreateRole(String name) {
        return roleRepository.findAllIncludingDeleted().stream()
                .filter(role -> name.equalsIgnoreCase(role.getName()))
                .findFirst()
                .orElseGet(() -> roleRepository.save(RoleEntity.builder().name(name).build()));
    }

    private UserEntity findOrCreateUser(String email, String username, String rawPassword, String firstName) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseGet(() -> userRepository.save(UserEntity.builder()
                        .username(username)
                        .password(passwordEncoder.encode(rawPassword))
                        .email(email)
                        .firstName(firstName)
                        .full_name(firstName)
                        .build()));
    }

    private void assignRoleIfMissing(UserEntity user, RoleEntity role) {
        userRoleRepository.findByUserIdAndRoleId(user.getId(), role.getId())
                .orElseGet(() -> userRoleRepository.save(UserRoleEntity.builder()
                        .user(user)
                        .role(role)
                        .build()));
    }
}
