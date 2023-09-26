package com.poczinha.log.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poczinha.log.domain.CorrelationModification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    @Value("${spring.kafka.audit-log.producer.topic:audit-log}")
    private String topic;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendAuditMessage(CorrelationModification modification) {
        try {

            String auditMessage = objectMapper.writeValueAsString(modification.getGroups());
            modification.setAudit(auditMessage);
            modification.setGroups(null);

            kafkaTemplate.send(topic, objectMapper.writeValueAsString(modification));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
