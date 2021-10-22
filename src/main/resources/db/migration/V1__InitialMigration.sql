Create table file_mimes
(
    id        bigint not null,
    file_mime varchar(255),
    primary key (id)
);
CREATE TABLE file_types
(
    `id`        bigint NOT NULL,
    `is_active` bit(1)       DEFAULT NULL,
    `type_name` varchar(255) DEFAULT NULL,
    `uuid`      varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
);
create table file_types_file_mimes
(
    file_type_model_id bigint not null,
    file_mime_id       bigint not null
);
create table folders_content
(
    id                 bigint not null,
    added_date         datetime(6),
    file_name          varchar(255),
    last_modified_date datetime(6),
    path               varchar(255),
    file_size          double precision,
    uuid               varchar(255),
    file_creator_id    bigint,
    file_type_model_id bigint,
    parent_folder_id   bigint,
    root_folder_id     bigint,
    primary key (id)
);
create table folders_content_sub_files
(
    content_file_model_id bigint not null,
    sub_files_id          bigint not null
);
create table hibernate_sequence
(
    next_val bigint
);
insert into hibernate_sequence
values (1);
create table root_folders
(
    id                bigint not null,
    folder_name       varchar(255),
    path              varchar(255),
    is_shared         bit,
    uuid              varchar(255),
    folder_creator_id bigint,
    primary key (id)
);
create table root_folders_allowed_users
(
    root_folder_model_id bigint not null,
    allowed_users_id     bigint not null
);
create table root_folders_files
(
    root_folder_model_id bigint not null,
    files_id             bigint not null
);
create table users
(
    id        bigint not null,
    email     varchar(255),
    firstname varchar(255),
    lastname  varchar(255),
    password  varchar(255),
    username  varchar(255),
    uuid      varchar(255),
    primary key (id)
);

alter table file_types
    add constraint UK_orlvtgw8yhalek5yim4prvgyo unique (type_name);
alter table file_types_file_mimes
    add constraint UK_9ey7tf4lg0ehgx8hglt5bv3q7 unique (file_mime_id);
alter table file_types_file_mimes
    add constraint FKk6rbk0cj8os7ign75qkvnpp4h foreign key (file_mime_id) references file_mimes (id);
alter table file_types_file_mimes
    add constraint FK9s5mi2nf8oxmbx5puay7qg0gd foreign key (file_type_model_id) references file_types (id);
alter table folders_content_sub_files
    add constraint UK_f4t7tv17iammr8up6r8m71dnm unique (sub_files_id);
alter table root_folders_files
    add constraint UK_evmtqbglwr6d2dqoaju66wam1 unique (files_id);
alter table users
    add constraint UK_6dotkott2kjsp8vw4d0m25fb7 unique (email);
alter table users
    add constraint UK_r43af9ap4edm43mmtq01oddj6 unique (username);
alter table folders_content
    add constraint FK3t4us6gojbk211c0s1veuvcea foreign key (file_creator_id) references users (id);
alter table folders_content
    add constraint FKnqto4556ihm5s7iajbjsxchy4 foreign key (file_type_model_id) references file_types (id);
alter table folders_content
    add constraint FKriknl6l8kwfobbdqfwqwmwr9h foreign key (parent_folder_id) references folders_content (id);
alter table folders_content
    add constraint FKsod4sojrniysj9tb1hdck3eae foreign key (root_folder_id) references root_folders (id);
alter table folders_content_sub_files
    add constraint FKouv2cc314cumj5i17a6w8hqod foreign key (sub_files_id) references folders_content (id);
alter table folders_content_sub_files
    add constraint FKkbwc04aefjwejccngcj06crpk foreign key (content_file_model_id) references folders_content (id);
alter table root_folders
    add constraint FK7ai06s3wd3hyqa78jl7h9p17u foreign key (folder_creator_id) references users (id);
alter table root_folders_allowed_users
    add constraint FKnt0pmkr5wtg6pwtufeqtybk4h foreign key (allowed_users_id) references users (id);
alter table root_folders_allowed_users
    add constraint FKofqbthx6515obbw1blikxqcfl foreign key (root_folder_model_id) references root_folders (id);
alter table root_folders_files
    add constraint FKqutfnkvaom3ql034c56o75n5k foreign key (files_id) references folders_content (id);
alter table root_folders_files
    add constraint FKhcpvinmsqnqk8t3a639egic8j foreign key (root_folder_model_id) references root_folders (id);