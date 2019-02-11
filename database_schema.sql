-- PRAGMA encoding="UTF-8";

-- SQLite Syntax.

-- User table
CREATE TABLE user (
  `id` VARCHAR(32) PRIMARY KEY NOT NULL,
  `username` VARCHAR(32) NOT NULL UNIQUE,
  `email` VARCHAR(64) NOT NULL UNIQUE,
  `password` CHAR(64) NOT NULL,
  `profile_photo` VARCHAR(255) NULL,
  `bio` TEXT NULL
);

-- Active user table
CREATE TABLE active_user (
  `id` VARCHAR(32) PRIMARY KEY NOT NULL,
  `user` VARCHAR(32) NOT NULL,
  `login_time` INTEGER NOT NULL,
  `session_key` VARCHAR(32) NOT NULL,
  FOREIGN KEY (`user`) REFERENCES user(`id`)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

-- Friend table
CREATE TABLE friend (
  `id` VARCHAR(32) PRIMARY KEY NOT NULL,
  `user1` VARCHAR(32) NOT NULL,
  `user2` VARCHAR(32) NOT NULL,
  UNIQUE(`user1`, `user2`),
  FOREIGN KEY (`user1`) REFERENCES user(`id`)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  FOREIGN KEY (`user2`) REFERENCES user(`id`)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

-- Song genre table
CREATE TABLE genre (
  `id` VARCHAR(32) PRIMARY KEY NOT NULL,
  `name` VARCHAR(32) NOT NULL
);

-- Song table
CREATE TABLE song (
  `id` VARCHAR(32) PRIMARY KEY NOT NULL,
  `resource_location` VARCHAR(255) NOT NULL,
  `name` VARCHAR(64) NOT NULL,
  `genre` VARCHAR(32) NOT NULL,
  `length` INTEGER NOT NULL,
  `artist` VARCHAR(64) NOT NULL,
  `album` VARCHAR(64) NULL,
  `user` VARCHAR(32) NOT NULL,
  `num_downloads` INTEGER NOT NULL DEFAULT 0,
  FOREIGN KEY (`genre`) REFERENCES genre(`id`)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  FOREIGN KEY (`user`) REFERENCES user(`id`)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

-- Post table
CREATE TABLE post (
  `id` VARCHAR(32) PRIMARY KEY NOT NULL,
  `title` VARCHAR(100) NOT NULL,
  `content` TEXT NOT NULL,
  `user` VARCHAR(32) NOT NULL,
  `time` INTEGER NOT NULL,
  FOREIGN KEY (`user`) REFERENCES user(`id`)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

-- Post share table
CREATE TABLE post_share (
  `id` VARCHAR(32) PRIMARY KEY NOT NULL,
  `post` VARCHAR(32) NOT NULL,
  `user` VARCHAR(32) NOT NULL,
  FOREIGN KEY (`post`) REFERENCES post(`id`)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  FOREIGN KEY (`user`) REFERENCES user(`id`)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

-- Message table
CREATE TABLE message (
  `id` VARCHAR(32) PRIMARY KEY NOT NULL,
  `sender` VARCHAR(32) NOT NULL,
  `recipient` VARCHAR(32) NOT NULL,
  `time_sent` INTEGER NOT NULL,
  `data` TEXT NOT NULL,
  `type` VARCHAR(16) NOT NULL DEFAULT 'text',
  FOREIGN KEY (`sender`) REFERENCES user(`id`)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  FOREIGN KEY (`recipient`) REFERENCES user(`id`)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

-- Friend Request table
CREATE TABLE friend_request (
  `id` VARCHAR(32) PRIMARY KEY NOT NULL,
  `sender` VARCHAR(32) NOT NULL,
  `recipient` VARCHAR(32) NOT NULL,
  `time_sent` INTEGER NOT NULL,
  FOREIGN KEY (`sender`) REFERENCES user(`id`)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  FOREIGN KEY (`recipient`) REFERENCES user(`id`)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

-- Notification table (experimental)
CREATE TABLE notification (
  `id` VARCHAR(32) PRIMARY KEY NOT NULL,
  `user` VARCHAR(32) NOT NULL,
  `time` INTEGER NOT NULL,
  `title` VARCHAR(100) NOT NULL,
  `content` TEXT NOT NULL,
  `type` VARCHAR(16) NOT NULL DEFAULT 'generic',
  FOREIGN KEY (`user`) REFERENCES user(`id`)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);


--- FOR CHAT SERVER ONLY
-- Message table
CREATE TABLE message (
  `id` VARCHAR(32) PRIMARY KEY NOT NULL,
  `sender` VARCHAR(32) NOT NULL,
  `recipient` VARCHAR(32) NOT NULL,
  `time_sent` INTEGER NOT NULL,
  `data` TEXT NOT NULL,
  `type` VARCHAR(16) NOT NULL DEFAULT 'text'
);
