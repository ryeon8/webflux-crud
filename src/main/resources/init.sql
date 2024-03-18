create table if not exists users (
  id serial primary key,
  email varchar(255) not null
);

create table if not exists noti (
	id serial primary key, 
	title VARCHAR(255) not null,
	content text not null,
	open_date_time timestamp,
	close_date_time timestamp,
	user_email varchar(255) not null,
	create_date_time timestamp not null default now(),
	update_date_time timestamp
);

create table if not exists noti_file (
	noti_id int4 not null,
	file_id varchar(36) not null,
	constraint unq_noti_file unique(noti_id, file_id),
	FOREIGN KEY (noti_id) REFERENCES noti(id) on delete cascade
);

create table if not exists file_info (
	id serial primary key,
	file_id varchar(36) not null,
	user_email varchar(255) not null,
	origin_name varchar(255) not null,
	create_date_time timestamp not null default now()
);

insert into users (email) values ('ryeon@test.com');