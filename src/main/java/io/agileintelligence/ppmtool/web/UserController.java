package io.agileintelligence.ppmtool.web;

import io.agileintelligence.ppmtool.domain.User;
import io.agileintelligence.ppmtool.services.MapValidationErrorService;
import io.agileintelligence.ppmtool.services.UserService;
import io.agileintelligence.ppmtool.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final MapValidationErrorService mapValidationErrorService;
    private final UserValidator userValidator;
    private final UserService userService;

    @Autowired
    public UserController(MapValidationErrorService mapValidationErrorService,
                          UserValidator userValidator,
                          UserService userService) {
        this.mapValidationErrorService = mapValidationErrorService;
        this.userValidator = userValidator;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user, BindingResult result) {
        userValidator.validate(user, result);
        ResponseEntity<?> errorMap = mapValidationErrorService.mapValidationService(result);
        if(errorMap != null) return errorMap;
        final User newUser = userService.saveUser(user);
        newUser.setPassword("");
        newUser.setConfirmPassword("");
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }
}
