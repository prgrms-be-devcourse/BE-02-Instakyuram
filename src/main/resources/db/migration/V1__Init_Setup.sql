DROP TABLE IF EXISTS comment_like;
DROP TABLE IF EXISTS comment;
DROP TABLE IF EXISTS profile_image;
DROP TABLE IF EXISTS post_tag;
DROP TABLE IF EXISTS tag;
DROP TABLE IF EXISTS post_like;
DROP TABLE IF EXISTS post;
DROP TABLE IF EXISTS profile_image;
DROP TABLE IF EXISTS follow;
DROP TABLE IF EXISTS token;
DROP TABLE IF EXISTS member;

CREATE TABLE member
(
    id           BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    username     VARCHAR(24) UNIQUE KEY,
    name         VARCHAR(16)  NOT NULL,
    password     VARCHAR(255) NOT NULL,
    phone_number VARCHAR(11)  NOT NULL,
    email        VARCHAR(255) UNIQUE KEY,
    introduction VARCHAR(255),
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    created_by   VARCHAR(255),
    updated_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_by   VARCHAR(255)
);

CREATE TABLE profile_image
(
    id                 BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    member_id          BIGINT       NOT NULL,
    original_file_name VARCHAR(255),
    server_file_name   VARCHAR(255),
    size               BIGINT       NOT NULL,
    path               VARCHAR(255) NOT NULL,
    created_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    created_by         VARCHAR(255),
    updated_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_by         VARCHAR(255),
    FOREIGN KEY (member_id) REFERENCES member (id)
);

CREATE TABLE token
(
    refresh_token VARCHAR(255) NOT NULL PRIMARY KEY,
    user_id      BIGINT,
    expiredAt    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    created_by   VARCHAR(255),
    updated_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_by   VARCHAR(255)
);

CREATE TABLE follow
(
    id         BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    member_id  BIGINT NOT NULL,
    target_id  BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    created_by VARCHAR(255),
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_by VARCHAR(255),
    FOREIGN KEY (member_id) REFERENCES member (id),
    FOREIGN KEY (target_id) REFERENCES member (id),
    UNIQUE KEY (member_id, target_id)
);

CREATE TABLE post
(
    id         BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    member_id  BIGINT NOT NULL,
    content    VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    created_by VARCHAR(255),
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_by VARCHAR(255),
    FOREIGN KEY (member_id) REFERENCES member (id)
);

CREATE TABLE post_like
(
    id         BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    member_id  BIGINT NOT NULL,
    post_id    BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    created_by VARCHAR(255),
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_by VARCHAR(255),
    FOREIGN KEY (member_id) REFERENCES member (id),
    FOREIGN KEY (post_id) REFERENCES post (id)
);

CREATE TABLE tag
(
    id         BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name       VARCHAR(255) UNIQUE KEY,
    count      BIGINT DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    created_by VARCHAR(255),
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_by VARCHAR(255)
);

CREATE TABLE post_tag
(
    id         BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    tag_id     BIGINT NOT NULL,
    post_id    BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    created_by VARCHAR(255),
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_by VARCHAR(255),
    FOREIGN KEY (tag_id) REFERENCES tag (id),
    FOREIGN KEY (post_id) REFERENCES post (id)
);

CREATE TABLE post_image
(
    id                 BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    post_id            BIGINT       NOT NULL,
    original_file_name VARCHAR(255),
    server_file_name   VARCHAR(255) UNIQUE KEY,
    size               BIGINT       NOT NULL,
    path               VARCHAR(255) NOT NULL,
    created_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    created_by         VARCHAR(255),
    updated_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_by         VARCHAR(255),
    FOREIGN KEY (post_id) REFERENCES post (id)
);

CREATE TABLE comment
(
    id         BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    post_id    BIGINT       NOT NULL,
    member_id  BIGINT       NOT NULL,
    content    VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    created_by VARCHAR(255),
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_by VARCHAR(255),
    FOREIGN KEY (post_id) REFERENCES post (id),
    FOREIGN KEY (member_id) REFERENCES member (id)
);

CREATE TABLE comment_like
(
    id         BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    comment_id BIGINT NOT NULL,
    member_id  BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    created_by VARCHAR(255),
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_by VARCHAR(255),
    FOREIGN KEY (comment_id) REFERENCES comment (id),
    FOREIGN KEY (member_id) REFERENCES member (id)
);