DROP TABLE IF EXISTS blog_tag;
DROP TABLE IF EXISTS blog;
DROP TABLE IF EXISTS tag;
DROP TABLE IF EXISTS _user;

CREATE TABLE _user
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_date       TIMESTAMP,
    last_modified_date TIMESTAMP,
    user_name          VARCHAR(255) NOT NULL,
    pass               VARCHAR(255) NOT NULL,
    display_name       VARCHAR(255) NOT NULL
);

CREATE TABLE blog
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_date       TIMESTAMP,
    last_modified_date TIMESTAMP,
    title              VARCHAR(255) NOT NULL,
    content            CLOB NOT NULL,
    user_id            BIGINT,
    FOREIGN KEY (user_id) REFERENCES _user (id)
);

CREATE TABLE tag
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_date       TIMESTAMP,
    last_modified_date TIMESTAMP,
    tag_name           VARCHAR(255) NOT NULL
);

CREATE TABLE blog_tag
(
    blog_id BIGINT,
    tag_id  BIGINT,
    PRIMARY KEY (blog_id, tag_id),
    FOREIGN KEY (blog_id) REFERENCES blog (id),
    FOREIGN KEY (tag_id) REFERENCES tag (id)
);
