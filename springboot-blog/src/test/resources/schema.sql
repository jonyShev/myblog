CREATE TABLE IF NOT EXISTS posts (
                    id IDENTITY PRIMARY KEY,
                    title VARCHAR(255) NOT NULL,
                    text TEXT,
                    image_path VARCHAR(255),
                    tags TEXT,
                    likes_count INTEGER DEFAULT 0
                );

CREATE TABLE IF NOT EXISTS comments (
                    id IDENTITY PRIMARY KEY,
                    post_id BIGINT NOT NULL,
                    text TEXT,
                    CONSTRAINT fk_comments_posts FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
                );