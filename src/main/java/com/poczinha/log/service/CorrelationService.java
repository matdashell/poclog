package com.poczinha.log.service;

import com.poczinha.log.domain.response.PeriodModification;
import com.poczinha.log.hibernate.entity.CorrelationEntity;
import com.poczinha.log.hibernate.repository.CorrelationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
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

    public void save(CorrelationEntity correlation) {
        correlationRepository.save(correlation);
    }

    public List<PeriodModification> findAllByIdentifier(List<String> identifiers) {
        if (identifiers.isEmpty()) {
            return new ArrayList<>();
        }

        Set<String> uniqueSet = new HashSet<>(identifiers);
        List<String> uniqueList = new ArrayList<>(uniqueSet);

        StringBuilder query = getStringBuilder(uniqueList);
        Query q = em.createQuery(query.toString(), PeriodModification.class);

        for (int count = 0; count < uniqueList.size(); count++) {
            q.setParameter("param" + count, "%" + uniqueList.get(count).toLowerCase() + "%");
        }

        return q.getResultList();
    }

    private static StringBuilder getStringBuilder(List<String> uniqueList) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT new com.poczinha.log.domain.response.PeriodModification(");
        query.append("c.identifier, c.id, c.date) ");
        query.append("FROM CorrelationEntity c ");
        query.append("WHERE ");

        for (int count = 0; count < uniqueList.size(); count++) {
            query.append("LOWER(c.identifier) LIKE :param").append(count);
            if (count < uniqueList.size() - 1) {
                query.append(" OR ");
            }
        }
        return query;
    }
}
