INSERT INTO member (email, name, password, phone_number, username)
    VALUES('test00@gmail.com','t-name00','$2a$10$V0N8N0EXFbvx8AxSkHsESu/ieMNRMpNCinub5sZRwHVV.7aOOAhf.','01010001000','test00');
INSERT INTO member (email, name, password, phone_number, username)
    VALUES('test01@gmail.com','t-name01','$2a$10$fglr/ju3beE30CEy9scFyu8Cn3.abMgbXImK60XtcjExLtfpxjbUO','01010001001','test01');
INSERT INTO member (email, name, password, phone_number, username)
    VALUES('test02@gmail.com','t-name02','$2a$10$QRMdyF22bqkkgBploiO3FeprK2RB4fpzXKqBQoMo2TFxjlmS03/We','01010001002','test02');
INSERT INTO member (email, name, password, phone_number, username)
    VALUES('test03@gmail.com','t-name03','$2a$10$EbAgwlMwuTchU.rBYbKv5.cb33blQOyOPk/AvqhT70UixdME0YMN2','01010001003','test03');
INSERT INTO member (email, name, password, phone_number, username)
    VALUES('test04@gmail.com','t-name04','$2a$10$mNsrrKaSZiwwODwhGR7PqejkI1aqOpEk48LujLUq98mArbVGQ5FeW','01010001004','test04');
INSERT INTO member (email, name, password, phone_number, username)
    VALUES('test05@gmail.com','t-name05','$2a$10$.uwnjP33kvL0OhapKrW9CO9dpzoav9igbSgenA0lQNfVwX4B.VJKu','01010001005','test05');

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