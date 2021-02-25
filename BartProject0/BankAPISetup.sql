-- Tables should be named in the singular
create table client(
client_id int primary key generated always as identity, 
full_name varchar(256) not null
);

create table account (
account_id int primary key generated always as identity,
client_id int not null,
amount real default 0
);

alter table account add foreign key (client_id) references client(client_id) on delete cascade;
alter table account add constraint amount_not_negative check (amount >= 0);

-- All good databases should have unique records
-- Every table should have a primary key

-- DQL (Data Query Language)
-- select { columns you want} from {table} where {filters what rows you get - boolean condition}

-- DML (Data Manipulation Language) (INSERT, DELETE, UPDATE, SET)
-- insert into {table} ({columns}) values ({values})
-- delete from {table} where {boolean condition}
-- update {table} set {columns = new values} where {boolean condition}

-- where clauses not strictly necessary for delete and update, 
-- but it will update or delete every row if not included

-- DDL (Data Definition Language) CREATE, DROP, ALTER

-- Making foreign keys, at the end add:
-- constraint fk_{child table}_{parent table} foreign key ({column in child table}) references {parent table}({column in parent table that is referenced})