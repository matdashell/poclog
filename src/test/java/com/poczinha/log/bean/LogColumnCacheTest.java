package com.poczinha.log.bean;

import com.poczinha.log.hibernate.entity.LogColumnEntity;
import com.poczinha.log.service.ColumnService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogColumnCacheTest {

    private LogColumnCache logColumnCache;

    @Mock
    private ColumnService columnService;

    @Test
    @DisplayName("Should create a new instance and set columnService")
    public void setUp() {
        logColumnCache = new LogColumnCache(columnService);
        shouldReturnWhenColumnIsNotCached();
        shouldReturnWhenColumnIsCached();
    }

    @DisplayName("Should return when column is not cached")
    public void shouldReturnWhenColumnIsNotCached() {
        LogColumnEntity logColumnEntity = new LogColumnEntity(null, "column");
        when(columnService.retrieveOrStoreByTableAndColumn(any(), any())).thenReturn(logColumnEntity);

        LogColumnEntity result = logColumnCache.retrieveOrStore("table", "column");

        verify(columnService).retrieveOrStoreByTableAndColumn("table", "column");

        assertEquals(logColumnEntity, result);
    }

    @DisplayName("Should return when column is cached")
    public void shouldReturnWhenColumnIsCached() {
        LogColumnEntity result = logColumnCache.retrieveOrStore(null, "column");

        assertNull(result.getTable());
        assertEquals("column", result.getField());
    }
}