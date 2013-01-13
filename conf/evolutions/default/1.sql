# --- !Ups

CREATE TABLE account (
    account_id serial NOT NULL,
    name character varying(255) NOT NULL,
    email character varying(255),
    password character varying(255) NOT NULL,
    permission character varying(255) NOT NULL
);

CREATE TABLE comment (
    comment_id bigserial NOT NULL,
    comment_text text NOT NULL,
    posted_timestamp timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    posted_by_uid bigint NOT NULL,
    parent_id bigint,
    posted_to integer NOT NULL,
    score double precision NOT NULL,
    deleted boolean DEFAULT false NOT NULL
);

CREATE TABLE item (
    item_id bigserial NOT NULL,
    title character varying(255) NOT NULL,
    posted_by_uid bigint NOT NULL,
    posted_to integer NOT NULL,
    score double precision DEFAULT 1.0 NOT NULL,
    link character varying(255),
    content text,
    posted_timestamp timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE moderator (
    sub_id integer NOT NULL,
    account_id integer NOT NULL
);

CREATE TABLE sub (
    sub_id serial NOT NULL,
    name character varying(255) NOT NULL,
    description text NOT NULL,
    created_by bigint NOT NULL,
    created_timestamp timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    total_members integer DEFAULT 0 NOT NULL
);

ALTER TABLE account
    ADD CONSTRAINT account_account_id_key UNIQUE (account_id);

ALTER TABLE account
    ADD CONSTRAINT pk_account PRIMARY KEY (account_id);


ALTER TABLE comment
    ADD CONSTRAINT pk_comment PRIMARY KEY (comment_id);


ALTER TABLE item
    ADD CONSTRAINT pk_item PRIMARY KEY (item_id);


ALTER TABLE moderator
    ADD CONSTRAINT pk_moderator PRIMARY KEY (sub_id, account_id);


ALTER TABLE sub
    ADD CONSTRAINT pk_sub PRIMARY KEY (sub_id);


ALTER TABLE sub
    ADD CONSTRAINT sub_sub_id_key UNIQUE (sub_id);


ALTER TABLE comment
    ADD CONSTRAINT fk_comment_account FOREIGN KEY (posted_by_uid) REFERENCES account(account_id) ON UPDATE CASCADE ON DELETE CASCADE;


ALTER TABLE comment
    ADD CONSTRAINT fk_comment_comment FOREIGN KEY (parent_id) REFERENCES comment(comment_id);


ALTER TABLE comment
    ADD CONSTRAINT fk_comment_sub FOREIGN KEY (posted_to) REFERENCES sub(sub_id) ON UPDATE CASCADE ON DELETE CASCADE;


ALTER TABLE item
    ADD CONSTRAINT fk_item_account FOREIGN KEY (posted_by_uid) REFERENCES account(account_id) ON UPDATE CASCADE ON DELETE CASCADE;


ALTER TABLE item
    ADD CONSTRAINT fk_item_sub FOREIGN KEY (posted_to) REFERENCES sub(sub_id) ON UPDATE CASCADE ON DELETE CASCADE;


ALTER TABLE moderator
    ADD CONSTRAINT moderator_account_id_fkey FOREIGN KEY (account_id) REFERENCES account(account_id) ON UPDATE CASCADE ON DELETE CASCADE;


ALTER TABLE moderator
    ADD CONSTRAINT moderator_sub_id_fkey FOREIGN KEY (sub_id) REFERENCES sub(sub_id) ON UPDATE CASCADE ON DELETE CASCADE;


# --- !Downs

drop table if exists moderator;
drop table if exists comment;
drop table if exists item;
drop table if exists sub;
drop table if exists account;
