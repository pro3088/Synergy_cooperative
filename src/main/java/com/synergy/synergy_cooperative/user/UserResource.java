package com.synergy.synergy_cooperative.user;

import com.synergy.synergy_cooperative.authorization.pojo.AuthRequest;
import com.synergy.synergy_cooperative.authorization.utils.CookiesUtil;
import com.synergy.synergy_cooperative.authorization.JwtService;
import com.synergy.synergy_cooperative.dto.UserInfo;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


@RestController
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserResource {

    @Autowired
    UserService userService;

    @Autowired
    JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Value("${authentication.auth.accessTokenCookieName}")
    private String accessTokenCookieName;

    @Value("${authentication.auth.refreshTokenCookieName}")
    private String refreshTokenCookieName;

    protected static Logger log = LoggerFactory.getLogger(UserResource.class);

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/enums")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<List<UserStatus>> getAllEnums(){ return ResponseEntity.ok(userService.getAllEnums());}

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<UserDTO> getUser(@PathVariable(name = "userId") final String id) {
        return ResponseEntity.ok(userService.get(id));
    }

    @GetMapping("/{status}/count")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserInfo> getCountByStatus(@PathVariable(name = "status") final String status) {
        return ResponseEntity.ok(userService.getCountByStatus(status));
    }

    @PostMapping("/signup")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<?> createUser(@RequestBody @Valid final UserDTO userDTO, HttpServletResponse response) {
        log.info("Request to create a new user");
        if (userDTO.getFirstName() == null || userDTO.getLastName() == null
                || userDTO.getPassword() == null || userDTO.getEmailAddress() == null
                || userDTO.getReferralCode() == null) {
            return new ResponseEntity<>("Field(s) is null", HttpStatus.BAD_REQUEST);
        }

        UserDTO user = null;
        try {
            user = userService.create(userDTO);

            String token = jwtService.generateToken(user.getEmailAddress());
            // Set HTTP-only cookie in the response
            response.addHeader(HttpHeaders.SET_COOKIE, new CookiesUtil(accessTokenCookieName,token).getCookie());

            if (userDTO.getRememberMe()){
                String refreshToken = jwtService.generateRefreshToken(user.getEmailAddress());
                response.addHeader(HttpHeaders.SET_COOKIE, new CookiesUtil( refreshTokenCookieName,refreshToken).getCookie());
            }

            log.info("JWT Token has been created");
        } catch (Exception e) {
            log.error("Error creating user with error: {}",e.getMessage());
            String errorMessage = (e.getMessage() != null) ? e.getMessage() : "Confirm details";
            return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/mail/reset/{email}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<?> sendForgotPassMail(@PathVariable(name = "email") final String email) throws MessagingException, UnsupportedEncodingException {
        userService.sendEmail(email);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<?> updateUser(@PathVariable(name = "id") final String id,
            @RequestBody @Valid final UserDTO userDTO) {
        UserDTO update;
        try {
            update = userService.update(id, userDTO);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(update);
    }

    @PostMapping("/login")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<?> validateUser(@RequestBody @Valid final AuthRequest authRequest, HttpServletResponse response){
        log.info("Request to login");
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
            if (authentication.isAuthenticated()) {
                UserDTO user = userService.validateUser(authRequest);
                if (user != null) {
                    String token = jwtService.generateToken(user.getEmailAddress());
                    // Set HTTP-only cookie in the response
                    response.addHeader(HttpHeaders.SET_COOKIE, new CookiesUtil(accessTokenCookieName, token).getCookie());

                    if (authRequest.isRememberMe()) {
                        String refreshToken = jwtService.generateRefreshToken(user.getEmailAddress());
                        response.addHeader(HttpHeaders.SET_COOKIE, new CookiesUtil(refreshTokenCookieName, refreshToken).getCookie());
                    }

                    log.info("JWT Token has been generated");
                    return new ResponseEntity<>(user, HttpStatus.ACCEPTED);
                }
            }
        } catch (AuthenticationException e) {
            log.error("UserName or Password is incorrect with error: {}", e.getMessage());
            return new ResponseEntity<>("UserName or Password is incorrect", HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>("Bad Request", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Void> deleteUser(@PathVariable(name = "id") final String id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/logout")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        log.info("logging out user from system");
        SecurityContextHolder.clearContext();
        response.addHeader(HttpHeaders.SET_COOKIE, new CookiesUtil( refreshTokenCookieName,"").clear());
        response.addHeader(HttpHeaders.SET_COOKIE, new CookiesUtil( accessTokenCookieName,"").clear());
        return ResponseEntity.ok("Logout successful");
    }
}
