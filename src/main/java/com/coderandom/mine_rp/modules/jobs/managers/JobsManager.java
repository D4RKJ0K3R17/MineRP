package com.coderandom.mine_rp.modules.jobs.managers;

import com.coderandom.mine_rp.modules.jobs.data.JobData;
import com.coderandom.mine_rp.managers.JsonFileManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.logging.Logger;

public class JobsManager {
    private final JsonFileManager fileManager;
    private final HashMap<String, JobData> jobs;
    private static final Logger LOGGER = Logger.getLogger(JobsManager.class.getName());

    public JobsManager() {
        this.fileManager = new JsonFileManager(null, "jobs");
        this.jobs = new HashMap<>();
        loadJobs();
    }

    private void loadJobs() {
        fileManager.getAsync().thenAccept(jsonElement -> {
            if (jsonElement != null && jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                for (String key : jsonObject.keySet()) {
                    JsonObject jobObject = jsonObject.getAsJsonObject(key);
                    JobData job = JobData.fromJsonObject(jobObject);
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

    public JobData getJob(String name) {
        return jobs.get(name);
    }

    public void addJob(String name, JobData job) {
        jobs.put(name, job);
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

    public void deleteJob(String name) {
        jobs.remove(name);
        saveJobs();
    }
}
