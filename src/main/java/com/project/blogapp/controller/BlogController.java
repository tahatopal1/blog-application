package com.project.blogapp.controller;

import com.project.blogapp.dto.BlogDTO;
import com.project.blogapp.service.BlogService;
import com.project.blogapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blog")
@AllArgsConstructor
@Tag(name = "Blog")
@Slf4j
public class BlogController {

    private BlogService blogService;

    private UserService userService;

    @PostMapping
    @Operation(
            description = "Save a New Blog",
            responses = {
                    @ApiResponse(responseCode = "400", ref = "badRequestResponseAPI"),
                    @ApiResponse(responseCode = "500", ref = "genericErrorAPI"),
                    @ApiResponse(responseCode = "201", description = "Created!")
            }
    )
    public ResponseEntity saveBlog(@io.swagger.v3.oas.annotations.parameters.RequestBody(ref = "blogRequestAPI")
                                       @RequestBody BlogDTO blogDTO){
        blogService.saveBlog(blogDTO);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(
            description = "Update a Blog",
            responses = {
                    @ApiResponse(responseCode = "400", ref = "badRequestResponseAPI"),
                    @ApiResponse(responseCode = "500", ref = "genericErrorAPI"),
                    @ApiResponse(responseCode = "202", description = "Accepted!")
            }
    )
    public ResponseEntity updateBlogContent(@PathVariable(value = "id") Long blogId,
                                            @io.swagger.v3.oas.annotations.parameters.RequestBody(ref = "blogRequestAPI") @RequestBody BlogDTO blogDTO){
        blogService.updateBlog(blogId, blogDTO);
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    @PutMapping("/{id}/tag/{tagId}")
    @Operation(
            description = "Add Tag to a Blog",
            responses = {
                    @ApiResponse(responseCode = "400", ref = "badRequestResponseAPI"),
                    @ApiResponse(responseCode = "500", ref = "genericErrorAPI"),
                    @ApiResponse(responseCode = "202", description = "Accepted!")
            }
    )
    public ResponseEntity addTag(@PathVariable(value = "id") Long blogId, @PathVariable(value = "tagId") Long tagId){
        blogService.addTag(blogId, tagId);
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{id}/tag/{tagId}")
    @Operation(
            description = "Delete Tag from a Blog",
            responses = {
                    @ApiResponse(responseCode = "400", ref = "badRequestResponseAPI"),
                    @ApiResponse(responseCode = "500", ref = "genericErrorAPI"),
                    @ApiResponse(responseCode = "202", description = "Accepted!")
            }
    )
    public ResponseEntity discardTag(@PathVariable(value = "id") Long blogId, @PathVariable(value = "tagId") Long tagId){
        blogService.discardTag(blogId, tagId);
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    @GetMapping("/summaries/{username}")
    @Operation(
            description = "Get All Blog Summaries by Username",
            responses = {
                    @ApiResponse(responseCode = "400", ref = "badRequestResponseAPI"),
                    @ApiResponse(responseCode = "500", ref = "genericErrorAPI"),
                    @ApiResponse(responseCode = "200", ref = "blogListSummaryResponseAPI")
            }
    )
    public ResponseEntity getAllSummariesByUsername(@PathVariable("username") String username){
        List<BlogDTO> blogPostSummaries = blogService.getAllBlogPostByUsernameWithSummaries(username);
        return new ResponseEntity(blogPostSummaries, HttpStatus.OK);
    }

    @GetMapping("/user/{username}")
    @Operation(
            description = "Get All Blogs by Username",
            responses = {
                    @ApiResponse(responseCode = "400", ref = "badRequestResponseAPI"),
                    @ApiResponse(responseCode = "500", ref = "genericErrorAPI"),
                    @ApiResponse(responseCode = "200", ref = "blogListResponseAPI")
            }
    )
    public ResponseEntity getAllBlogPosts(@PathVariable("username") String username){
        List<BlogDTO> blogs = blogService.getAllBlogPostsByUsername(username);
        return new ResponseEntity(blogs, HttpStatus.OK);
    }

    @GetMapping("/tag/{id}")
    @Operation(
            description = "Get All Blogs by Tag",
            responses = {
                    @ApiResponse(responseCode = "400", ref = "badRequestResponseAPI"),
                    @ApiResponse(responseCode = "500", ref = "genericErrorAPI"),
                    @ApiResponse(responseCode = "200", ref = "blogListResponseAPI")
            }
    )
    public ResponseEntity getAllBlogPostsByTags(@PathVariable(value = "id") Long tagId){
        List<BlogDTO> blogs = blogService.getAllBlogPostsByTag(tagId);
        return new ResponseEntity(blogs, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(
            description = "Get a Blog by ID",
            responses = {
                    @ApiResponse(responseCode = "400", ref = "badRequestResponseAPI"),
                    @ApiResponse(responseCode = "500", ref = "genericErrorAPI"),
                    @ApiResponse(responseCode = "200", ref = "blogResponseAPI")
            }
    )
    public ResponseEntity getBlog(@PathVariable(value = "id") Long id){
        BlogDTO blog = blogService.getBlogById(id);
        return new ResponseEntity(blog, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(
            description = "Delete a Blog",
            responses = {
                    @ApiResponse(responseCode = "400", ref = "badRequestResponseAPI"),
                    @ApiResponse(responseCode = "500", ref = "genericErrorAPI"),
                    @ApiResponse(responseCode = "200", ref = "blogResponseAPI")
            }
    )
    public ResponseEntity deleteBlog(@PathVariable(value = "id") Long id){
        blogService.deleteBlogById(id);
        return new ResponseEntity(HttpStatus.OK);
    }

}
