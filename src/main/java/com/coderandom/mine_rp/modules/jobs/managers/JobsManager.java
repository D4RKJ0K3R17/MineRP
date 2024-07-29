package com.coderandom.mine_rp.modules.jobs.managers;

import com.coderandom.mine_rp.managers.JsonFileManager;
import com.coderandom.mine_rp.modules.jobs.data.JobData;
import com.coderandom.mine_rp.modules.jobs.listeners.OnPlayerJoinAssignJob;
import com.coderandom.mine_rp.modules.jobs.listeners.OnPlayerQuitRemoveJob;
import com.google.gson.JsonObject;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import static com.coderandom.mine_rp.MineRP.CONFIG;
import static com.coderandom.mine_rp.MineRP.MINE_RP;

public class JobsManager {
    private static final Logger LOGGER = MINE_RP.getLogger();
    private final JsonFileManager fileManager;
    private final HashMap<String, JobData> jobs;
    private static volatile JobsManager instance;
    private final String defaultJob;

    private JobsManager() {
        this.fileManager = new JsonFileManager(null, "jobs");
        this.jobs = new HashMap<>();
        this.defaultJob = CONFIG.getString("default_job", "citizen");
        loadJobs();

        MINE_RP.getServer().getPluginManager().registerEvents(new OnPlayerJoinAssignJob(), MINE_RP);
        MINE_RP.getServer().getPluginManager().registerEvents(new OnPlayerQuitRemoveJob(), MINE_RP);
    }

    public static void initialize() {
        if (instance == null) {
            synchronized (JobsManager.class) {
                if (instance == null) {
                    instance = new JobsManager();
                }
            }
        }
    }

    public static JobsManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("JobsManager has not been initialized. Call initialize() first.");
        }
        return instance;
    }

    private void loadJobs() {
        fileManager.getAsync().thenAccept(jsonElement -> {
            if (jsonElement != null && jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                for (String key : jsonObject.keySet()) {
                    JsonObject jobObject = jsonObject.getAsJsonObject(key);
                    JobData job = JobData.fromJsonObject(key, jobObject);
                    jobs.put(key, job);
                    LOGGER.info("Loaded job: " + key);
                }
            } else {
                LOGGER.warning("Failed to load jobs: JSON element is null or not an object.");
            }
        }).exceptionally(throwable -> {
            LOGGER.severe("Exception occurred while loading jobs: " + throwable.getMessage());
            throwable.printStackTrace();
            return null;
        });
    }

    public JobData getJob(String key) {
        return jobs.get(key.toLowerCase());
    }

    public void addJob(JobData job) {
        jobs.put(job.getKey(), job);
        saveJobs();
    }

    private void saveJobs() {
        JsonObject jsonObject = new JsonObject();
        for (String key : jobs.keySet()) {
            JobData job = jobs.get(key);
            jsonObject.add(key, job.toJsonObject());
        }
        fileManager.setAsync(jsonObject).exceptionally(throwable -> {
            LOGGER.severe("Exception occurred while saving jobs: " + throwable.getMessage());
            throwable.printStackTrace();
            return null;
        });
    }

    public void deleteJob(String key) {
        jobs.remove(key);
        saveJobs();
    }

    public List<String> getJobKeys() {
        return new ArrayList<>(jobs.keySet());
    }

    public String getDefaultJob() {
        return defaultJob;
    }

    public boolean hasPermission(Player player, String jobKey) {
        JobData job = jobs.get(jobKey);
        return job != null && player.hasPermission(job.getPermission());
    }
}
