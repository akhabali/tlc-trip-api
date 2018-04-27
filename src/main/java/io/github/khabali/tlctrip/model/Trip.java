package io.github.khabali.tlctrip.model;

import lombok.Data;

@Data
public class Trip {
    private final String VendorID;
    private final String tpepPickupDatetime;
    private final String tpepDropoffDatetime;
    private final String passengerCount;
    private final String tripDistance;
    private final String RatecodeID;
    private final String storeAndFwdFlag;
    private final String PULocationID;
    private final String DOLocationID;
    private final String paymentType;
    private final String fareAmount;
    private final String extra;
    private final String mtaTax;
    private final String tipAmount;
    private final String tollsAmount;
    private final String improvementSurcharge;
    private final String totalAmount;
}
