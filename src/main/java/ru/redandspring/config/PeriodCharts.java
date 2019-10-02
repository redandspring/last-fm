package ru.redandspring.config;

import ru.redandspring.services.ServiceException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public enum PeriodCharts {

    CURRENT_MONTH,
    PREV_MONTH;

    static final DateTimeFormatter FORMAT_MONTH = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public String getDate() throws ServiceException {

        final LocalDate now = LocalDate.now().withDayOfMonth(1).minusDays(1);
        if (eq(PeriodCharts.CURRENT_MONTH)){
            return now.format(FORMAT_MONTH);
        }
        if (eq(PeriodCharts.PREV_MONTH)){
            return now.minusMonths(1).format(FORMAT_MONTH);
        }
        throw new ServiceException("Invalid operation PeriodCharts.getDate() with name="+name());
    }

    private boolean eq(PeriodCharts p){
        return name().equalsIgnoreCase(p.toString());
    }

}
