CREATE TABLE file
(
    id                 BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_date       TIMESTAMP,
    last_modified_date TIMESTAMP,
    name               VARCHAR(255),
    type               VARCHAR(255),
    blog_id            BIGINT,
    CONSTRAINT FK_blog_image FOREIGN KEY (blog_id) REFERENCES blog (id)
);
