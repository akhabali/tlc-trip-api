package io.github.khabali.tlctrip.front;

import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import io.github.khabali.tlctrip.service.Data;

@Path("trip")
@ApplicationScoped
@Produces(APPLICATION_JSON)
public class TripResource {

    @Inject
    private Data data;

    @GET
    public List<JsonObject> query(
            @Context final HttpServletRequest request,
            @QueryParam("max_records") @DefaultValue("10000") final int maxRecords) {

        final List<Predicate<JsonObject>> filters = request.getParameterMap().entrySet().stream()
                .filter(e -> !"max_records".equals(e.getKey()))
                .map(entry -> {
                    if (entry.getKey().endsWith("_max")) {
                        final double max = entry.getValue().length > 0 ? Double.parseDouble(entry.getValue()[0]) : 0;
                        return (Predicate<JsonObject>) json -> json.getJsonNumber(
                                entry.getKey().substring(0, entry.getKey().length() - "_max".length())).doubleValue()
                                <= max;
                    }
                    if (entry.getKey().endsWith("_min")) {
                        final double min = entry.getValue().length > 0 ? Double.parseDouble(entry.getValue()[0]) : 0;
                        return (Predicate<JsonObject>) json -> json.getJsonNumber(
                                entry.getKey().substring(0, entry.getKey().length() - "_min".length())).doubleValue()
                                <= min;
                    }
                    if (entry.getValue().length == 0) {
                        return (Predicate<JsonObject>) j -> true;
                    }
                    return (Predicate<JsonObject>) json -> entry.getValue()[0].equals(json.getString(entry.getKey()));
                })
                .collect(toList());

        return data.getTrips().stream()
                .filter(t -> filters.stream().allMatch(p -> p.test(t)))
                .limit(maxRecords)
                .collect(Collectors.toList());
    }

}
