package ru.shubin.flight;

import ru.shubin.flight.model.Flight;
import ru.shubin.flight.service.FilterFlightService;
import ru.shubin.flight.service.FilterFlightServiceImpl;
import ru.shubin.flight.utilits.FlightBuilder;

import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        FilterFlightService filterFlightService = new FilterFlightServiceImpl();
        List<Flight> flightListForForFiltering = FlightBuilder.createFlights();
        System.out.printf(
                "Исходный список с количеством полетов %d: %s %n",
                flightListForForFiltering.size(),
                flightListForForFiltering);

        // Фильтрация полетов, где имеются сегменты с датой вылета до текущего момента времени
        System.out.println();
        List<Flight> filtredFLightList1 = filterFlightService.filterFlights(
                Set.of("excludePastFlightsRule"),
                flightListForForFiltering
        );
        System.out.printf(
                "Cписок полетов c фильтрацией, где дата вылета до текущего момента времени с количеством полетов %d: %s %n",
                filtredFLightList1.size(),
                filtredFLightList1
        );

        // Фильтрация полетов, где имеются сегменты с датой прилёта раньше даты вылета
        System.out.println();
        List<Flight> filtredFLightList2 = filterFlightService.filterFlights(
                Set.of("excludeFlightsByArrivalBeforeDepartureRule"),
                flightListForForFiltering
        );
        System.out.printf(
                "Cписок полетов c фильтрацией по дате прилета-дате вылета с количеством полетов %d: %s %n",
                filtredFLightList2.size(),
                filtredFLightList2
        );

        // Фильтрация полетов, где имеются сегменты с общим временем, проведённым на земле превышает два часа
        System.out.println();
        List<Flight> filtredFLightList3 = filterFlightService.filterFlights(
                Set.of("excludeFlightsByEarthTime"),
                flightListForForFiltering
        );
        System.out.printf(
                "Cписок полетов c фильтрацией, где общее время, проведённое на земле, превышает два часа, с количеством полетов %d: %s %n",
                filtredFLightList3.size(),
                filtredFLightList3
        );
    }
}
