package com.poczinha.log.bean;

import com.poczinha.log.domain.response.CorrelationModification;
import com.poczinha.log.domain.response.PeriodModification;
import com.poczinha.log.service.ColumnService;
import com.poczinha.log.service.CorrelationService;
import com.poczinha.log.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/log")
public class LogController {

    @Autowired
    private ColumnService columnService;

    @Autowired
    private RegisterService registerService;

    @Autowired
    private CorrelationService correlationService;

    @GetMapping("/period-modification/between/{start}/{end}")
    @ResponseStatus(HttpStatus.OK)
    public Page<PeriodModification> getAllPeriodModificationBetween(
            @PathVariable LocalDateTime start,
            @PathVariable LocalDateTime end,
            @RequestParam(required = false, value = "0") int page,
            @RequestParam(required = false, value = "25") int size) {

        return registerService.getAllPeriodModificationBetween(start, end, page, size);
    }

    @GetMapping("/modifications/correlation/{correlation}")
    @ResponseStatus(HttpStatus.OK)
    public CorrelationModification getAllModificationsByCorrelation(
            @PathVariable Long correlation) {

        return registerService.getAllModificationsByCorrelation(correlation);
    }

    @GetMapping("/period-modification/identifiers")
    @ResponseStatus(HttpStatus.OK)
    public Page<PeriodModification> getAllPeriodModificationByIdentifiers(
            @RequestParam List<String> values,
            @RequestParam(required = false, value = "0") int page,
            @RequestParam(required = false, value = "25") int size) {

        return correlationService.findAllByIdentifier(values, page, size);
    }
}