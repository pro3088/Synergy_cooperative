package com.synergy.synergy_cooperative.user;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RestController
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserResource {

    private final UserService userService;

    public UserResource(final UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/enums")
    public ResponseEntity<List<UserStatus>> getAllEnums(){ return ResponseEntity.ok(userService.getAllEnums());}

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable(name = "id") final String id) {
        return ResponseEntity.ok(userService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<User> createUser(@RequestBody @Valid final UserDTO userDTO) {
        if (userDTO.getFirstName() == null || userDTO.getLastName() == null
                || userDTO.getPassword() == null || userDTO.getEmailAddress() == null
                || userDTO.getReferralCode() == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        User user = userService.create(userDTO);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(@PathVariable(name = "id") final String id,
            @RequestBody @Valid final UserDTO userDTO) {
        userService.update(id, userDTO);
        return ResponseEntity.ok(id);
    }

    @PostMapping("/login")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<UserDTO> validateUser(@RequestBody @Valid final UserDTO userDTO){
        UserDTO user = userService.validateUser(userDTO);
        return new ResponseEntity<>(user, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteUser(@PathVariable(name = "id") final String id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
