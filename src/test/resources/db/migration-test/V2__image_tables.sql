create table file
(
    id                 bigint not null auto_increment primary key,
    created_date       datetime(6),
    last_modified_date datetime(6),
    name               varchar(255),
    type               varchar(255),
    blog_id            bigint,
    CONSTRAINT FK_blog_image FOREIGN KEY (blog_id) REFERENCES blog (id)
);