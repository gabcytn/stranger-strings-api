# Stranger Strings

A backend API powering a WebSocket-enabled (STOMP subprotocol) chat application for interest-based matchmaking.  
It’s similar in concept to Omegle but introduces a key enhancement: users may choose to remain **anonymous** or **authenticate**.  

- **Anonymous chat** → ephemeral messages, no persistence.  
- **Authenticated chat** → persisted messages with **username + profile picture**, retrievable through paginated message history.  

This project focuses purely on the backend API.

---

## ✨ Features

- 🔗 **WebSocket Messaging (STOMP)** — real-time communication between matched users.  
- 🧑‍🤝‍🧑 **Interest-based matchmaking** — users are paired with others sharing the same interest.  
- 🕵️ **Anonymous Mode** — quick chats without authentication (no persistence).  
- 🔑 **Authenticated Mode** —  
  - Messages are persisted in PostgreSQL.  
  - Includes user profile info (username, avatar).  
  - Supports paginated message history.  
- 🛡️ **Authentication** — JWT-based with short-lived access tokens (15 min) and refresh tokens for revalidation.  
- ⚡ **High-performance matching** — uses Redis hash sets as a multimap:  **interests->set_of_user_ids**
- ✅ **Strict segregation** — authenticated users are only matched with other authenticated users, and anonymous users only with other anonymous users.

---

## 🛠️ Tech Stack

- **Language/Framework:** Java (Spring Boot)  
- **Authentication:** JWT + Refresh Tokens  
- **Database:** PostgreSQL  
- **Cache / Matchmaking Engine:** Redis  

---

## 📐 Matching Technique

Redis is used to efficiently manage interest-based queues:

- Each interest maps to a **set of user IDs**.  
- Users are matched by popping from the set.  
- Ensures **O(1)** insertion and quick matching.  

Example structure in Redis:  
```txt
anonInterestSet:sports -> {user1, user2, user3}
authInterestSet:music  -> {user4, user5}
```

## 🚀 Future Roadmap

- **🗨️ Group chats** — multiple users in the same interest room.

- **🛡️ Moderation tools** — spam/abuse detection.

