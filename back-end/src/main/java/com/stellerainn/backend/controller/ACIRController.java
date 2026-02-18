package com.stellerainn.backend.controller;

import com.stellerainn.backend.entity.SQLExample;
import com.stellerainn.backend.service.ACIRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ACIRController {

    @Autowired
    private ACIRService acirService;

    @RequestMapping("/get/allExamples")
    public List<SQLExample> getAllExamples() {
        return acirService.getAllExamples();
    }
}
