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
COMMENT ON TABLE noti IS '공지글';
comment on column noti.id is 'PK'; 
comment on column noti.title is '공지 제목'; 
comment on column noti.content is '공지 내용'; 
comment on column noti.open_date_time is '공지 시작일시'; 
comment on column noti.close_date_time is '공지 종료일시'; 
comment on column noti.user_email is '등록자 email'; 
comment on column noti.create_date_time is '등록일시'; 
comment on column noti.update_date_time is '수정일시'; 

create table if not exists noti_file (
	noti_id int4 not null,
	file_id varchar(36) not null,
	constraint unq_noti_file unique(noti_id, file_id),
	FOREIGN KEY (noti_id) REFERENCES noti(id) on delete cascade
);
COMMENT ON TABLE noti_file IS '공지글-첨부파일 연관 관계';
comment on column noti_file.noti_id is '공지글 PK'; 
comment on column noti_file.file_id is '첨부파일 ID'; 

create table if not exists file_info (
	id serial primary key,
	file_id varchar(36) not null,
	user_email varchar(255) not null,
	origin_name varchar(255) not null,
	create_date_time timestamp not null default now()
);
COMMENT ON TABLE file_info IS '공지글-첨부파일 연관 관계';
comment on column file_info.id is '공지글 PK'; 
comment on column file_info.file_id is '파일 ID(UUID)'; 
comment on column file_info.user_email is '등록자 email'; 
comment on column file_info.origin_name is '원본 파일명'; 
comment on column file_info.create_date_time is '등록일시'; 