# --- !Ups

CREATE TABLE subscription (
    account_id bigint NOT NULL,
    sub_id bigint NOT NULL,
    subscribed_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    constraint fk_subscription_account foreign key (account_id)
      references account (account_id) on update cascade on delete cascade,
    constraint fk_subscription_sub foreign key (sub_id)
      references sub (sub_id) on update cascade on delete cascade
);

CREATE INDEX idx_subscription_account ON subscription(account_id); 

# --- !Downs

DROP INDEX IF EXISTS idx_subscription_account;

DROP TABLE IF EXISTS subscription;
