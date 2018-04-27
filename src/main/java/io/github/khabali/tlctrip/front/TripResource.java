package io.github.khabali.tlctrip.front;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import io.github.khabali.tlctrip.model.Trip;

import java.util.Collections;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("trip")
@ApplicationScoped
@Produces(APPLICATION_JSON)
public class TripResource {


    @GET
    public List<Trip> query(
            @QueryParam("pickup_time") String pickupTime,
            @QueryParam("passenger_count") String passengerCount,
            @QueryParam("distance") Long distance,
            @QueryParam("max_records") Integer maxRecords) {

        return Collections.emptyList();
    }

}
