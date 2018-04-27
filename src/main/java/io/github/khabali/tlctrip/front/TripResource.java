package io.github.khabali.tlctrip.front;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import io.github.khabali.tlctrip.configuration.Data;

@Path("trip")
@ApplicationScoped
@Produces(APPLICATION_JSON)
public class TripResource {

    private static final Integer MAX_RESULT = 10000;

    @GET
    public List<JsonObject> query(
            @QueryParam("month") final Integer month,
            @QueryParam("passenger_count") final Integer passengerCount,
            @QueryParam("trip_distance") final Double distance,
            @QueryParam("max_records") final Integer maxRecords) {

        if (month == null || month > 12 || month < 1) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("month value is not valid '" + month + "'")
                    .build());
        }

        Integer maxResult = maxRecords;
        if (maxResult == null || maxResult > MAX_RESULT) {
            maxResult = MAX_RESULT;
        }

        return Data.get().stream()
                .filter(t -> t.getInt("tpep_pickup_datetime_month") == month)
                .filter(t -> passengerCount == null || t.getInt("passenger_count") == passengerCount)
                .filter(t -> distance == null || t.getJsonNumber("trip_distance").doubleValue() <= distance)
                .limit(maxResult)
                .collect(Collectors.toList());
    }

}
