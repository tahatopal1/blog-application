drop table if exists blog_tag;
drop table if exists blog;
drop table if exists tag;
drop table if exists _user;

create table _user
(
    id                 bigint       not null auto_increment primary key,
    created_date       datetime,
    last_modified_date datetime,
    user_name          VARCHAR(255) NOT NULL,
    pass               VARCHAR(255) NOT NULL,
    display_name       VARCHAR(255) NOT NULL
);

create table blog
(
    id                 bigint       not null auto_increment primary key,
    created_date       datetime,
    last_modified_date datetime,
    title              VARCHAR(255) NOT NULL,
    content            LONGTEXT     NOT NULL,
    user_id            bigint,
    CONSTRAINT FK_user FOREIGN KEY (user_id) REFERENCES _user (id)
);


create table tag
(
    id                 bigint       not null auto_increment primary key,
    created_date       datetime,
    last_modified_date datetime,
    tag_name           VARCHAR(255) NOT NULL
);

create table blog_tag
(
    blog_id bigint,
    tag_id  bigint,
    CONSTRAINT blog_tag_pk PRIMARY KEY (blog_id, tag_id),
    CONSTRAINT FK_blog FOREIGN KEY (blog_id) REFERENCES blog (id),
    CONSTRAINT FK_tag FOREIGN KEY (tag_id) REFERENCES tag (id)
);

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