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

create table if not exists noti_file (
	noti_id int4 NOT NULL,
	file_url varchar(255) NOT NULL,
	CONSTRAINT fk_noti_file1 FOREIGN KEY (noti_id) REFERENCES noti(id)
);

insert into users (email) values ('ryeon@test.com');