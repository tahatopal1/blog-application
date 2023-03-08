package com.project.blogapp.controller;

import com.project.blogapp.dto.BlogDTO;
import com.project.blogapp.service.BlogService;
import com.project.blogapp.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blog")
@AllArgsConstructor
@Slf4j
public class BlogController {

    private BlogService blogService;

    private UserService userService;

    @PostMapping
    public ResponseEntity saveBlog(@RequestBody BlogDTO blogDTO){
        blogService.saveBlog(blogDTO);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateBlogContent(@PathVariable(value = "id") Long blogId, @RequestBody BlogDTO blogDTO){
        blogService.updateBlog(blogId, blogDTO);
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    @PutMapping("/{id}/tag/{tagId}")
    public ResponseEntity addTag(@PathVariable(value = "id") Long blogId, @PathVariable(value = "tagId") Long tagId){
        blogService.addTag(blogId, tagId);
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{id}/tag/{tagId}")
    public ResponseEntity discardTag(@PathVariable(value = "id") Long blogId, @PathVariable(value = "tagId") Long tagId){
        blogService.discardTag(blogId, tagId);
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    @GetMapping("/summaries/{username}")
    public ResponseEntity getAllSummariesByUsername(@PathVariable("username") String username){
        List<BlogDTO> blogPostSummaries = blogService.getAllBlogPostByUsernameWithSummaries(username);
        return new ResponseEntity(blogPostSummaries, HttpStatus.OK);
    }

    @GetMapping("/{username}")
    public ResponseEntity getAllBlogPosts(@PathVariable("username") String username){
        List<BlogDTO> blogs = blogService.getAllBlogPostsByUsername(username);
        return new ResponseEntity(blogs, HttpStatus.OK);
    }

    @GetMapping("/tag/{id}")
    public ResponseEntity getAllBlogPostsByTags(@PathVariable(value = "id") Long tagId){
        List<BlogDTO> blogs = blogService.getAllBlogPostsByTag(tagId);
        return new ResponseEntity(blogs, HttpStatus.OK);
    }


}
