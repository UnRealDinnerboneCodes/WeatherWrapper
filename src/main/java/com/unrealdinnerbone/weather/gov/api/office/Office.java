package com.unrealdinnerbone.weather.gov.api.office;

import java.util.List;

public record Office(String id,
                     String name,
                     Address address,
                     String telephone,
                     String faxNumber,
                     String email,
                     String sameAs,
                     String newRegion,
                     String parentOrganization,
                     List<String> responsibleCounties,
                     List<String> responsibleForecastZones,
                     List<String> responsibleFireZones,
                     List<String> approvedObservationStations) {}
