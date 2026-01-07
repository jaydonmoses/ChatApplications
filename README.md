# Real-Time Chat Application

A WebSocket-based real-time chat application built with Spring Boot that enables multiple users to communicate instantly in a shared chatroom. Messages are broadcast to all connected clients using the STOMP protocol over WebSocket connections.

## Features

- Real-time bidirectional communication using WebSockets
- Simple message broker for broadcasting messages to all connected users
- SockJS fallback support for environments without native WebSocket support
- Bootstrap-based responsive UI
- Auto-scrolling chat window
- No authentication required (simple demo implementation)

## Architecture

### Message Flow
1. Client connects to WebSocket endpoint `/chat` using SockJS
2. Client subscribes to `/topic/messages` to receive broadcast messages
3. User sends message to `/app/sendMessage` with sender name and content
4. Server receives message and broadcasts it to all subscribers
5. All connected clients display the received message

### Core Components

**WebSocketConfig**: Configures STOMP endpoints and message broker with `/topic` prefix for broadcasting and `/app` prefix for application-destined messages.

**ChatController**: Handles incoming messages via `@MessageMapping` and broadcasts them using `@SendTo` annotation.

**ChatMessage**: Data model containing message ID, sender name, and content.

**chat.html**: Frontend interface with SockJS and STOMP.js for WebSocket communication.

## Tech Stack

### Backend (Server-Side)
- **Spring Boot 3.5.9** - Application framework
- **Spring WebSocket** - WebSocket support
- **Spring Messaging** - STOMP protocol implementation
- **Thymeleaf** - Server-side template engine
- **Lombok** - Reduces boilerplate code
- **Java 17** - Programming language

> **Note**: STOMP (Simple Text Oriented Message Protocol) is a messaging protocol that works over WebSocket, providing a simple format for clients to send and receive messages with support for subscriptions and broadcasting.

### Frontend
- **Thymeleaf** - Template rendering
- **JavaScript** - Client-side logic
- **SockJS** - WebSocket fallback/polyfill
- **STOMP.js** - STOMP protocol client library
- **HTML/CSS** - Structure and styling
- **Bootstrap 5** - UI framework

### Development Tools
- **Maven** - Build automation and dependency management
- **IntelliJ IDEA** - IDE

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Installation and Running

1. Clone the repository:
```bash
git clone <repository-url>
cd ChatApplications/app/app
```

2. Build the project:
```bash
./mvnw clean install
```

3. Run the application:
```bash
./mvnw spring-boot:run
```

4. Open your browser and navigate to:
```
http://localhost:8080/chat
```

5. Open multiple browser windows/tabs to simulate multiple users and start chatting.

## Project Structure

```
app/app/
├── src/main/java/com/chat/app/
│   ├── AppApplication.java           # Spring Boot entry point
│   ├── config/
│   │   └── WebSocketConfig.java      # WebSocket configuration
│   ├── controller/
│   │   └── ChatController.java       # Message handling controller
│   └── model/
│       └── ChatMessage.java          # Message data model
├── src/main/resources/
│   ├── application.properties        # Application configuration
│   └── templates/
│       └── chat.html                 # Chat UI
└── pom.xml                          # Maven dependencies
```

## Configuration

### WebSocket Endpoint
- **URL**: `/chat`
- **Allowed Origins**: `http://localhost:5173` (configured for CORS)
- **Protocol**: STOMP over SockJS

### Message Destinations
- **Send Message**: `/app/sendMessage`
- **Receive Messages**: `/topic/messages`

## API Endpoints

### HTTP Endpoints
- `GET /chat` - Serves the chat interface

### WebSocket Endpoints
- `CONNECT /chat` - Establishes WebSocket connection
- `SUBSCRIBE /topic/messages` - Subscribe to receive broadcast messages
- `SEND /app/sendMessage` - Send a message to be broadcast

## Limitations and Future Improvements

This is a basic demonstration implementation. Consider these enhancements for production use:

- Add user authentication and authorization
- Implement message persistence (database storage)
- Add private messaging between users
- Include user presence indicators (online/offline status)
- Add message history on initial connection
- Implement typing indicators
- Add file/image sharing capabilities
- Include message timestamps
- Add username validation and uniqueness checks
- Implement rate limiting to prevent spam

## Implementation Resources

### 1. Message Persistence with H2 Database

**Official Documentation:**
- [Spring Data JPA Reference](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [H2 Database Documentation](https://www.h2database.com/html/main.html)
- [Spring Boot with H2](https://spring.io/guides/gs/accessing-data-jpa/)

**Key Steps:**
1. Add dependencies to `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

2. Update `ChatMessage.java` with JPA annotations:
```java
@Entity
@Table(name = "messages")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String sender;
    
    @Column(nullable = false)
    private String content;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
```

3. Create a repository interface:
```java
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findTop50ByOrderByCreatedAtDesc();
}
```

4. Configure H2 in `application.properties`:
```properties
spring.datasource.url=jdbc:h2:mem:chatdb
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

5. Update `ChatController` to save and retrieve messages
6. Modify frontend to load message history on connect

**Tutorials:**
- [Baeldung: Spring Data JPA](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)
- [Spring Boot H2 Database Tutorial](https://www.baeldung.com/spring-boot-h2-database)

### 2. Multiple Chat Rooms

**Official Documentation:**
- [Spring WebSocket Dynamic Destinations](https://docs.spring.io/spring-framework/reference/web/websocket/stomp/destinations.html)
- [STOMP Path Variables](https://docs.spring.io/spring-framework/reference/web/websocket/stomp/handle-annotations.html)

**Key Concepts:**
- Use `@DestinationVariable` to extract room ID from paths
- Subscribe to room-specific topics: `/topic/room/{roomId}`
- Send to room-specific destinations: `/app/room/{roomId}/sendMessage`

**Example Code:**
```java
@MessageMapping("/room/{roomId}/sendMessage")
@SendTo("/topic/room/{roomId}")
public ChatMessage sendMessage(@DestinationVariable String roomId, 
                               ChatMessage message) {
    message.setRoomId(roomId);
    return messageRepository.save(message);
}
```

**Tutorials:**
- [Spring WebSocket with STOMP - Room Implementation](https://www.baeldung.com/spring-websockets-send-message-to-user)
- [Multi-room Chat Application](https://github.com/callicoder/spring-boot-websocket-chat-demo) - GitHub reference

**Frontend Changes:**
- Add room selection/creation UI
- Dynamically subscribe to `/topic/room/{roomId}`
- Include roomId in message payloads

### 3. Basic User Validation / Handshake Authentication

**Official Documentation:**
- [Spring WebSocket Security](https://docs.spring.io/spring-security/reference/servlet/integrations/websocket.html)
- [WebSocket HandshakeInterceptor](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/socket/server/HandshakeInterceptor.html)

**Key Concepts:**
- Implement `HandshakeInterceptor` to validate connections before upgrade
- Use `ChannelInterceptor` to authenticate STOMP messages
- Store user info in WebSocket session attributes

**Example Code:**
```java
@Configuration
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat")
                .setAllowedOrigins("http://localhost:5173")
                .addInterceptors(new HttpHandshakeInterceptor())
                .withSockJS();
    }
}

public class HttpHandshakeInterceptor implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, 
                                  ServerHttpResponse response,
                                  WebSocketHandler wsHandler, 
                                  Map<String, Object> attributes) {
        // Validate username from query params or headers
        String username = extractUsername(request);
        if (isValid(username)) {
            attributes.put("username", username);
            return true;
        }
        return false;
    }
}
```

**Tutorials:**
- [Spring WebSocket Authentication](https://www.baeldung.com/spring-security-websockets)
- [WebSocket Handshake Interceptor](https://www.toptal.com/java/stomp-spring-boot-websocket)

### Additional Learning Resources

**Video Tutorials:**
- [Spring Boot WebSocket Chat Application](https://www.youtube.com/results?search_query=spring+boot+websocket+chat) - YouTube search
- [Amigoscode Spring Boot WebSocket](https://www.youtube.com/c/amigoscode) - Channel with Spring tutorials

**Complete Example Projects:**
- [Spring WebSocket Chat Demo](https://github.com/callicoder/spring-boot-websocket-chat-demo)
- [Spring Boot Realtime Chat](https://github.com/eugenp/tutorials/tree/master/spring-websockets)

**Books:**
- "Pro Spring 5" - Chapter on WebSocket and Messaging
- "Spring in Action" - WebSocket integration chapters

### Migration Path to PostgreSQL

When ready to move from H2 to PostgreSQL:

1. Add PostgreSQL dependency:
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

2. Update `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/chatdb
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

**No code changes needed** - JPA abstracts database specifics!

**Resources:**
- [Spring Boot with PostgreSQL](https://www.baeldung.com/spring-boot-postgresql-docker)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

## License

This project is a demonstration application for learning Spring Boot WebSocket functionality.