package com.stellerainn.backend.controller;

import com.stellerainn.backend.common.Result;
import com.stellerainn.backend.entity.Tag;
import com.stellerainn.backend.mapper.TagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    @Autowired
    private TagMapper tagMapper;

    @GetMapping
    public Result<List<Tag>> getTags(@RequestParam Long userId) {
        return Result.success(tagMapper.findByUserId(userId));
    }

    @PostMapping
    public Result<Tag> createTag(@RequestBody Tag tag) {
        tagMapper.insert(tag);
        return Result.success(tag);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteTag(@PathVariable Long id, @RequestParam Long userId) {
        tagMapper.delete(id, userId);
        return Result.success(null);
    }
}
