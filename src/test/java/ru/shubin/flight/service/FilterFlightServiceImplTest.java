package ru.shubin.flight.service;


import ru.shubin.flight.model.Flight;
import ru.shubin.flight.model.Segment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilterFlightServiceImplTest {
    FilterFlightService filterFlightService;
    @BeforeEach
    void setUp(){
        filterFlightService = new FilterFlightServiceImpl();
    }

    @Test
    @DisplayName("Проверка корректности фильтрации списка полетов при добавлении всех фильтров")
    void filterFlightsAllFiltresTest(){
        LocalDateTime currentTime = LocalDateTime.now();
        Segment segment1 = new Segment(currentTime.plusHours(5), currentTime.plusHours(10));
        Segment segment2 = new Segment(currentTime.plusHours(13), currentTime.plusHours(18));
        Flight validFlight1 = new Flight(List.of(segment1, segment2));

        Segment segment3 = new Segment(currentTime.minusHours(5), currentTime.plusHours(4));
        Flight departureBeforeCurrentFlight2 = new Flight(List.of(segment3));

        Segment segment4 = new Segment(currentTime.plusHours(5), currentTime.plusHours(4));
        Flight arrivalBeforeDepartureCurrentFlight3 = new Flight(List.of(segment4));

        Segment segment5 = new Segment(currentTime.plusHours(5), currentTime.plusHours(10));
        Segment segment6 = new Segment(currentTime.plusHours(11), currentTime.plusHours(18));
        Flight shortEarthTimeFlight4 = new Flight(List.of(segment5, segment6));

        Set<String> filterRules = Set.of("excludePastFlightsRule",
                "excludeFlightsByArrivalBeforeDepartureRule",
                "excludeFlightsByEarthTime");
        List<Flight> flightList = List.of(validFlight1, departureBeforeCurrentFlight2, arrivalBeforeDepartureCurrentFlight3, shortEarthTimeFlight4);
        List<Flight> filtredFlights = filterFlightService.filterFlights(filterRules, flightList);
        assertEquals(1, filtredFlights.size());
        assertTrue(filtredFlights.contains(validFlight1));
        assertFalse(filtredFlights.contains(departureBeforeCurrentFlight2));
        assertFalse(filtredFlights.contains(arrivalBeforeDepartureCurrentFlight3));
        assertFalse(filtredFlights.contains(shortEarthTimeFlight4));
    }

    @Test
    @DisplayName("Проверка корректности фильтрации полетов с сегментами, имеющими дату вылета до текущего момента времени")
    void filterPastFlightsTest(){
        LocalDateTime currentTime = LocalDateTime.now();

        Set<String> filterRules = Set.of("excludePastFlightsRule");
        Segment segment1 = new Segment(currentTime.plusHours(5), currentTime.plusHours(10));
        Flight ArrivalAfterCurrFlight = new Flight(List.of(segment1));

        Segment segment2 = new Segment(currentTime.minusHours(5), currentTime.plusHours(4));
        Flight ArrivalBeforeCurrFlight = new Flight(List.of(segment2));

        List<Flight> testFlightList = List.of(ArrivalAfterCurrFlight, ArrivalBeforeCurrFlight);

        List<Flight> filtredFlights = filterFlightService.filterFlights(filterRules, testFlightList);
        assertEquals(1, filtredFlights.size());
        assertTrue(filtredFlights.contains(ArrivalAfterCurrFlight));
        assertFalse(filtredFlights.contains(ArrivalBeforeCurrFlight));
    }

    @Test
    @DisplayName("Проверка корректности фильтрации полетов с сегментами, имеющими дату прилета до даты вылета")
    void filterFlightsByArrivalBeforeDepartureTest(){
        LocalDateTime currentTime = LocalDateTime.now();

        Set<String> filterRules = Set.of("excludeFlightsByArrivalBeforeDepartureRule");
        Segment segment1 = new Segment(currentTime.plusHours(12), currentTime.plusHours(10));
        Flight arrivalBeforeDepartureFlight = new Flight(List.of(segment1));

        Segment segment2 = new Segment(currentTime.plusHours(5), currentTime.plusHours(12));
        Flight arrivalAfterDepartureFlight = new Flight(List.of(segment2));

        List<Flight> testFlightList = List.of(arrivalAfterDepartureFlight, arrivalBeforeDepartureFlight);

        List<Flight> filtredFlights = filterFlightService.filterFlights(filterRules, testFlightList);
        assertEquals(1, filtredFlights.size());
        assertTrue(filtredFlights.contains(arrivalAfterDepartureFlight));
        assertFalse(filtredFlights.contains(arrivalBeforeDepartureFlight));
    }

    @Test
    @DisplayName("Проверка корректности фильтрации полетов по общему времени, проведённому на земле")
    void filterFlightsByEarthTimeTest(){
        LocalDateTime currentTime = LocalDateTime.now();

        Set<String> filterRules = Set.of("excludeFlightsByEarthTime");
        Segment segment1 = new Segment(currentTime.plusHours(5), currentTime.plusHours(10));
        Segment segment2 = new Segment(currentTime.plusHours(13), currentTime.plusHours(18));
        Flight longEarthTimeFlight1 = new Flight(List.of(segment1, segment2));

        Segment segment5 = new Segment(currentTime.plusHours(5), currentTime.plusHours(9));
        Segment segment6 = new Segment(currentTime.plusHours(10), currentTime.plusHours(18));
        Flight shortEarthTimeFlight2 = new Flight(List.of(segment5, segment6));


        List<Flight> testFlightList = List.of(longEarthTimeFlight1, shortEarthTimeFlight2);

        List<Flight> filtredFlights = filterFlightService.filterFlights(filterRules, testFlightList);
        assertEquals(1, filtredFlights.size());
        assertTrue(filtredFlights.contains(longEarthTimeFlight1));
        assertFalse(filtredFlights.contains(shortEarthTimeFlight2));
    }
}