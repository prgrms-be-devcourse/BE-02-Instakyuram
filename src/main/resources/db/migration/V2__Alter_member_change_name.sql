ALTER TABLE token CHANGE refresh_token token VARCHAR(255);
ALTER TABLE token CHANGE user_id member_id BIGINT;