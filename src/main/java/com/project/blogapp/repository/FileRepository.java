package com.project.blogapp.repository;

import com.project.blogapp.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> { }
