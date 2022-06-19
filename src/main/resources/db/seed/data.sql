INSERT INTO member (username, name, email, phone_number) VALUES ('test00', '00', 'test00@gmail.com', '1234');
INSERT INTO member (username, name, email, phone_number) VALUES ('test01', '01', 'test01@gmail.com', '5678');
INSERT INTO member (username, name, email, phone_number) VALUES ('test02', '02', 'test02@gmail.com', '9012');
INSERT INTO member (username, name, email, phone_number) VALUES ('test03', '03', 'test03@gmail.com', '3456');
INSERT INTO member (username, name, email, phone_number) VALUES ('test04', '04', 'test04@gmail.com', '7890');

INSERT INTO post (content, member_id) VALUES ('p-content01', 1);
INSERT INTO post (content, member_id) VALUES ('p-content02', 1);
INSERT INTO post (content, member_id) VALUES ('p-content03', 2);
INSERT INTO post (content, member_id) VALUES ('p-content04', 2);
INSERT INTO post (content, member_id) VALUES ('p-content05', 3);
INSERT INTO post (content, member_id) VALUES ('p-content05', 4);
INSERT INTO post (content, member_id) VALUES ('p-content05', 5);

INSERT INTO post_like (post_id, member_id) VALUES (1, 2);
INSERT INTO post_like (post_id, member_id) VALUES (1, 3);
INSERT INTO post_like (post_id, member_id) VALUES (1, 4);
INSERT INTO post_like (post_id, member_id) VALUES (1, 5);
INSERT INTO post_like (post_id, member_id) VALUES (3, 1);
INSERT INTO post_like (post_id, member_id) VALUES (3, 4);
INSERT INTO post_like (post_id, member_id) VALUES (7, 1);

INSERT INTO comment (content, post_id, member_id) VALUES ('c-content01', 1, 2);
INSERT INTO comment (content, post_id, member_id) VALUES ('c-content02', 1, 3);
INSERT INTO comment (content, post_id, member_id) VALUES ('c-content03', 1, 4);
INSERT INTO comment (content, post_id, member_id) VALUES ('c-content03', 3, 5);