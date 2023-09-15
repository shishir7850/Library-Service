package com.berenberg.Library.dao;

import com.berenberg.Library.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    public User findByEmail(String email);
    public User findByUserId(String userId);

    public void deleteByEmail(String email);
}
