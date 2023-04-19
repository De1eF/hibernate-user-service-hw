package mate.academy.service.impl;

import mate.academy.exception.AuthenticationException;
import mate.academy.exception.RegistrationException;
import mate.academy.lib.Inject;
import mate.academy.lib.Service;
import mate.academy.model.User;
import mate.academy.service.AuthenticationService;
import mate.academy.service.UserService;
import mate.academy.util.SaltUtil;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    @Inject
    private UserService userService;

    @Override
    public User login(String email, String password) throws AuthenticationException {
        AuthenticationException exception =
                new AuthenticationException("Login or password is incorrect");
        User fondUser = userService.findByEmail(email).orElseThrow(() -> exception);
        if (fondUser.getPassword().equals(SaltUtil.getSalt(password, fondUser.getSalt()))) {
            return fondUser;
        }
        throw exception;
    }

    @Override
    public User register(String email, String password) throws RegistrationException {
        if (userService.findByEmail(email).isPresent()) {
            throw new RegistrationException("User with this email already exists");
        }
        byte[] salt = new byte[2];
        salt[0] = 6;
        salt[1] = 12;
        User toAdd = new User(
                email,
                SaltUtil.getSalt(password,salt),
                salt
        );
        userService.add(toAdd);
        return toAdd;
    }
}