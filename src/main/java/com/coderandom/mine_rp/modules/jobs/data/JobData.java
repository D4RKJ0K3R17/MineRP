package com.coderandom.mine_rp.modules.jobs.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.logging.Logger;

public class JobData {
    private String name;
    private String category;
    private double salary;
    private String[] permissions;
    private String[] commands;

    private static final Logger LOGGER = Logger.getLogger(JobData.class.getName());

    // Constructor
    public JobData(String name, String category, double salary, String[] permissions, String[] commands) {
        this.name = name;
        this.category = category;
        this.salary = salary;
        this.permissions = permissions;
        this.commands = commands;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public String[] getCommands() {
        return commands;
    }

    public void setCommands(String[] commands) {
        this.commands = commands;
    }

    // Parsing methods
    public static JobData fromJsonObject(JsonObject jobObject) {
        String name = getString(jobObject, "name");
        String category = getString(jobObject, "category");
        double salary = getDouble(jobObject, "salary");
        String[] permissions = getStringArray(jobObject, "permissions");
        String[] commands = getStringArray(jobObject, "commands");

        return new JobData(name, category, salary, permissions, commands);
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
        jobObject.addProperty("name", this.name);
        jobObject.addProperty("category", this.category);
        jobObject.addProperty("salary", this.salary);

        JsonArray permissionsArray = new JsonArray();
        for (String permission : this.permissions) {
            permissionsArray.add(permission);
        }
        jobObject.add("permissions", permissionsArray);

        JsonArray commandsArray = new JsonArray();
        for (String command : this.commands) {
            commandsArray.add(command);
        }
        jobObject.add("commands", commandsArray);

        return jobObject;
    }
}
