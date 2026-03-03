package com.stellerainn.backend.mapper;

import com.stellerainn.backend.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);

    @Insert("INSERT INTO users(username, password, email) VALUES(#{username}, #{password}, #{email})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);
    
    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(Long id);
}
