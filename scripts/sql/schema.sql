drop table zapas;
drop table kolo;
drop table hrac;

create table hrac (
 id int not null primary key,
 name varchar (255) not null,
 email varchar (255) not null
);
alter table hrac add constraint ui_hrac_name unique (name);
alter table hrac add constraint ui_hrac_email unique (email);

create table kolo (
 id int not null primary key,
 od date not null,
 do date not null
);

create table zapas (
 id int not null primary key,
 kolo int not null,
 hrac1 int not null,
 hrac2 int not null,
 skore char(3),
 body_hrac1 int,
 body_hrac2 int,
 ts timestamp
);
alter table zapas add constraint ui_zapas_hraci unique (hrac1, hrac2);
alter table zapas add constraint fk_kolo foreign key (kolo) references kolo(id);
alter table zapas add constraint fk_hrac1 foreign key (hrac1) references hrac(id);
alter table zapas add constraint fk_hrac2 foreign key (hrac2) references hrac(id);

alter table hrac add column aktivni boolean default true not null;