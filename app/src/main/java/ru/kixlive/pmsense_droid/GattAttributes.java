package ru.kixlive.pmsense_droid;

import java.util.HashMap;

public class GattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static String PM01_MEASUREMENT = "cc4f2f18-7b99-4fae-b46d-3568a42cb3f8";
    public static String PM25_MEASUREMENT = "d7f6bea5-4e01-458d-9395-9e42ddd00b69";
    public static String PM10_MEASUREMENT = "4e79550a-da13-4cc3-905c-2c4426d305be";

    static {
        attributes.put(PM01_MEASUREMENT, "PM0.1 concentration");
        attributes.put(PM25_MEASUREMENT, "PM2.5 concentration");
        attributes.put(PM10_MEASUREMENT, "PM10 concentration");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);

        return name == null ? defaultName : name;
    }
}
