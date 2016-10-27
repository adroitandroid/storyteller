CREATE DATABASE storyteller;
USE storyteller;

CREATE TABLE node_content_type (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, language_id INT NOT NULL, type VARCHAR(10) NOT NULL DEFAULT 'text') ENGINE=INNODB;

--CREATE TABLE story_nodes (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, date DATE NOT NULL, content TEXT, content_type_id INT, end_node BOOL NOT NULL DEFAULT FALSE, FOREIGN KEY (content_type_id) REFERENCES node_content_type(id)) ENGINE=INNODB;

CREATE TABLE story_prompt (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, prompt TEXT CHARACTER SET utf8, content_type_id INT DEFAULT 1, start_date DATE NOT NULL, end_date DATE NOT NULL, created_at DATETIME NOT NULL, updated_at DATETIME NOT NULL, soft_deleted TINYINT NOT NULL DEFAULT 0, FOREIGN KEY (content_type_id) REFERENCES node_content_type(id)) ENGINE=INNODB

CREATE TABLE story_snippet (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, story_prompt_id INT NOT NULL, parent_id INT NOT NULL, snippet TEXT CHARACTER SET utf8 NOT NULL, content_type_id INT DEFAULT 1, creator_user_id INT NOT NULL, end_node BOOL NOT NULL DEFAULT FALSE, traversal TEXT CHARACTER SET utf8 NOT NULL, created_at DATETIME NOT NULL, FOREIGN KEY (content_type_id) REFERENCES node_content_type(id), FOREIGN KEY (story_prompt_id) REFERENCES story_prompt(id), FOREIGN KEY (creator_user_id) REFERENCES user(id)) ENGINE=INNODB

CREATE TABLE user_snippet_vote (user_id INT NOT NULL, snippet_id INT NOT NULL, vote TINYINT NOT NULL, created_at DATETIME NOT NULL, updated_at DATETIME NOT NULL, PRIMARY KEY (user_id, snippet_id)) ENGINE=INNODB;

create table story (end_snippet_id INT NOT NULL, story_prompt_id INT NOT NULL, content_type_id INT DEFAULT 1, story MEDIUMTEXT CHARACTER SET utf8 NOT NULL, traversal TEXT CHARACTER SET utf8 NOT NULL, created_at DATETIME NOT NULL, updated_at DATETIME NOT NULL, FOREIGN KEY (story_prompt_id) REFERENCES story_prompt(id), FOREIGN KEY (content_type_id) REFERENCES node_content_type(id), PRIMARY KEY(end_snippet_id, content_type_id));

create table snippet_relation (ancestor_id INT NOT NULL, descendent_id INT NOT NULL, PRIMARY KEY (ancestor_id, descendent_id));

CREATE TABLE user_auth_type (id MEDIUMINT NOT NULL AUTO_INCREMENT PRIMARY KEY, type VARCHAR(10) NOT NULL) ENGINE=INNODB;

CREATE TABLE user (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, username VARCHAR(30) NOT NULL, auth_type_id MEDIUMINT NOT NULL, auth_user_id VARCHAR(255) NOT NULL, last_login DATETIME NOT NULL, FOREIGN KEY (auth_type_id) REFERENCES user_auth_type(id)) ENGINE=INNODB;

CREATE TABLE node_relations (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, ancestor_id INT NOT NULL, descendent_id INT NOT NULL, FOREIGN KEY (ancestor_id) REFERENCES story_nodes(id), FOREIGN KEY (descendent_id) REFERENCES story_nodes(id)) ENGINE=INNODB;

CREATE TABLE user_node_type (id TINYINT NOT NULL AUTO_INCREMENT PRIMARY KEY, type VARCHAR(10) NOT NULL) ENGINE=INNODB;

CREATE TABLE user_nodes (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, user_id INT NOT NULL, node_id INT NOT NULL, type_id TINYINT NOT NULL, soft_deleted BOOL, FOREIGN KEY(user_id) REFERENCES user(id), FOREIGN KEY(node_id) REFERENCES story_nodes(id), FOREIGN KEY (type_id) REFERENCES user_node_type(id)) ENGINE=INNODB;

CREATE TABLE user_session(user_id INT NOT NULL PRIMARY KEY, session_id TEXT NOT NULL, FOREIGN KEY(user_id) REFERENCES user(id)) ENGINE=INNODB;

INSERT INTO user_auth_type VALUES (DEFAULT, 'facebook'), (DEFAULT, 'google');

INSERT INTO user_node_type VALUES (DEFAULT, 'contributi'), (DEFAULT, 'bookmark');

INSERT INTO node_content_type VALUES (DEFAULT, 1, DEFAULT);
