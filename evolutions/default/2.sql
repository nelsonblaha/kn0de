# --- !Ups

CREATE TABLE subscription (
    account_id integer NOT NULL references account(account_id),
    sub_id integer NOT NULL references sub(sub_id),
    sub_name varchar(80) NOT NULL,
    created_date timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL
);

# --- !Downs

DROP TABLE IF EXISTS subscriptions;
DROP TABLE subscription;
