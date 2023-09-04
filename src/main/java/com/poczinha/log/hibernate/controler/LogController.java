package com.poczinha.log.hibernate.controler;

import com.poczinha.log.hibernate.domain.response.IdentifierDate;
import com.poczinha.log.hibernate.domain.response.ModificationIdentifier;
import com.poczinha.log.hibernate.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/log")
public class LogController {

    @Autowired
    private RegisterService registerService;

    @GetMapping("/indentifiers-between/{start}/{end}")
    @ResponseStatus(HttpStatus.OK)
    public List<IdentifierDate> getAllIdentifiersModifiedIn(
            @PathVariable LocalDateTime start,
            @PathVariable LocalDateTime end) {

        return registerService.getAllIdentifiersModifiedIn(start, end);
    }

    @GetMapping("/modifications/identifier/{identifier}/between/{start}/{end}")
    @ResponseStatus(HttpStatus.OK)
    public List<ModificationIdentifier> getAllModificationsByIdentifierBetween(
            @PathVariable String identifier,
            @PathVariable LocalDateTime start,
            @PathVariable LocalDateTime end) {

        return registerService.getAllModificationsByIdentifierBetween(identifier, start, end);
    }

    @GetMapping("/modifications/correlation/{correlation}")
    @ResponseStatus(HttpStatus.OK)
    public List<ModificationIdentifier> getAllModificationsByCorrelation(
            @PathVariable String correlation) {

        return registerService.getAllModificationsByCorrelation(correlation);
    }

}