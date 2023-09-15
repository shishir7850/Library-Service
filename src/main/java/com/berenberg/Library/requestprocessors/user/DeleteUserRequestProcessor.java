package com.berenberg.Library.requestprocessors.user;

import com.berenberg.Library.dao.UserDao;
import com.berenberg.Library.exception.InvalidInputException;
import com.berenberg.Library.model.User;
import com.berenberg.Library.model.requests.User.DeleteUserRequest;
import com.berenberg.Library.requestprocessors.AbstractRequestProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeleteUserRequestProcessor extends AbstractRequestProcessor<DeleteUserRequest, String> {
    private static Logger logger = LoggerFactory.getLogger(DeleteUserRequestProcessor.class);

    @Autowired
    private UserDao userDao;

    @Override
    public void validateInput(DeleteUserRequest request) {
        User user = userDao.findUserByEmail(request.getEmail());
        if (user == null) {
            logger.info("User does not exist");
            throw new InvalidInputException("User does not exist");
        }
    }
    @Override
    public String processRequest(DeleteUserRequest request) {
        logger.info("Removing user with email: " + request.getEmail());
        userDao.removeUser(request);
        return "User Deleted Successfully";
    }
}
