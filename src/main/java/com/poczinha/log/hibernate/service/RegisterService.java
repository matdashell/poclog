package com.poczinha.log.hibernate.service;

import com.poczinha.log.hibernate.domain.response.ColumnActionResponse;
import com.poczinha.log.hibernate.domain.response.TableActionResponse;
import com.poczinha.log.hibernate.entity.RegisterEntity;
import com.poczinha.log.hibernate.entity.TableEntity;
import com.poczinha.log.hibernate.mapper.RegisterMapper;
import com.poczinha.log.hibernate.repository.RegisterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RegisterService {

    @Autowired
    private RegisterRepository registerRepository;
    @Autowired
    private TableService tableService;
    @Autowired
    private ColumnService columnService;
    @Autowired
    private IdentifierService identifierService;
    @Autowired
    private RegisterMapper registerMapper;

    public void register(String tableName, String columnName, String identifierName, String regType, String lastValue, String newValue) {

        Integer tableId = tableService.getTableId(tableName);
        if (tableId == null) {
            tableId = tableService.createTable(tableName);
        }

        Integer columnId = columnService.getColumnIdInTable(columnName, tableId);
        if (columnId == null) {
            columnId = columnService.createColumn(columnName, tableId);
        }

        Integer identifierId = identifierService.getIdentifierId(identifierName);
        if (identifierId == null) {
            identifierId = identifierService.createIdentifier(identifierName);
        }

        RegisterEntity registerEntity = registerMapper.toEntity(tableId, columnId, identifierId, regType, lastValue, newValue);

        registerRepository.save(registerEntity);
    }

    public List<String> listActionsOnRange(LocalDateTime startDate, LocalDateTime endDate) {
        return registerRepository.getAllIdentifierNamesByCreatedAtBetween(startDate, endDate);
    }

    public List<TableActionResponse> listActionsByIdentifierInRange(String identifierName, LocalDateTime startDate, LocalDateTime endDate) {
        List<TableEntity> tables = registerRepository.findAllTableByIdentifierNameAndCreatedAtBetween(identifierName, startDate, endDate);

        List<TableActionResponse> tableActionResponses = new ArrayList<>();

        for (TableEntity table : tables) {
            List<RegisterEntity> registers = registerRepository.findAllByTableAndIdentifierNameAndCreatedAtBetweenOrderByCreatedAt(table, identifierName, startDate, endDate);

            List<ColumnActionResponse> columnActionResponses = new ArrayList<>();

            for (RegisterEntity register : registers) {

                columnActionResponses.add(new ColumnActionResponse(
                        register.getColumn().getName(),
                        register.getType(),
                        register.getLastValue(),
                        register.getNewValue(),
                        register.getCreatedAt()
                ));
            }

            tableActionResponses.add(new TableActionResponse(
                    table.getName(),
                    columnActionResponses
            ));
        }

        return tableActionResponses;
    }
}
