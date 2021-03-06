package com.example.islamicinfoapp.src.main.java.com.utilities;

import com.example.islamicinfoapp.src.main.java.com.model.PrayerTiming;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class PrayerTimeDeserializer implements JsonDeserializer {
    @Override
    public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject data = json.getAsJsonObject().get("data").getAsJsonObject();
        JsonObject timings = data.get("timings").getAsJsonObject();
        String fajr = timings.get("Fajr").getAsString();
        String sunrise = timings.get("Sunrise").getAsString();
        String dhuhr = timings.get("Dhuhr").getAsString();
        String asr = timings.get("Asr").getAsString();
        String sunset = timings.get("Sunset").getAsString();
        String maghrib = timings.get("Maghrib").getAsString();
        String isha = timings.get("Isha").getAsString();
        String imsak = timings.get("Imsak").getAsString();
        String date = data.get("date").getAsJsonObject().get("readable").getAsString();
        JsonObject hijri = data.get("date").getAsJsonObject().get("hijri").getAsJsonObject();
        String hijridate = hijri.get("date").getAsString();
        String hijriday = hijri.get("day").getAsString();
        int hijrimonthnumber = hijri.get("month").getAsJsonObject().get("number").getAsInt();
        String hijrimonthname = hijri.get("month").getAsJsonObject().get("en").getAsString();
        String hijriyear = hijri.get("year").getAsString();
        PrayerTiming prayerTiming = new PrayerTiming(fajr,sunrise,dhuhr,asr,sunset,maghrib,isha,imsak,
                date,hijridate,hijriday,hijrimonthnumber,hijrimonthname,hijriyear);

        return prayerTiming;
    }
}
