package bujjwole.recommend_liveTV.exception;

public class TwitchException extends RuntimeException{
    public TwitchException(String errorMessage){
        super(errorMessage);
    }
}