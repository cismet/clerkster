
# --- !Ups

CREATE ALIAS MD5 FOR "org.apache.commons.codec.digest.DigestUtils.md5Hex(java.lang.String)";

create table account (
  name                      varchar not null,
  password                  varchar,
  api_password              varchar as MD5(password),
  constraint pk_account primary key (name))
;

create sequence account_seq;


# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists account;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists account_seq;

