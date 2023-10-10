package com.poczinha.log.bean;

import com.poczinha.log.domain.response.CorrelationModification;
import com.poczinha.log.domain.response.PeriodModification;
import com.poczinha.log.service.ColumnService;
import com.poczinha.log.service.CorrelationService;
import com.poczinha.log.service.RegisterService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
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

    @ApiOperation("Get correlations from database by period")
    @GetMapping("/period-modification/between/{start}/{end}")
    @ResponseStatus(HttpStatus.OK)
    public Page<PeriodModification> getAllPeriodModificationBetween(
            @ApiParam(value = "Start date", example = "2020-01-01T00:00:00")
            @PathVariable LocalDateTime start,

            @ApiParam(value = "End date", example = "2020-01-01T00:00:00")
            @PathVariable LocalDateTime end,

            @ApiParam(value = "Page number", example = "0")
            @RequestParam(required = false, defaultValue = "0") int page,

            @ApiParam(value = "Page size", example = "25")
            @RequestParam(required = false, defaultValue = "25") int size) {

        return registerService.getAllPeriodModificationBetween(start, end, page, size);
    }

    @ApiOperation("Get all entityModifications from database by correlation")
    @GetMapping("/modifications/correlation/{correlation}")
    @ResponseStatus(HttpStatus.OK)
    public CorrelationModification getAllModificationsByCorrelation(

            @ApiParam(value = "Correlation", example = "1")
            @PathVariable Long correlation) {

        return registerService.getAllModificationsByCorrelation(correlation);
    }

    @ApiOperation("Get correlations from database by identifier")
    @GetMapping("/period-modification/identifiers")
    @ResponseStatus(HttpStatus.OK)
    public Page<PeriodModification> getAllPeriodModificationByIdentifiers(

            @ApiParam(value = "Identifier", example = "ID, SESSION_ID")
            @RequestParam List<String> values,

            @ApiParam(value = "Page number", example = "0")
            @RequestParam(required = false, defaultValue = "0") int page,

            @ApiParam(value = "Page size", example = "25")
            @RequestParam(required = false, defaultValue = "25") int size) {

        return correlationService.findAllByIdentifier(values, page, size);
    }
}