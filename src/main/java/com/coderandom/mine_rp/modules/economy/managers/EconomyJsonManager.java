package com.coderandom.mine_rp.modules.economy.managers;

import com.coderandom.mine_rp.managers.JsonFileManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import static com.coderandom.mine_rp.MineRP.MINE_RP;

public class EconomyJsonManager implements EconomyManager {
    private static EconomyManager instance;
    private final JsonFileManager jsonFileManager;
    private final HashMap<UUID, Double> balanceCache;

    EconomyJsonManager() {
        this.jsonFileManager = new JsonFileManager("DATA", "wallets");
        this.balanceCache = new HashMap<>();
    }

    @Override
    public double getBalance(UUID uuid) {
        if (balanceCache.containsKey(uuid)) {
            return balanceCache.get(uuid);
        }
        return loadBalance(uuid);
    }

    @Override
    public void setBalance(UUID uuid, double balance) {
        balanceCache.put(uuid, balance);
    }

    @Override
    public void updateBalance(UUID uuid, double amount) {
        double newBalance = getBalance(uuid) + amount;
        setBalance(uuid, newBalance);
    }

    @Override
    public double loadBalance(UUID uuid) {
        CompletableFuture<JsonElement> future = jsonFileManager.getAsync();
        try {
            JsonElement jsonElement = future.get();
            if (jsonElement != null && jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                if (jsonObject.has(uuid.toString())) {
                    double balance = jsonObject.get(uuid.toString()).getAsDouble();
                    balanceCache.put(uuid, balance);
                    return balance;
                }
            }
        } catch (Exception e) {
            MINE_RP.getLogger().log(Level.SEVERE, "Could not load balance for player: " + uuid, e);
        }
        balanceCache.put(uuid, 0.0); // Default balance if not found
        return 0.0;
    }

    @Override
    public void saveBalance(UUID uuid) {
        double balance = getBalance(uuid);
        jsonFileManager.getAsync().thenAccept(jsonElement -> {
            JsonObject jsonObject;
            if (jsonElement != null && jsonElement.isJsonObject()) {
                jsonObject = jsonElement.getAsJsonObject();
            } else {
                jsonObject = new JsonObject();
            }

            jsonObject.addProperty(uuid.toString(), balance);
            jsonFileManager.setAsync(jsonObject).exceptionally(throwable -> {
                MINE_RP.getLogger().log(Level.SEVERE, "Could not save balance for player: " + uuid, throwable);
                return null;
            });
            balanceCache.remove(uuid);
        });
    }

    @Override
    public void saveAllBalances() {
        jsonFileManager.getAsync().thenAccept(jsonElement -> {
            JsonObject jsonObject;
            if (jsonElement != null && jsonElement.isJsonObject()) {
                jsonObject = jsonElement.getAsJsonObject();
            } else {
                jsonObject = new JsonObject();
            }

            for (UUID uuid : balanceCache.keySet()) {
                jsonObject.addProperty(uuid.toString(), getBalance(uuid));
            }

            jsonFileManager.setAsync(jsonObject).exceptionally(throwable -> {
                MINE_RP.getLogger().log(Level.SEVERE, "Could not save all balances!", throwable);
                return null;
            });

            balanceCache.clear();
        });
    }

    @Override
    public boolean hasAccount(UUID uuid) {
        CompletableFuture<JsonElement> future = jsonFileManager.getAsync();
        try {
            JsonElement jsonElement = future.get();
            if (jsonElement != null && jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                return jsonObject.has(uuid.toString());
            }
        } catch (Exception e) {
            MINE_RP.getLogger().log(Level.SEVERE, "Could not check if account exists for player: " + uuid, e);
        }
        return false;
    }
}
