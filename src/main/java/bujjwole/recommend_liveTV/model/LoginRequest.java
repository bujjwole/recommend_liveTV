package bujjwole.recommend_liveTV.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginRequest {

    private final String userId;
    private final String password;

    @JsonCreator
    public LoginRequest(@JsonProperty("user_id")String userId, @JsonProperty("password")String password) {
        this.userId = userId;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

}
