package com.berenberg.Library.requestprocessors.user;

import com.berenberg.Library.dao.UserDao;
import com.berenberg.Library.exception.InvalidInputException;
import com.berenberg.Library.model.User;
import com.berenberg.Library.model.requests.User.CreateUserRequest;
import com.berenberg.Library.requestprocessors.AbstractRequestProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.apache.commons.validator.routines.EmailValidator;

@Component
public class CreateUserRequestProcessor extends AbstractRequestProcessor<CreateUserRequest, User> {
    private static Logger logger = LoggerFactory.getLogger(CreateUserRequestProcessor.class);
    @Autowired
    private UserDao userDao;

    @Override
    public void validateInput(CreateUserRequest request) {
        EmailValidator emailValidator = EmailValidator.getInstance();
        if (request.getName().isEmpty() || !emailValidator.isValid(request.getEmail())) {
            logger.info("Invalid Name or Email");
            throw new InvalidInputException("Invalid name");
        }
        User user = userDao.findUserByEmail(request.getEmail());
        if (user != null) {
            logger.info("User already exists with email: " + request.getEmail());
            throw new InvalidInputException("User exists");
        }
    }
    @Override
    public User processRequest(CreateUserRequest request) {
        logger.info("Creating user email: " + request.getEmail());
        return userDao.createNewUser(request);
    }
}
