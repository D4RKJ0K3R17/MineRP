package com.coderandom.mine_rp.modules.jobs.data;

import com.coderandom.mine_rp.MineRP;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.logging.Logger;

public class JobData {
    private static final Logger LOGGER = MineRP.getInstance().getLogger();
    private String name;
    private String category;
    private double salary;
    private String[] permissions;
    private String key;
    private final String permission;

    // Constructor
    public JobData(String key, String name, String category, double salary, String[] permissions) {
        this.key = key.toLowerCase();
        this.name = name;
        this.category = category.toLowerCase();
        this.salary = salary;
        this.permissions = permissions;
        this.permission = "mine_rp.job." + this.key;
    }

    // Parsing methods
    public static JobData fromJsonObject(String key, JsonObject jobObject) {
        String name = getString(jobObject, "name");
        String category = getString(jobObject, "category").toLowerCase();
        double salary = getDouble(jobObject, "salary");
        String[] permissions = getStringArray(jobObject, "permissions");

        return new JobData(key, name, category, salary, permissions);
    }

    // Getters and setters
    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setKey(String key) {
        this.key = key.toLowerCase();
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String[] getPermissions() {
        return permissions;
    }

    public void setPermissions(String[] permissions) {
        this.permissions = permissions;
    }

    public void setCategory(String category) {
        this.category = category.toLowerCase();
    }

    public String getPermission() {
        return permission;
    }

    private static String getString(JsonObject jsonObject, String key) {
        JsonElement element = jsonObject.get(key);
        if (element != null && !element.isJsonNull()) {
            return element.getAsString();
        }
        LOGGER.warning("Missing or null property: " + key);
        return "";
    }

    private static double getDouble(JsonObject jsonObject, String key) {
        JsonElement element = jsonObject.get(key);
        if (element != null && !element.isJsonNull()) {
            return element.getAsDouble();
        }
        LOGGER.warning("Missing or null property: " + key);
        return 0.0;
    }

    private static String[] getStringArray(JsonObject jsonObject, String key) {
        JsonElement element = jsonObject.get(key);
        if (element != null && element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            String[] strings = new String[array.size()];
            for (int i = 0; i < array.size(); i++) {
                strings[i] = array.get(i).getAsString();
            }
            return strings;
        }
        LOGGER.warning("Missing or null property: " + key);
        return new String[0];
    }

    public JsonObject toJsonObject() {
        JsonObject jobObject = new JsonObject();
        jobObject.addProperty("key", this.key);
        jobObject.addProperty("name", this.name);
        jobObject.addProperty("category", this.category);
        jobObject.addProperty("salary", this.salary);

        JsonArray permissionsArray = new JsonArray();
        for (String permission : this.permissions) {
            permissionsArray.add(permission);
        }
        jobObject.add("permissions", permissionsArray);

        return jobObject;
    }
}
