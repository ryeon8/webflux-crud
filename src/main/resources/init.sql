create table if not exists users (
  id serial primary key,
  email varchar(255) not null
);

create table if not exists noti (
	id serial primary key, 
	title VARCHAR(255) not null,
	description text not null,
	open_date_time timestamp,
	close_date_time timestamp,
	create_date_time timestamp not null default now(),
	update_date_time timestamp
);

create table if not exists noti_deleted (
	id int4 primary key,
	title varchar(255),
	open_date_time timestamp,
	close_date_time timestamp,
	create_date_time timestamp,
	update_date_time timestamp,
	delete_date_time timestamp not null default now()
);

create table if not exists file_info (
	id serial primary key,
	file_id varchar(36) not null,
	user_email varchar(255) not null,
	origin_name varchar(255) not null,
	create_date_time timestamp not null default now()
);

insert into users (email) values ('ryeon@test.com');