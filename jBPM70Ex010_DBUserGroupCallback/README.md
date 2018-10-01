<property name="org.jbpm.ht.callback" value="db"/>

--- Example tables

create table MY_USER (
USERNAME varchar(255) not null,
primary key (USERNAME)
);

create table MY_ROLE (
ROLE_CODE varchar(255) not null,
primary key (ROLE_CODE)
);

create table ROLE_MAPPING (
USERNAME varchar(255) not null,
ROLE_CODE varchar(255) not null
);

insert into MY_USER (USERNAME) values ('myuser');
insert into MY_ROLE (ROLE_CODE) values ('IT');
insert into ROLE_MAPPING (USERNAME, ROLE_CODE) values ('myuser', 'IT');

insert into MY_USER (USERNAME) values ('john');
insert into MY_ROLE (ROLE_CODE) values ('PM');
insert into ROLE_MAPPING (USERNAME, ROLE_CODE) values ('john', 'PM');

insert into MY_USER (USERNAME) values ('mary');
insert into MY_ROLE (ROLE_CODE) values ('HR');
insert into ROLE_MAPPING (USERNAME, ROLE_CODE) values ('mary', 'HR');

insert into MY_USER (USERNAME) values ('Administrator');
insert into MY_ROLE (ROLE_CODE) values ('Administrators');
insert into ROLE_MAPPING (USERNAME, ROLE_CODE) values ('Administrator', 'Administrators');

insert into MY_USER (USERNAME) values ('rhpamAdmin');
insert into ROLE_MAPPING (USERNAME, ROLE_CODE) values ('rhpamAdmin', 'Administrators');
