package com.stellerainn.backend.service;

import com.stellerainn.backend.entity.SQLExample;
import com.stellerainn.backend.mapper.ACIRMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ACIRService {
    @Autowired
    private ACIRMapper acirMapper;

    public List<SQLExample> getAllExamples() {
        return acirMapper.getAllExamples();
    }
}
