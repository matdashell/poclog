package com.poczinha.log.hibernate.mapper;

import com.poczinha.log.hibernate.entity.ColumnEntity;
import com.poczinha.log.hibernate.entity.IdentifierEntity;
import com.poczinha.log.hibernate.entity.RegisterEntity;
import com.poczinha.log.hibernate.entity.TableEntity;
import org.springframework.stereotype.Component;

@Component
public class RegisterMapper {
    public RegisterEntity toEntity(Integer tableId, Integer columnId, Integer identifierId, String regType, String lastValue, String newValue) {
        TableEntity tableEntity = new TableEntity(tableId);
        ColumnEntity columnEntity = new ColumnEntity(columnId);
        IdentifierEntity identifierEntity = new IdentifierEntity(identifierId);

        return new RegisterEntity(regType, tableEntity, columnEntity, identifierEntity, lastValue, newValue);
    }
}