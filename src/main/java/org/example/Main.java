package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Main {
    public static void main(String[] args){
        int priceSum = 0;
        ArrayList<Integer> prices = new ArrayList<>();
        HashMap<String, Integer> timeDiffForCarriers = new HashMap<>();
        try (Reader reader = new FileReader(args[0], StandardCharsets.UTF_8)){
            JsonArray tickets = new Gson().fromJson(reader, JsonObject.class).getAsJsonArray("tickets");
            for (int i = 0; i < tickets.size(); i++)
            {
                JsonObject ticket = (JsonObject) tickets.get(i);
                String origin = ticket.get("origin").getAsString();
                String destination = ticket.get("destination").getAsString();
                if (origin.equals("VVO") && destination.equals("TLV"))
                {
                    String str = ticket.get("carrier").getAsString();
                    String[] arrivalTimes = ticket.get("arrival_time").getAsString().split(":");
                    String[] departureTimes = ticket.get("departure_time").getAsString().split(":");
                    int price = Integer.parseInt(ticket.get("price").getAsString());
                    priceSum += price;
                    prices.add(price);
                    int timeDiff = (Integer.parseInt(arrivalTimes[0]) - Integer.parseInt(departureTimes[0])) * 60 + (Integer.parseInt(arrivalTimes[1]) - Integer.parseInt(departureTimes[1]));
                    if (!timeDiffForCarriers.containsKey(str))
                        timeDiffForCarriers.put(str, timeDiff);
                    else if (timeDiffForCarriers.get(str) > timeDiff) {
                        timeDiffForCarriers.replace(str, timeDiff);
                    }
                }
            }
            timeDiffForCarriers.forEach((k, v) ->
            {
                String s = String.format("Время перелета для оператора %s составляет %s", k, String.valueOf(v / 60 + 7).concat(" часов и " + v % 60 +" минут."));
                System.out.println(s);
            });
            Collections.sort(prices);
            if (prices.size % 2 ==0)
                System.out.println("Разница между медианой и средней ценой: " + Math.abs(priceSum / prices.size() - (prices.get(prices.size() / 2) + prices.get(prices.size() / 2 - 1))/2));
            else
                System.out.println("Разница между медианой и средней ценой: " + Math.abs(priceSum / prices.size() - prices.get(prices.size() / 2)));
                
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

