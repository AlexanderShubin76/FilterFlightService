package ru.shubin.flight.service;

import ru.shubin.flight.model.Flight;
import ru.shubin.flight.model.Segment;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class FilterFlightServiceImpl implements FilterFlightService {
    private final Map<String, Predicate<Segment>> filterRuleMatcher;
    private LocalDateTime prevArrivalDate;

    public FilterFlightServiceImpl() {
        this.filterRuleMatcher = getFilterRuleMatcher();
    }

    @Override
    public List<Flight> filterFlights(Set<String> filterRules, List<Flight> flightList) {
        Predicate<Segment> chosenFilters = chooseFilters(filterRules);
        List<Flight> filtredFlights = flightList.stream()
                .filter(flight -> {
                    prevArrivalDate = null;
                    return flight.getSegments().stream()
                            .allMatch(chosenFilters);
                })
                .toList();
        return filtredFlights;
    }

    // Метод для инициализации HashMap, содержащую пары "название правила" - предикат
    private Map<String, Predicate<Segment>> getFilterRuleMatcher(){
        Map<String, Predicate<Segment>> filterMap = new HashMap<>();
        filterMap.put("excludePastFlightsRule", filterPastFlights());
        filterMap.put("excludeFlightsByArrivalBeforeDepartureRule", filterFlightsByArrivalBeforeDeparture());
        filterMap.put("excludeFlightsByEarthTime", filterFlightsByEarthTime());
        return filterMap;
    }

    // Предикат для фильтрации полетов с сегментами, имеющими дату вылета до текущего момента времени
    private Predicate<Segment> filterPastFlights(){
        LocalDateTime currentTime = LocalDateTime.now();
        return segm-> segm.getDepartureDate().isAfter(currentTime);
    }

    // Предикат для фильтрации полетов с сегментами, имеющими дату прилёта раньше даты вылета
    private Predicate<Segment> filterFlightsByArrivalBeforeDeparture(){
        return segm-> segm.getDepartureDate().isBefore(segm.getArrivalDate());
    }

    // Предикат для фильтрации полетов с сегментами, где общее время, проведённое на земле превышает два часа
    private Predicate<Segment> filterFlightsByEarthTime(){
        return segm->{
            if (prevArrivalDate==null){
                prevArrivalDate = segm.getArrivalDate();
                return true;
            }
            else{
                Duration earthDuration = Duration.between(prevArrivalDate, segm.getDepartureDate());
                if(earthDuration.toHours() > 2){
                    prevArrivalDate = segm.getArrivalDate();
                    return true;
                }
                else{
                    return false;
                }
            }
        };
    }

    // Метод выбора предиката фильтрации на основе строкового представления правила
    private Predicate<Segment> chooseFilters(Set<String> filterRules) {
        // Фильтруем полеты, имеющие сегменты, у которых поля DepartureDate или ArrivalDate равны null
        Predicate<Segment> chosenFilters = _ ->true;
        for(String filterRule : filterRules){
            Predicate<Segment> predicateFilter = filterRuleMatcher.get(filterRule);
            if(predicateFilter!=null){
                chosenFilters = chosenFilters.and(predicateFilter);
            }
            else System.out.println("Filter" + filterRule + "doesn't exist");
        }
        return chosenFilters;
    }
}
