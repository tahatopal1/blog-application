package com.project.blogapp.controller;

import com.project.blogapp.dto.BlogDTO;
import com.project.blogapp.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@Tag(name = "Tag")
@Slf4j
public class TagController {

    private TagService tagService;

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
        tagService.addTag(blogId, tagId);
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
        tagService.discardTag(blogId, tagId);
        return new ResponseEntity(HttpStatus.ACCEPTED);
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
        List<BlogDTO> blogs = tagService.getAllBlogPostsByTag(tagId);
        return new ResponseEntity(blogs, HttpStatus.OK);
    }

}
