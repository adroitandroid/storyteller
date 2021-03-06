CREATE DATABASE storyteller;
USE storyteller;

CREATE TABLE node_content_type (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, language_id INT NOT NULL, type VARCHAR(10) NOT NULL DEFAULT 'text') ENGINE=INNODB;

CREATE TABLE story_prompt (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, prompt TEXT CHARACTER SET utf8, content_type_id INT DEFAULT 1, start_date DATE NOT NULL, end_date DATE NOT NULL, created_at DATETIME NOT NULL, updated_at DATETIME NOT NULL, soft_deleted TINYINT NOT NULL DEFAULT 0, FOREIGN KEY (content_type_id) REFERENCES node_content_type(id)) ENGINE=INNODB;

CREATE TABLE story_snippet (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, story_prompt_id INT NOT NULL, parent_id INT NOT NULL, snippet TEXT CHARACTER SET utf8 NOT NULL, content_type_id INT DEFAULT 1, creator_user_id INT NOT NULL, end_node BOOL NOT NULL DEFAULT FALSE, traversal TEXT CHARACTER SET utf8 NOT NULL, created_at DATETIME NOT NULL, FOREIGN KEY (content_type_id) REFERENCES node_content_type(id), FOREIGN KEY (story_prompt_id) REFERENCES story_prompt(id), FOREIGN KEY (creator_user_id) REFERENCES user(id)) ENGINE=INNODB

CREATE TABLE user_snippet_vote (user_id INT NOT NULL, snippet_id INT NOT NULL, vote TINYINT NOT NULL, created_at DATETIME NOT NULL, updated_at DATETIME NOT NULL, PRIMARY KEY (user_id, snippet_id)) ENGINE=INNODB;

CREATE TABLE story (end_snippet_id INT NOT NULL, story_prompt_id INT NOT NULL, content_type_id INT DEFAULT 1, story MEDIUMTEXT CHARACTER SET utf8 NOT NULL, traversal TEXT CHARACTER SET utf8 NOT NULL, created_at DATETIME NOT NULL, updated_at DATETIME NOT NULL, FOREIGN KEY (story_prompt_id) REFERENCES story_prompt(id), FOREIGN KEY (content_type_id) REFERENCES node_content_type(id), PRIMARY KEY(end_snippet_id, content_type_id)) ENGINE=INNODB;

CREATE TABLE snippet_relation (ancestor_id INT NOT NULL, descendent_id INT NOT NULL, PRIMARY KEY (ancestor_id, descendent_id)) ENGINE=INNODB;

CREATE TABLE user_snippet_relation (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, user_id INT NOT NULL, story_prompt_id INT NOT NULL, snippet_id INT NOT NULL, relation_type VARCHAR(20) CHARACTER SET utf8 NOT NULL, created_at DATETIME NOT NULL, updated_at DATETIME NOT NULL, soft_deleted TINYINT NOT NULL DEFAULT 0, FOREIGN KEY (user_id) REFERENCES user(id), FOREIGN KEY (story_prompt_id) REFERENCES story_prompt(id), FOREIGN KEY (snippet_id) REFERENCES story_snippet(id), UNIQUE KEY `user_snippet_key`(user_id, snippet_id)) ENGINE=INNODB;

CREATE TABLE user_auth_type (id MEDIUMINT NOT NULL AUTO_INCREMENT PRIMARY KEY, type VARCHAR(10) NOT NULL) ENGINE=INNODB;

CREATE TABLE user (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, username VARCHAR(30) NOT NULL, auth_type_id MEDIUMINT NOT NULL, auth_user_id VARCHAR(255) NOT NULL, last_login DATETIME NOT NULL, FOREIGN KEY (auth_type_id) REFERENCES user_auth_type(id)) ENGINE=INNODB;

CREATE TABLE node_relations (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, ancestor_id INT NOT NULL, descendent_id INT NOT NULL, FOREIGN KEY (ancestor_id) REFERENCES story_nodes(id), FOREIGN KEY (descendent_id) REFERENCES story_nodes(id)) ENGINE=INNODB;

CREATE TABLE user_node_type (id TINYINT NOT NULL AUTO_INCREMENT PRIMARY KEY, type VARCHAR(10) NOT NULL) ENGINE=INNODB;

CREATE TABLE user_nodes (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, user_id INT NOT NULL, node_id INT NOT NULL, type_id TINYINT NOT NULL, soft_deleted BOOL, FOREIGN KEY(user_id) REFERENCES user(id), FOREIGN KEY(node_id) REFERENCES story_nodes(id), FOREIGN KEY (type_id) REFERENCES user_node_type(id)) ENGINE=INNODB;

CREATE TABLE user_session(user_id INT NOT NULL PRIMARY KEY, session_id TEXT NOT NULL, FOREIGN KEY(user_id) REFERENCES user(id)) ENGINE=INNODB;

INSERT INTO user_auth_type VALUES (DEFAULT, 'facebook'), (DEFAULT, 'google');

INSERT INTO user_node_type VALUES (DEFAULT, 'contributi'), (DEFAULT, 'bookmark');

INSERT INTO node_content_type VALUES (DEFAULT, 1, DEFAULT);

------------------ for vver

CREATE TABLE auth_type (id MEDIUMINT NOT NULL AUTO_INCREMENT PRIMARY KEY, type VARCHAR(10) NOT NULL) ENGINE=INNODB;

CREATE TABLE user (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, username VARCHAR(30), description VARCHAR(160), auth_type_id MEDIUMINT NOT NULL, auth_user_id VARCHAR(255) NOT NULL, last_active DATETIME NOT NULL, created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (auth_type_id) REFERENCES auth_type(id)) ENGINE=INNODB;

INSERT INTO auth_type VALUES (DEFAULT, 'facebook'), (DEFAULT, 'phone'), (DEFAULT, 'google');

CREATE TABLE user_relations (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, user_id INT NOT NULL, follower_user_id INT NOT NULL, updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, soft_deleted TINYINT NOT NULL DEFAULT 0, FOREIGN KEY (user_id) REFERENCES user(`id`), FOREIGN KEY (follower_user_id) REFERENCES user(id)) ENGINE=INNODB;

CREATE TABLE user_stats (user_id INT NOT NULL PRIMARY KEY, num_snippets INT NOT NULL DEFAULT 0, num_followers INT NOT NULL DEFAULT 0, net_votes INT NOT NULL DEFAULT 0, FOREIGN KEY (user_id) REFERENCES user(id), INDEX `vote_sum` (`vote_sum`), INDEX `num_votes` (`num_votes`)) ENGINE=INNODB;

CREATE TABLE snippet (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, content VARCHAR(160) NOT NULL, author_user_id INT NOT NULL, parent_snippet_id INT NOT NULL DEFAULT -1, root_snippet_id INT, created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (`author_user_id`) REFERENCES user(id), KEY(parent_snippet_id), KEY(root_snippet_id)) ENGINE=INNODB;

CREATE TABLE snippet_stats (snippet_id INT NOT NULL PRIMARY KEY, num_votes INT NOT NULL DEFAULT 0, vote_sum INT NOT NULL DEFAULT 0, num_children INT NOT NULL DEFAULT 0, updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (snippet_id) REFERENCES snippet(id)) ENGINE=INNODB;

CREATE TABLE user_bookmarks (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, user_id INT NOT NULL, snippet_id INT NOT NULL, updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, soft_deleted TINYINT NOT NULL DEFAULT 0, FOREIGN KEY (user_id) REFERENCES user(id), FOREIGN KEY (snippet_id) REFERENCES snippet(`id`)) ENGINE=INNODB;

CREATE TABLE user_votes (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, user_id INT NOT NULL, snippet_id INT NOT NULL, vote TINYINT NOT NULL DEFAULT 0, updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (user_id) REFERENCES user(id), FOREIGN KEY (snippet_id) REFERENCES snippet(id)) ENGINE=INNODB;

------------------ for v_quora

CREATE TABLE prompt (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, content TEXT CHARACTER SET utf8 NOT NULL, created_at DATETIME NOT NULL, updated_at DATETIME NOT NULL, soft_deleted TINYINT NOT NULL DEFAULT 0);

CREATE TABLE story_summary (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, title TEXT CHARACTER SET utf8 NOT NULL, created_at DATETIME NOT NULL, updated_at DATETIME NOT NULL);

CREATE TABLE prompt_stories (story_id INT NOT NULL, prompt_id INT NOT NULL, FOREIGN KEY (story_id) REFERENCES story_summary(id), FOREIGN KEY (prompt_id) REFERENCES prompt(id));

CREATE TABLE chapter_detail (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, content MEDIUMTEXT CHARACTER SET utf8 NOT NULL);

CREATE TABLE chapter_summary (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, parent_id INT, detail_id INT, title TEXT CHARACTER SET utf8 NOT NULL, description TEXT CHARACTER SET utf8 NOT NULL, ends_story TINYINT, status TINYINT(4), author_user_id INT NOT NULL,  created_at DATETIME NOT NULL, updated_at DATETIME NOT NULL, soft_deleted TINYINT NOT NULL DEFAULT 0, FOREIGN KEY (author_user_id) REFERENCES user(id), FOREIGN KEY (detail_id) REFERENCES chapter_detail(id));

CREATE TABLE story_chapters (story_id INT NOT NULL, chapter_id INT NOT NULL, FOREIGN KEY (story_id) REFERENCES story_summary(id), FOREIGN KEY (chapter_id) REFERENCES `chapter_summary`(id));

CREATE TABLE story_stats (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, story_id INT NOT NULL, num_completed INT NOT NULL DEFAULT 0, num_likes INT NOT NULL DEFAULT 0, num_reads INT NOT NULL DEFAULT 0, FOREIGN KEY (story_id) REFERENCES story_summary(id), UNIQUE KEY story_id_key (`story_id`));

CREATE TABLE genre (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, genre_name VARCHAR(30) NOT NULL);

CREATE TABLE story_genres (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, genre_id INT NOT NULL, story_id INT NOT NULL, count_completed INT NOT NULL, FOREIGN KEY (story_id) REFERENCES story_summary(id), FOREIGN KEY (genre_id) REFERENCES genre(id), UNIQUE KEY `story_genre_key`(story_id, genre_id)));

CREATE TABLE user_story_relation (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, user_id INT NOT NULL, story_id INT NOT NULL, relation_id INT NOT NULL, created_at DATETIME NOT NULL, updated_at DATETIME NOT NULL, soft_deleted TINYINT NOT NULL DEFAULT 0, FOREIGN KEY (story_id) REFERENCES story_summary(id), FOREIGN KEY (user_id) REFERENCES user(id), UNIQUE KEY `user_story_relation_key`(story_id, user_id, relation_id));

CREATE TABLE chapter_stats (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, chapter_id INT NOT NULL, num_bookmarks INT NOT NULL DEFAULT 0, num_reads INT NOT NULL DEFAULT 0, FOREIGN KEY (chapter_id) REFERENCES chapter_summary(id), UNIQUE KEY chapter_id_key (`chapter_id`));

CREATE TABLE user_chapter_relation (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, user_id INT NOT NULL, chapter_id INT NOT NULL, relation_id INT NOT NULL, created_at DATETIME NOT NULL, updated_at DATETIME NOT NULL, soft_deleted TINYINT NOT NULL DEFAULT 0, FOREIGN KEY (chapter_id) REFERENCES chapter_summary(id), FOREIGN KEY (user_id) REFERENCES user(id), UNIQUE KEY `user_chapter_relation_key`(chapter_id, user_id, relation_id));

CREATE TABLE version (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, platform VARCHAR(30) NOT NULL, version_latest INT NOT NULL, version_min_supported INT NOT NULL, created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP );

CREATE TABLE user_details (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, user_id INT NOT NULL, fcm_token TEXT NOT NULL CHARACTER SET utf8 NOT NULL, created_at DATETIME NOT NULL, updated_at DATETIME NOT NULL, soft_deleted TINYINT NOT NULL DEFAULT 0, FOREIGN KEY (user_id) REFERENCES user(id), UNIQUE KEY `user_key`(user_id));
