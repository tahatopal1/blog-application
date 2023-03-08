drop table if exists blog_tag;
drop table if exists blog;
drop table if exists tag;

create table blog
(
    id      bigint       not null auto_increment primary key,
    title   VARCHAR(255) NOT NULL,
    content CLOB         NOT NULL
);


create table tag(
    id bigint not null auto_increment primary key,
    tag_name VARCHAR(255) NOT NULL
);

create table blog_tag(
    blog_id bigint,
    tag_id bigint,
    CONSTRAINT blog_tag_pk PRIMARY KEY (blog_id, tag_id),
    CONSTRAINT FK_blog FOREIGN KEY (blog_id) REFERENCES blog (id),
    CONSTRAINT FK_tag FOREIGN KEY (tag_id) REFERENCES tag (id)
);