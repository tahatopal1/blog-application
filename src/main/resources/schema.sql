drop table if exists blog_tag;
drop table if exists blog;
drop table if exists tag;

create table blog
(
    id      bigint       not null auto_increment primary key,
    title   VARCHAR(255) NOT NULL,
    content CLOB         NOT NULL
) engine = InnoDB;


create table tag(
                    id bigint not null auto_increment primary key,
                    tag_name VARCHAR(255) NOT NULL
) engine = InnoDB;

create table blog_tag(
                         blog_id bigint,
                         tag_id bigint,
                         CONSTRAINT blog_tag_pk PRIMARY KEY (blog_id, tag_id),
                         CONSTRAINT FK_blog FOREIGN KEY (blog_id) REFERENCES blog (id),
                         CONSTRAINT FK_tag FOREIGN KEY (tag_id) REFERENCES tag (id)
) engine = InnoDB;

insert into blog (id, title, content)
values (1, 'First Blog Post',
        'Lorem ipsum, dolor sit amet consectetur adipisicing elit. Saepe doloribus pariatur dolorem quae numquam distinctio aut, voluptatum consequatur ad. Provident aliquid rerum minus quo tenetur, sed libero, neque id obcaecati perferendis temporibus non nihil nobis possimus, ratione omnis natus voluptatem numquam! Suscipit, fuga totam! Odit ipsa, blanditiis harum ratione quae inventore expedita, suscipit minus quia magnam, nemo non aliquid necessitatibus porro quod sapiente quidem beatae? Error voluptatem tempora laboriosam amet totam earum ipsam, veniam corporis sapiente magni commodi natus consequatur, numquam reprehenderit odio eum nulla vel sint itaque ex inventore fugit temporibus perferendis? Non velit, commodi nesciunt laborum qui minima.');

insert into blog (id, title, content)
values (2, 'Second Blog Post',
        'Lorem ipsum dolor sit amet consectetur adipisicing elit. Enim laborum rerum soluta asperiores nihil quam quisquam rem veniam laudantium animi, ipsum, eius tempore magni nemo eum aliquid libero, amet ad eligendi. Provident mollitia perferendis recusandae non blanditiis vero corporis aliquam maiores. Animi quos eum suscipit autem dolores saepe ducimus libero illo et? Provident impedit exercitationem fuga facilis obcaecati aut itaque debitis accusamus nesciunt dignissimos assumenda explicabo vero voluptatem id, numquam animi ratione dolorem unde. Sapiente, maxime fuga! Fugiat ipsum esse reprehenderit cupiditate vel corporis, veniam vero ducimus amet quisquam nobis libero placeat quod illum fugit commodi a obcaecati? Fugiat, blanditiis.');

insert into blog (id, title, content)
values (3, 'Third Blog Post',
        'Lorem ipsum dolor sit amet consectetur adipisicing elit. Aliquam ducimus doloremque totam adipisci nobis nesciunt ab nulla temporibus. Quo obcaecati consequuntur odio quos, delectus vel soluta distinctio ad accusantium tenetur qui, temporibus neque commodi suscipit labore, culpa nemo ratione dignissimos eligendi alias ipsam dolores rem! Repellat veritatis praesentium, ut minus vero similique itaque saepe tempora nulla assumenda non cupiditate adipisci, ratione corrupti ad, aut optio? Eaque ad in, facilis inventore vitae, aliquam eum qui molestiae debitis sequi aperiam delectus animi, fugiat dolorum consequuntur soluta autem dolores. Sunt, provident? Voluptatem excepturi sequi saepe natus quidem animi similique atque recusandae cum fugit.');

insert into tag (id, tag_name) values (1, 'Backend');
insert into tag (id, tag_name) values (2, 'Frontend');
insert into tag (id, tag_name) values (3, 'Mobile');
insert into tag (id, tag_name) values (4, 'DevOps');
insert into tag (id, tag_name) values (5, 'Network');
insert into tag (id, tag_name) values (6, 'Database');

insert into blog_tag (blog_id, tag_id) values (1, 1);
insert into blog_tag (blog_id, tag_id) values (2, 2);
insert into blog_tag (blog_id, tag_id) values (3, 3);