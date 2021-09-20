create table file_types
(
    id        bigint not null,
    is_active bit,
    type_name varchar(255),
    uuid      varchar(255),
    primary key (id)
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
insert into hibernate_sequence values (1);

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

create table root_folders_files
(
    root_folder_model_id bigint not null,
    files_id             bigint not null
);

create table root_folders_root_folder_access_model
(
    root_folder_model_id        bigint not null,
    root_folder_access_model_id bigint not null
);

create table users
(
    id        bigint not null,
    email     varchar(255),
    firstname varchar(255),
    lastname  varchar(255),
    password varchar(255),
    username  varchar(255),
    uuid      varchar(255),
    primary key (id)
);

create table users_root_folder_access_model
(
    user_model_id               bigint not null,
    root_folder_access_model_id bigint not null
);

create table users_root_folders_access
(
    id bigint not null,
    primary key (id)
);

create table users_root_folders_access_root_folders
(
    root_folder_access_model_id bigint not null,
    root_folders_id             bigint not null
);

create table users_root_folders_access_users
(
    root_folder_access_model_id bigint not null,
    users_id                    bigint not null
);

alter table file_types
    add constraint unique (type_name);

alter table folders_content_sub_files
    add constraint unique (sub_files_id);

alter table root_folders_files
    add constraint unique (files_id);

alter table root_folders_root_folder_access_model
    add constraint unique (root_folder_access_model_id);

alter table users
    add constraint unique (email);

alter table users
    add constraint unique (username);

alter table users_root_folder_access_model
    add constraint unique (root_folder_access_model_id);
alter table folders_content
    add constraint foreign key (file_creator_id) references users (id);
alter table folders_content
    add constraint foreign key (file_type_model_id) references file_types (id);
alter table folders_content
    add constraint foreign key (parent_folder_id) references folders_content (id);
alter table folders_content
    add constraint foreign key (root_folder_id) references root_folders (id);
alter table folders_content_sub_files
    add constraint foreign key (sub_files_id) references folders_content (id);
alter table folders_content_sub_files
    add constraint foreign key (content_file_model_id) references folders_content (id);
alter table root_folders
    add constraint foreign key (folder_creator_id) references users (id);
alter table root_folders_files
    add constraint foreign key (files_id) references folders_content (id);
alter table root_folders_files
    add constraint foreign key (root_folder_model_id) references root_folders (id);
alter table root_folders_root_folder_access_model
    add constraint foreign key (root_folder_access_model_id) references users_root_folders_access (id);
alter table root_folders_root_folder_access_model
    add constraint foreign key (root_folder_model_id) references root_folders (id);
alter table users_root_folder_access_model
    add constraint foreign key (root_folder_access_model_id) references users_root_folders_access (id);
alter table users_root_folder_access_model
    add constraint foreign key (user_model_id) references users (id);
alter table users_root_folders_access_root_folders
    add constraint foreign key (root_folders_id) references root_folders (id);
alter table users_root_folders_access_root_folders
    add constraint foreign key (root_folder_access_model_id) references users_root_folders_access (id);
alter table users_root_folders_access_users
    add constraint foreign key (users_id) references users (id);
alter table users_root_folders_access_users
    add constraint foreign key (root_folder_access_model_id) references users_root_folders_access (id);