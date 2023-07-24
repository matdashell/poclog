package com.poczinha.log.hibernate.controler;

import com.poczinha.log.hibernate.domain.response.TableActionResponse;
import com.poczinha.log.hibernate.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/logger")
public class LoggerController {

    @Autowired
    private RegisterService registerService;

    @GetMapping("/list-all-identifiers-in-range/{startDate}/{endDate}")
    @ResponseStatus(HttpStatus.OK)
    public List<String> listAllIdentifiersInRange(
            @PathVariable LocalDateTime startDate,
            @PathVariable LocalDateTime endDate
    ) {
        return registerService.listActionsOnRange(startDate, endDate);
    }

    @GetMapping("/list-all-actions-by-identifier/{identifierName}/in-range/{startDate}/{endDate}")
    @ResponseStatus(HttpStatus.OK)
    public List<TableActionResponse> listAllActionsByIdentifierInRange(
            @PathVariable String identifierName,
            @PathVariable LocalDateTime startDate,
            @PathVariable LocalDateTime endDate
    ) {
        return registerService.listActionsByIdentifierInRange(identifierName, startDate, endDate);
    }
}