package com.stellerainn.backend.mapper;

import com.stellerainn.backend.entity.SQLExample;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ACIRMapper {
    List<SQLExample> getAllExamples();
}
