package io.github.khabali.tlctrip.configuration;

import static java.util.Collections.emptyMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.spi.JsonProvider;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class Data implements Feature {

    private final static List<JsonObject> trips = new ArrayList<>();

    @Override
    public boolean configure(final FeatureContext context) {
        try {
            loadData();
            if (trips.isEmpty()) {
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                        .entity("Error : data was not loaded")
                        .build());
            }
            log.info("Data sample loaded successfully. " + trips.size() + " trips");
            return true;
        } catch (IOException | ParseException e) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Can't load data samples\n" + e.getMessage())
                    .build());
        }
    }

    private void loadData() throws IOException, ParseException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader()
                .getResourceAsStream("samples/yellow_tripdata_2017.csv")))) {
            String[] headers = br.readLine().split(",");
            JsonBuilderFactory jsonBuilderFactory = JsonProvider.provider().createBuilderFactory(emptyMap());
            final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }
                JsonObjectBuilder ob = jsonBuilderFactory.createObjectBuilder();
                String[] values = line.split(",");
                for (int i = 0; i < values.length; i++) {
                    if (i == 1 || i == 2) { //date
                        final LocalDate localDate = df.parse(values[i]).toInstant()
                                .atZone(ZoneId.systemDefault()).toLocalDate();
                        ob.add(headers[i] + "_day", localDate.getDayOfMonth());
                        ob.add(headers[i] + "_month", localDate.getMonthValue());
                        ob.add(headers[i] + "_year", localDate.getYear());
                        ob.add(headers[i], values[i]);
                    } else if (i == 3 || i == 4) { // number
                        ob.add(headers[i], Double.parseDouble(values[i]));
                    } else {
                        ob.add(headers[i], values[i]);
                    }

                }
                trips.add(ob.build());
            }
        }
    }

    public static List<JsonObject> get() {
        return trips;
    }
}
