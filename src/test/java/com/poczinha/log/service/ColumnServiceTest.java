package com.poczinha.log.service;

import com.poczinha.log.hibernate.entity.LogColumnEntity;
import com.poczinha.log.hibernate.entity.LogTableEntity;
import com.poczinha.log.hibernate.repository.ColumnRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ColumnServiceTest {

    @Spy
    @InjectMocks
    private ColumnService columnService;

    @Mock
    private ColumnRepository columnRepository;

    @Mock
    private TableService tableService;

    private final String tableName = "table";
    private final String fieldName = "column";
    private final LogColumnEntity logColumnEntity = new LogColumnEntity();
    private final LogTableEntity logTableEntity = new LogTableEntity("any");

    @Test
    @DisplayName("Should retrieve table and column")
    void shouldRetrieveTableAndColumn() {
        doReturn(logColumnEntity).when(columnService).createColumn(any(), any());

        LogColumnEntity result = columnService.retrieveOrStoreByTableAndColumn(tableName, fieldName);

        verify(columnRepository).findIdByTableAndField(tableName, fieldName);

        assertEquals(logColumnEntity, result);
    }

    @Test
    @DisplayName("Should store and retrieve by table and column")
    void shouldStoreAndRetrieveByTableAndColumn() {
        doReturn(null).when(columnService).findByNameAndTable(any(), any());
        doReturn(logColumnEntity).when(columnService).createColumn(any(), any());

        LogColumnEntity result = columnService.retrieveOrStoreByTableAndColumn(tableName, fieldName);

        verify(columnService).findByNameAndTable(tableName, fieldName);
        verify(columnService).createColumn(tableName, fieldName);

        assertEquals(logColumnEntity, result);
    }

    @Test
    @DisplayName("Should find by name and table")
    void shouldFindByNameAndTable() {
        doReturn(logColumnEntity).when(columnRepository).findIdByTableAndField(any(), any());

        LogColumnEntity result = columnService.findByNameAndTable(tableName, fieldName);

        verify(columnRepository).findIdByTableAndField(tableName, fieldName);

        assertEquals(logColumnEntity, result);
    }

    @Test
    @DisplayName("Should create column")
    void shouldCreateColumn() {
        doReturn(logColumnEntity).when(columnRepository).save(any());
        doReturn(logTableEntity).when(tableService).retrieveOrStore(any());

        LogColumnEntity result = columnService.createColumn(tableName, fieldName);

        verify(columnRepository).save(any());
        verify(tableService).retrieveOrStore(tableName);

        assertEquals(logColumnEntity.getId(), result.getId());
    }
}