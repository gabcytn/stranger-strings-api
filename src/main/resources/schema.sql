CREATE TABLE users (
	id UUID NOT NULL,
	username VARCHAR(100) NOT NULL,
	email VARCHAR(255) NOT NULL,
	email_verified_at TIMESTAMP,
	password VARCHAR(75) NOT NULL,
	profile_pic VARCHAR (255) NOT NULL
		DEFAULT 'default.png',
	CONSTRAINT users_pk PRIMARY KEY (id),
	CONSTRAINT users_username_ak UNIQUE (username),
	CONSTRAINT users_email_ak UNIQUE (email)
);

-- CREATE TABLE conversations (
-- 	id SERIAL NOT NULL,
-- 	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
-- 	request BOOLEAN NOT NULL,
-- 	CONSTRAINT conversations_pk PRIMARY KEY (id)
-- );
--
-- CREATE TABLE conversation_members (
-- 	conversation_id INT NOT NULL,
-- 	user_id UUID NOT NULL,
-- 	CONSTRAINT conversation_members_pk
-- 		PRIMARY KEY(conversation_id, user_id),
-- 	CONSTRAINT conversation_id_fk FOREIGN KEY (conversation_id)
-- 	    REFERENCES conversations(id),
-- 	CONSTRAINT user_id_fk FOREIGN KEY (user_id)
-- 	    REFERENCES users(id)
-- );
--
-- CREATE TABLE messages (
-- 	id SERIAL NOT NULL,
-- 	conversation_id INT NOT NULL,
-- 	sender_id UUID NOT NULL,
-- 	message TEXT NOT NULL,
-- 	sent_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
-- 	CONSTRAINT messages_pk PRIMARY KEY(id),
-- 	CONSTRAINT messages_conversation_id_fk FOREIGN KEY (conversation_id)
-- 	    REFERENCES conversations(id),
-- 	CONSTRAINT messages_sender_id FOREIGN KEY (sender_id)
-- 	    REFERENCES users(id)
-- );
--
-- CREATE TABLE blocks (
-- 	blocked_id UUID NOT NULL,
-- 	blocker_id UUID NOT NULL,
-- 	CONSTRAINT blocks_pk PRIMARY KEY (blocked_id, blocker_id),
-- 	CONSTRAINT blocked_fk FOREIGN KEY (blocked)
-- 		REFERENCES users(id),
-- 	CONSTRAINT blocker_fk FOREIGN KEY (blocker)
-- 		REFERENCES users(id),
-- 	CONSTRAINT blocks_chk CHECK(blocked <> blocker)
-- );
--
-- CREATE INDEX blocker_idx
-- ON blocks(blocker_id);
--
-- CREATE INDEX blocked_idx
-- ON blocks(blocked_id);
--
-- CREATE INDEX cm_user_idx
-- ON conversation_members(user_id);
--
-- CREATE INDEX cm_conversation_idx
-- ON conversation_members(conversation_id);
--
-- CREATE INDEX messages_conversation_idx
-- ON messages(conversation_id);
--
-- CREATE INDEX messages_sender_idx
-- ON messages(sender_id);

