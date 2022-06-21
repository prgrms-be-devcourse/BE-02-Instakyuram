INSERT INTO member (username, name, email, phone_number,created_at, created_by, updated_at, updated_by) VALUES ('test00', '00', 'test00@gmail.com', '1234',now(),'test00',now(),'test00');
INSERT INTO member (username, name, email, phone_number,created_at, created_by, updated_at, updated_by) VALUES ('test01', '00', 'test01@gmail.com', '5678',now(),'test01',now(),'test01');
INSERT INTO member (username, name, email, phone_number,created_at, created_by, updated_at, updated_by) VALUES ('test02', '00', 'test02@gmail.com', '9012',now(),'test02',now(),'test02');
INSERT INTO member (username, name, email, phone_number,created_at, created_by, updated_at, updated_by) VALUES ('test03', '00', 'test03@gmail.com', '3456',now(),'test03',now(),'test03');
INSERT INTO member (username, name, email, phone_number,created_at, created_by, updated_at, updated_by) VALUES ('test04', '00', 'test04@gmail.com', '7890',now(),'test04',now(),'test04');

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