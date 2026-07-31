# MyRedis

A Redis-inspired in-memory key-value database built from scratch in Java.

MyRedis is an educational backend systems project that implements core Redis concepts including the RESP protocol, TCP networking, persistence, replication, Pub/Sub messaging, expiration (TTL), and a Java client SDK. The project focuses on understanding how distributed in-memory databases work internally rather than simply using Redis as a dependency.

---

# Features

## Networking

* TCP server using Java NIO
* Multiple concurrent client connections
* RESP (Redis Serialization Protocol) implementation
* Custom RESP parser and encoder
* Java SDK for communicating with the server

---

## Supported Data Types

### Strings

* SET
* GET
* DEL

### Hashes

* HSET
* HGET

### Lists

* LPUSH
* RPUSH
* LPOP
* RPOP
* LRANGE

---

## Expiration (TTL)

Supports Redis-style key expiration.

Commands:

* EXPIRE
* TTL

Features:

* Lazy expiration during key access
* Background expiration scheduler
* Automatic cleanup of expired keys

---

## Persistence

Append Only File (AOF)

Every write command is stored in RESP format.

Example:

```text
*3
$3
SET
$4
name
$7
tabitha
```

Server startup automatically replays the AOF to rebuild the in-memory database.

---

## Replication

Supports Master → Replica replication.

Features:

* Replica handshake
* Replica registration
* RESP command broadcasting
* Shared command execution pipeline
* Replica maintains an identical in-memory database

Architecture:

```
Client
   │
   ▼
Master Server
   │
   ├── Execute Command
   ├── Append to AOF
   └── Broadcast RESP
             │
             ▼
      Replica Server
             │
             ▼
     CommandExecuter
             │
             ▼
      MemoryDatabase
```

---

## Pub/Sub

Supports publish-subscribe messaging.

Components:

* PubSubManager
* Subscriber registration
* Channel-based publishing
* Message broadcasting

---

## Java SDK

The project includes a client SDK for communicating with MyRedis.

Example:

```java
MyRedisClient client = new MyRedisClient("localhost", 6378);

client.connect();

client.set("name", "tabitha");

System.out.println(client.get("name"));

client.expire("name", 30);

System.out.println(client.ttl("name"));

client.close();
```

---

# Project Structure

```
src
├── client
│   ├── ClientConnection
│   ├── ClientEncoder
│   ├── ClientDecoder
│   └── MyRedisClient
├──server
   ├── command
   ├── commands
   ├── config
   ├── executer
   ├── network
   ├── persistence
   ├── protocol
   ├── pubsub
   ├── replication
   ├── scheduler
   ├── server
   ├── storage
   └── response
```

---

# Internal Architecture

```
                  Client SDK
                       │
                       ▼
                TCP Connection
                       │
                       ▼
                 ConnectionManager
                       │
                       ▼
                  ClientHandler
                       │
                       ▼
                  RESP Parser
                       │
                       ▼
                CommandExecuter
                       │
        ┌──────────────┼──────────────┐
        │              │              │
        ▼              ▼              ▼
 MemoryDatabase   AppendOnlyFile  ReplicaManager
        │                             │
        │                             ▼
        │                      Replica Servers
        │
        ▼
ExpirationScheduler
```

---

# Commands

| Command   | Status |
| --------- | ------ |
| PING      | ✅      |
| SET       | ✅      |
| GET       | ✅      |
| DEL       | ✅      |
| HSET      | ✅      |
| HGET      | ✅      |
| LPUSH     | ✅      |
| RPUSH     | ✅      |
| LPOP      | ✅      |
| RPOP      | ✅      |
| LRANGE    | ✅      |
| EXPIRE    | ✅      |
| TTL       | ✅      |
| PUB       | ✅      |
| SUBSCRIBE | ✅      |

---

# Persistence Flow

```
SET name tabitha

        │
        ▼

CommandExecuter

        │
        ▼

MemoryDatabase

        │
        ▼

AppendOnlyFile

        │
        ▼

appendonly.aof
```

---

# Replication Flow

```
Client

    │
    ▼

Master

    │

Execute Command

    │

Broadcast RESP

    │

Replica

    │

Execute Command

    │

Replica MemoryDatabase
```

---

# Technologies

* Java 21
* Java NIO
* ConcurrentHashMap
* TCP Sockets
* RESP Protocol
* Multithreading

---

# Learning Outcomes

This project explores the internal implementation of distributed in-memory databases and backend infrastructure concepts including:

* TCP networking
* Custom application protocols
* Command parsing
* Thread-safe in-memory storage
* Append-only persistence
* Recovery mechanisms
* Master–Replica replication
* Publish–Subscribe messaging
* Background scheduling
* Client SDK design
* Distributed systems fundamentals

---

# Roadmap

Planned features:

* SETNX
* Distributed locks
* INCR
* EXISTS
* Transactions (MULTI / EXEC)
* RDB Snapshots
* AOF Rewrite
* Memory eviction policies (LRU/LFU)
* INFO command
* CLIENT LIST
* Additional Redis-compatible commands
