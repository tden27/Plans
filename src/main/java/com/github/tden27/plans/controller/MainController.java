package com.github.tden27.plans.controller;

import com.github.tden27.plans.service.PlansService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sbp.sbt.sdk.exception.SdkJsonRpcClientException;

import java.util.List;

@RestController
public class MainController {
    private final PlansService plansService;

    public MainController(PlansService plansService) {
        this.plansService = plansService;
    }

    @RequestMapping(value = "/getPlansHistoryChanges")
    public ResponseEntity<List<String>> getPlansHistoryChanges(@RequestParam String planId) throws SdkJsonRpcClientException {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(plansService.getPlansHistoryChanges(planId));
    }
}