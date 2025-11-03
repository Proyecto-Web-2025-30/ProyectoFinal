package com.example.welcomebackend.service;

import com.example.welcomebackend.model.Gateway;
import com.example.welcomebackend.model.Process;
import com.example.welcomebackend.repository.GatewayRepository;
import com.example.welcomebackend.repository.ProcessRepository;
import com.example.welcomebackend.repository.ArcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GatewayService {

    @Autowired private GatewayRepository gatewayRepository;
    @Autowired private ProcessRepository processRepository;
    @Autowired private ArcRepository arcRepository;

    @Transactional
    public Gateway createGateway(Long processId, Gateway gateway) {
        Process process = processRepository.findById(processId)
                .orElseThrow(() -> new RuntimeException("Process not found"));

        gateway.setProcess(process);

        process.setUpdatedAt(LocalDateTime.now());
        processRepository.save(process);

        return gatewayRepository.save(gateway);
    }

    @Transactional
    public Gateway updateGateway(Long gatewayId, Gateway updatedGateway) {
        Gateway gateway = gatewayRepository.findById(gatewayId)
                .orElseThrow(() -> new RuntimeException("Gateway not found"));

        gateway.setName(updatedGateway.getName());
        gateway.setGatewayType(updatedGateway.getGatewayType());
        gateway.setConditions(updatedGateway.getConditions());
        gateway.setX(updatedGateway.getX());
        gateway.setY(updatedGateway.getY());

        Process process = gateway.getProcess();
        process.setUpdatedAt(LocalDateTime.now());
        processRepository.save(process);

        return gatewayRepository.save(gateway);
    }

    @Transactional
    public void deleteGateway(Long gatewayId) {
        Gateway gateway = gatewayRepository.findById(gatewayId)
                .orElseThrow(() -> new RuntimeException("Gateway not found"));

        Process process = gateway.getProcess();
        process.setUpdatedAt(LocalDateTime.now());
        processRepository.save(process);

        arcRepository.deleteByProcessIdAndSourceTypeAndSourceId(process.getId(), "GATEWAY", gatewayId);
        arcRepository.deleteByProcessIdAndTargetTypeAndTargetId(process.getId(), "GATEWAY", gatewayId);

        gatewayRepository.delete(gateway);
    }

    @Transactional(readOnly = true)
    public List<Gateway> getGatewaysByProcess(Long processId) {
        return gatewayRepository.findByProcessId(processId);
    }

    @Transactional(readOnly = true)
    public Gateway getGatewayById(Long gatewayId) {
        return gatewayRepository.findById(gatewayId)
                .orElseThrow(() -> new RuntimeException("Gateway not found"));
    }
}
