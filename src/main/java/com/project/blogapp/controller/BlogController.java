package com.project.blogapp.controller;

import com.project.blogapp.dto.BlogDTO;
import com.project.blogapp.service.BlogService;
import com.project.blogapp.service.SearchService;
import com.project.blogapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/blog")
@AllArgsConstructor
@Tag(name = "Blog")
@Slf4j
public class BlogController {

    private BlogService blogService;

    private UserService userService;

    private SearchService searchService;

    @PostMapping
    @Operation(
            description = "Save a New Blog",
            responses = {
                    @ApiResponse(responseCode = "400", ref = "badRequestResponseAPI"),
                    @ApiResponse(responseCode = "500", ref = "genericErrorAPI"),
                    @ApiResponse(responseCode = "201", description = "Created!")
            },
            security = @SecurityRequirement(name = "token")
    )
    public ResponseEntity saveBlog(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(
                                    value = "{\"title\" : \"Blog Title\", \"content\" : \"Really long content...\"}"
                            ),
                    }
            ))
            @RequestBody BlogDTO blogDTO) {
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
            },
            security = @SecurityRequirement(name = "token")
    )
    public ResponseEntity updateBlogContent(@PathVariable(value = "id") Long blogId,
                                            @io.swagger.v3.oas.annotations.parameters.RequestBody(ref = "blogRequestAPI") @RequestBody BlogDTO blogDTO) {
        blogService.updateBlog(blogId, blogDTO);
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{id}")
    @Operation(
            description = "Delete a Blog",
            responses = {
                    @ApiResponse(responseCode = "400", ref = "badRequestResponseAPI"),
                    @ApiResponse(responseCode = "500", ref = "genericErrorAPI"),
                    @ApiResponse(responseCode = "202", description = "Accepted!")
            },
            security = @SecurityRequirement(name = "token")
    )
    public ResponseEntity deleteBlog(@PathVariable(value = "id") Long blogId){
        blogService.deleteBlogById(blogId);
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    @PutMapping("/{id}/tag/{tagId}")
    @Operation(
            description = "Add Tag to a Blog",
            responses = {
                    @ApiResponse(responseCode = "400", ref = "badRequestResponseAPI"),
                    @ApiResponse(responseCode = "500", ref = "genericErrorAPI"),
                    @ApiResponse(responseCode = "202", description = "Accepted!")
            },
            security = @SecurityRequirement(name = "token")
    )
    public ResponseEntity addTag(@PathVariable(value = "id") Long blogId, @PathVariable(value = "tagId") Long tagId) {
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
            },
            security = @SecurityRequirement(name = "token")
    )
    public ResponseEntity discardTag(@PathVariable(value = "id") Long blogId, @PathVariable(value = "tagId") Long tagId) {
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
            },
            security = @SecurityRequirement(name = "token")
    )
    public ResponseEntity getAllSummariesByUsername(@PathVariable("username") String username,
                                                    @PageableDefault(size = 10) Pageable pageable){
        List<BlogDTO> blogPostSummaries = blogService.getAllBlogPostByUsernameWithSummaries(username, pageable);
        return new ResponseEntity(blogPostSummaries, HttpStatus.OK);
    }

    @GetMapping("/user/{username}")
    @Operation(
            description = "Get All Blogs by Username",
            responses = {
                    @ApiResponse(responseCode = "400", ref = "badRequestResponseAPI"),
                    @ApiResponse(responseCode = "500", ref = "genericErrorAPI"),
                    @ApiResponse(responseCode = "200", ref = "blogListResponseAPI")
            },
            security = @SecurityRequirement(name = "token")
    )
    public ResponseEntity getAllBlogPosts(@PathVariable("username") String username) {
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
            },
            security = @SecurityRequirement(name = "token")
    )
    public ResponseEntity getAllBlogPostsByTags(@PathVariable(value = "id") Long tagId) {
        List<BlogDTO> blogs = blogService.getAllBlogPostsByTag(tagId);
        return new ResponseEntity(blogs, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<BlogDTO>> searchBlogs(@RequestParam String searchText,
                                                     @PageableDefault(size = 10) Pageable pageable) {
        return new ResponseEntity<>(searchService.search(searchText, pageable), HttpStatus.OK);
    }

    @PostMapping("/{id}/file")
    public ResponseEntity<?> uploadFile(@RequestParam("image")MultipartFile file,
                                        @PathVariable(name = "id") Long blogId,
                                        @RequestParam(name = "scale", required = false) Double scale,
                                        @RequestParam(name = "quality", required = false) Float quality) throws Exception {
        blogService.uploadFile(file, blogId, scale, quality);
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

    @GetMapping("/{id}/file/{fileName}")
    public ResponseEntity<?> downloadFile(@PathVariable Long id, @PathVariable String fileName) throws IOException {
        byte[] imageData=blogService.downloadFile(id, fileName);
        return ResponseEntity.status(HttpStatus.OK)
                .body(imageData);
    }

    @DeleteMapping("/{id}/file/{fileName}")
    public ResponseEntity deleteFile(@PathVariable Long id, @PathVariable String fileName){
        blogService.deleteFile(id, fileName);
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

}
