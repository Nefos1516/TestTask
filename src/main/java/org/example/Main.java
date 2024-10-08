package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;

public class Main {
    public static void main(String[] args){
        int priceSum = 0;
        ArrayList<Integer> prices = new ArrayList<>();
        HashMap<String, Duration> timeDiffForCarriers = new HashMap<>();
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
                    DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder().append(DateTimeFormatter.ofPattern("[dd.MM.yy HH:mm]" + "[dd.MM.yy H:mm]"));
                    DateTimeFormatter format = builder.toFormatter();
                    LocalDateTime departureTime = LocalDateTime.parse(ticket.get("departure_date").getAsString() + " " + ticket.get("departure_time").getAsString(), format);
                    LocalDateTime arrivalTime = LocalDateTime.parse(ticket.get("arrival_date").getAsString() + " " + ticket.get("arrival_time").getAsString(), format);
                    int price = Integer.parseInt(ticket.get("price").getAsString());
                    priceSum += price;
                    prices.add(price);
                    Duration timeDiff = Duration.between(departureTime, arrivalTime).plusHours(7);
                    if (!timeDiffForCarriers.containsKey(str))
                        timeDiffForCarriers.put(str, timeDiff);
                    else if (timeDiff.compareTo(timeDiffForCarriers.get(str)) < 0)
                        timeDiffForCarriers.replace(str, timeDiff);
                }
            }
            timeDiffForCarriers.forEach((k, v) ->
            {
                String s = String.format("Время перелета для оператора %s составляет %s", k, v.toHours() + " часов и " + v.toMinutes() % 60 + " минут.");
                System.out.println(s);
            });
            Collections.sort(prices);
            if (prices.size() % 2 == 0)
                System.out.println("Разница между медианой и средней ценой: " + Math.abs(priceSum / prices.size() - (prices.get(prices.size() / 2) + prices.get(prices.size() / 2 - 1))/ 2));
            else
                System.out.println("Разница между медианой и средней ценой: " + Math.abs(priceSum / prices.size() - prices.get(prices.size() / 2 + 1)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

