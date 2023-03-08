package com.project.blogapp.controller;

import com.project.blogapp.dto.BlogDTO;
import com.project.blogapp.service.BlogService;
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

    @GetMapping("/summaries")
    public ResponseEntity getAllSummaries(){
        List<BlogDTO> blogPostSummaries = blogService.getAllBlogPostsWithSummaries();
        return new ResponseEntity(blogPostSummaries, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getAllBlogPosts(){
        List<BlogDTO> blogs = blogService.getAllBlogPosts();
        return new ResponseEntity(blogs, HttpStatus.OK);
    }

    @GetMapping("/tag/{id}")
    public ResponseEntity getAllBlogPostsByTags(@PathVariable(value = "id") Long tagId){
        List<BlogDTO> blogs = blogService.getAllBlogPostsByTag(tagId);
        return new ResponseEntity(blogs, HttpStatus.OK);
    }


}
