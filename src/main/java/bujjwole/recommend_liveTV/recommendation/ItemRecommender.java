package bujjwole.recommend_liveTV.recommendation;

import bujjwole.recommend_liveTV.database.MySQLConnection;
import bujjwole.recommend_liveTV.exception.MySQLException;
import bujjwole.recommend_liveTV.exception.RecommendationException;
import bujjwole.recommend_liveTV.exception.TwitchException;
import bujjwole.recommend_liveTV.external.TwitchUser;
import bujjwole.recommend_liveTV.model.Game;
import bujjwole.recommend_liveTV.model.Item;
import bujjwole.recommend_liveTV.model.ItemType;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ItemRecommender {
    private static final int DEFAULT_GAME_LIMIT = 3;
    private static final int DEFAULT_PER_GAME_RECOMMENDATION_LIMIT = 10;
    private static final int DEFAULT_TOTAL_RECOMMENDATION_LIMIT = 20;

    public Map<String, List<Item>> recommendItemsByDefault() throws RecommendationException {
        Map<String, List<Item>> recommendedItemMap = new HashMap<>();
        TwitchUser user = new TwitchUser();
        List<Game> topGames;

        try {
            topGames = user.getTopGames(DEFAULT_GAME_LIMIT);
        } catch (TwitchException e) {
            throw new RecommendationException("Failed to get game data for recommendation");
        }

        for (ItemType type: ItemType.values()) {
            recommendedItemMap.put(type.toString(), recommendByTopGames(type, topGames));
        }

        return recommendedItemMap;
    }

    public Map<String, List<Item>> recommendItemsByUser(String userId) throws RecommendationException {
        Map<String, List<Item>> recommendedItemMap = new HashMap<>();
        Set<String> favoriteItemIds;
        Map<String, List<String>> favoriteGameIds;
        MySQLConnection connection = null;

        try {
            connection = new MySQLConnection();
            favoriteItemIds = connection.getFavoriteItemIds(userId);
            favoriteGameIds = connection.getFavoriteGameIds(favoriteItemIds);
        } catch (MySQLException e) {
            throw new RecommendationException("Failed to get user favorite history for recommendation");
        } finally {
            connection.close();
        }

        for (Map.Entry<String, List<String>> entry: favoriteGameIds.entrySet()) {
            if (entry.getValue().size() == 0) {
                TwitchUser user = new TwitchUser();
                List<Game> topGames;
                try {
                    topGames = user.getTopGames(DEFAULT_GAME_LIMIT);
                } catch (TwitchException e) {
                    throw new RecommendationException("Failed to get game data for recommendation");
                }
                recommendedItemMap.put(entry.getKey(), recommendByTopGames(ItemType.valueOf(entry.getKey()), topGames));
            } else {
                recommendedItemMap.put(entry.getKey(), recommendByFavoriteHistory(favoriteItemIds, entry.getValue(), ItemType.valueOf(entry.getKey())));
            }
        }

        return recommendedItemMap;
    }


    private List<Item> recommendByTopGames(ItemType type, List<Game> topGames) throws RecommendationException {
        List<Item> recommendedItems = new ArrayList<>();
        TwitchUser user = new TwitchUser();

        loop:
        for (Game game: topGames) {
            List<Item> items;

            try {
                items = user.searchByType(game.getId(), type, DEFAULT_PER_GAME_RECOMMENDATION_LIMIT);
            } catch (TwitchException e) {
                throw new RecommendationException("Failed to get recommended result");
            }

            for (Item item : items) {
                if (recommendedItems.size() == DEFAULT_TOTAL_RECOMMENDATION_LIMIT) {
                    break loop;
                }
                recommendedItems.add(item);
            }
        }

        return recommendedItems;
    }

    private List<Item> recommendByFavoriteHistory(Set<String> favoriteItemIds, List<String> favoriteGameIds, ItemType type) throws RecommendationException {
        Map<String, Long> favoriteGameIdByCount = favoriteGameIds.parallelStream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        List<Map.Entry<String, Long>> sortedFavoriteGameIdListByCount = new ArrayList<>(favoriteGameIdByCount.entrySet());
        sortedFavoriteGameIdListByCount.sort(
                (Map.Entry<String, Long> e1, Map.Entry<String, Long> e2) -> Long.compare(e2.getValue(), e1.getValue()));
        if (sortedFavoriteGameIdListByCount.size() > DEFAULT_GAME_LIMIT) {
            sortedFavoriteGameIdListByCount = sortedFavoriteGameIdListByCount.subList(0, DEFAULT_GAME_LIMIT);
        }

        List<Item> recommendedItems = new ArrayList<>();
        TwitchUser user = new TwitchUser();
        loop:
        for (Map.Entry<String, Long> favoriteGame: sortedFavoriteGameIdListByCount) {
            List<Item> items;
            try {
                items = user.searchByType(favoriteGame.getKey(), type, DEFAULT_PER_GAME_RECOMMENDATION_LIMIT);
            } catch (TwitchException e) {
                throw new RecommendationException("Failed to get recommended result");
            }

            for (Item item: items) {
                if (recommendedItems.size() == DEFAULT_TOTAL_RECOMMENDATION_LIMIT) {
                    break loop;
                }
                if (!favoriteItemIds.contains(item.getId())) {
                    recommendedItems.add(item);
                }
            }
        }

        return recommendedItems;
    }

}
