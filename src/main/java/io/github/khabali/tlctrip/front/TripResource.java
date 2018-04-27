package io.github.khabali.tlctrip.front;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.spi.JsonProvider;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Path("trip")
@ApplicationScoped
@Produces(APPLICATION_JSON)
public class TripResource {

    private static final Integer MAX_RESULT = 10000;

    private final static List<JsonObject> trips = new ArrayList<>();

    private final JsonProvider jsonp = JsonProvider.provider();

    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    private void loadData() throws IOException, ParseException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(TripResource.class.getClassLoader()
                .getResourceAsStream("samples/yellow_tripdata_2017.csv")))) {
            String[] headers = br.readLine()
                    .split(",");
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }
                JsonObjectBuilder ob = jsonp.createObjectBuilder();
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

    @GET
    public List<JsonObject> query(
            @QueryParam("month") final Integer month,
            @QueryParam("passenger_count") final Integer passengerCount,
            @QueryParam("trip_distance") final Double distance,
            @QueryParam("max_records") final Integer maxRecords) {
        if (trips.isEmpty()) {
            try {
                loadData();
            } catch (IOException | ParseException e) {
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                        .entity("Can't load data samples\n" + e.getMessage())
                        .build());
            }
        }

        if (month == null || month > 12 || month < 1) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("month value is not valid '" + month + "'")
                    .build());
        }

        Integer maxResult = maxRecords;
        if (maxResult == null || maxResult > MAX_RESULT) {
            maxResult = MAX_RESULT;
        }

        return trips.stream()
                .filter(t -> t.getInt("tpep_pickup_datetime_month") == month)
                .filter(t -> passengerCount == null || t.getInt("passenger_count") == passengerCount)
                .filter(t -> distance == null || t.getJsonNumber("trip_distance").doubleValue() <= distance)
                .limit(maxResult)
                .collect(Collectors.toList());
    }

}
