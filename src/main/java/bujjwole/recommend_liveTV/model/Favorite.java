package bujjwole.recommend_liveTV.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;

public class Favorite {

    private final Item favoriteItem;

    @JsonCreator
    public Favorite(@JsonProperty("favorite")Item favoriteItem) {
        this.favoriteItem = favoriteItem;
    }

    public Item getFavoriteItem() {
        return favoriteItem;
    }

}
