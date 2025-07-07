package ru.shubin.flight.service;

import ru.shubin.flight.model.Flight;

import java.util.List;
import java.util.Set;

public interface FilterFlightService {
    /**
     *
     * Метод для динамической фильтрации списка полетов на основе названия правила
     *
     * @param filterRules Список правил фильтрации перелетов
     * @param flightList  Список перелетов для фильтрации
     * @return список полетов после фильтрации
     */
    List<Flight> filterFlights(Set<String> filterRules, List<Flight> flightList);
}
