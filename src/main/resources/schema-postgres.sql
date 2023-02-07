CREATE SEQUENCE IF NOT EXISTS EMOODJI_SEQ AS INTEGER MINVALUE 0 START WITH 0 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS EMOODJI (id INT NOT NULL, current INT NOT NULL, PRIMARY KEY (id));
INSERT INTO EMOODJI VALUES (0, nextval('EMOODJI_SEQ')) ON CONFLICT DO NOTHING;