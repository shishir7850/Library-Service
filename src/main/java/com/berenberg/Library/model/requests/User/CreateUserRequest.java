package com.berenberg.Library.model.requests.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {
    String name;
    String email;
}
