package com.poczinha.log.service;

import com.poczinha.log.domain.response.PeriodModification;
import com.poczinha.log.hibernate.entity.LogCorrelationEntity;
import com.poczinha.log.hibernate.repository.CorrelationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CorrelationService {

    @Autowired
    private EntityManager em;

    @Autowired
    private CorrelationRepository correlationRepository;

    public void save(LogCorrelationEntity correlation) {
        correlationRepository.save(correlation);
    }

    public Page<PeriodModification> findAllByIdentifier(List<String> values, int page, int size) {
        if (values.isEmpty()) {
            return Page.empty();
        }

        Set<String> uniqueSet = new HashSet<>(values);
        List<String> uniqueList = new ArrayList<>(uniqueSet);

        TypedQuery<PeriodModification> selectQuery = em.createQuery(
                createSelectQuery(uniqueList),
                PeriodModification.class);

        TypedQuery<Long> countQuery = em.createQuery(
                createCountQuery(values),
                Long.class);

        selectQuery.setMaxResults(size);
        selectQuery.setFirstResult(page * size);

        for (int count = 0; count < uniqueList.size(); count++) {
            selectQuery.setParameter("param" + count, "%" + uniqueList.get(count).toLowerCase() + "%");
            countQuery.setParameter("param" + count, "%" + uniqueList.get(count).toLowerCase() + "%");
        }

        Long total = countQuery.getSingleResult();
        return new PageImpl<>(selectQuery.getResultList(), PageRequest.of(page, size), total);
    }

    private String createCountQuery(List<String> values) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT count(c) ");
        createFooterQuery(query, values);

        return query.toString();
    }

    private String createSelectQuery(List<String> values) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT new com.poczinha.log.domain.response.PeriodModification(");
        query.append("c.identifier, c.id, c.date) ");
        createFooterQuery(query, values);
        query.append(" ORDER BY c.date DESC");

        return query.toString();
    }

    private void createFooterQuery(StringBuilder query, List<String> values) {
        query.append("FROM CorrelationEntity c ");
        query.append("WHERE ");

        for (int count = 0; count < values.size(); count++) {
            query.append("LOWER(c.identifier) LIKE :param").append(count);
            if (count < values.size() - 1) {
                query.append(" OR ");
            }
        }
    }
}
