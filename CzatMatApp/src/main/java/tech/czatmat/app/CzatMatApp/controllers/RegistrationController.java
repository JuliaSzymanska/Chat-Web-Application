package tech.czatmat.app.CzatMatApp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tech.czatmat.app.CzatMatApp.dataClasses.authorities.AuthoritiesRepository;
import tech.czatmat.app.CzatMatApp.dataClasses.roles.Role;
import tech.czatmat.app.CzatMatApp.dataClasses.roles.RoleRepository;
import tech.czatmat.app.CzatMatApp.dataClasses.users.User;
import tech.czatmat.app.CzatMatApp.dataClasses.users.UserRepository;
import tech.czatmat.app.CzatMatApp.messages.Message;
import tech.czatmat.app.CzatMatApp.payload.request.RegistrationRequest;
import tech.czatmat.app.CzatMatApp.payload.response.MessageResponse;
import tech.czatmat.app.CzatMatApp.security.JwtUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Set;


@RestController
@RequestMapping("/registration")

// TODO: 27.11.2020 : https://www.codebyamir.com/blog/user-account-registration-with-spring-boot
public class RegistrationController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private AuthoritiesRepository authoritiesRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtUtils jwtUtils;

    public RegistrationController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // TODO: 27.11.2020 Nie jestem pewien implementacji
    // TODO: 27.11.2020 Sprawdzać czy udało się dodać usera

    // TODO: 28.11.2020 Obsługiwać brak kolumn i zwracac odpowiedni error
    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<?> createUser(@RequestBody RegistrationRequest request, HttpServletResponse response) {
        if (userRepository.existsByUsername(request.getUsername())) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            // TODO: 27.11.2020 Make throw exepction?
            return ResponseEntity.badRequest().body(new MessageResponse("User By That Login Already Exists"));
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }
        User user = new User(request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getName(),
                request.getSurname(),
                request.getEmail(),
                User.ENABLED
        );
        Set<String> strRoles = request.getRole();
        Set<Role> roles = new HashSet<>();


        if (strRoles == null) {
            Role userRole = authoritiesRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));


        request.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(request);
        response.setStatus(HttpServletResponse.SC_CREATED);
        return ("User successfully created");
    }

    // TODO: 27.11.2020 Ograniczyć możliwość używania zapytania
    @RequestMapping(value = "/get_users", method = RequestMethod.GET, produces = "application/json")
    public Iterable<User> getUsers() {
        return userRepository.findAll();
    }

    @RequestMapping(value = "/przyklad", method = RequestMethod.GET, produces = "application/json")
    public String getPrzyklad() {
        return "Siema Registration";
    }
}
