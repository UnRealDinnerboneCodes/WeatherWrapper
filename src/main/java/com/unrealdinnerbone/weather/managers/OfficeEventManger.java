package com.unrealdinnerbone.weather.managers;

import com.unrealdinnerbone.postgresslib.PostgresConsumer;
import com.unrealdinnerbone.unreallib.LogHelper;
import com.unrealdinnerbone.unreallib.TaskScheduler;
import com.unrealdinnerbone.weather.api.Office;
import com.unrealdinnerbone.weather.gov.api.GovApi;
import com.unrealdinnerbone.weather.gov.api.office.Headline;
import com.unrealdinnerbone.weather.wrapper.WeatherAPI;
import org.slf4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class OfficeEventManger {

    private static final Logger LOGGER = LogHelper.getLogger();

    private static final HashSet<Headline> headlines = new HashSet<>();
    public static void init() {
        TaskScheduler.scheduleRepeatingTask(12, TimeUnit.HOURS, task -> {
            HashSet<Headline> newHeadlines = new HashSet<>();
            List<PostgresConsumer> consumers = new ArrayList<>();
            String query = "insert into events.event (id, office, important, issuance_time, link, name, title, summary, content) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) on conflict do update";
            for (Office value : Office.REGISTRY) {
                for (Headline headline : GovApi.getHeadlines(value.name()).getNow().headlines()) {
                    consumers.add(preparedStatement -> {
                        preparedStatement.setString(1, headline.id());
                        preparedStatement.setString(2, value.name());
                        preparedStatement.setBoolean(3, headline.important());
                        preparedStatement.setTimestamp(4, Timestamp.from(headline.issuanceTime()));
                        preparedStatement.setString(5, headline.link());
                        preparedStatement.setString(6, headline.name());
                        preparedStatement.setString(7, headline.title());
                        preparedStatement.setString(8, headline.summary());

                        preparedStatement.setString(9, headline.content());
                    });
                }
                LOGGER.info("Found {} events for {}", consumers.size(), value.name());
            }
            LOGGER.info("Found {} events", consumers.size());
            WeatherAPI.POSTGRESS_HANDLER.executeBatchUpdate(query, consumers);
            try {
                ResultSet set = WeatherAPI.POSTGRESS_HANDLER.getSet("select * from events.event");
                while (set.next()) {
                    headlines.add(new Headline(set.getString("id"),
                            set.getString("office"),
                            set.getBoolean("important"),
                            set.getTimestamp("issuance_time").toInstant(),
                            set.getString("link"),
                            set.getString("name"),
                            set.getString("title"),
                            set.getString("summary"),
                            set.getString("content")));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            LOGGER.info("Finished adding events");
            headlines.clear();
            headlines.addAll(newHeadlines);
        });

    }

    public static HashSet<Headline> getHeadlines() {
        return headlines;
    }
}
