package com.goldencat.chatapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class ChatappApplication {

	@Autowired
	private ChatService chatService;

	@Autowired
	private ChatComponentFactory factory;

	public static void main(String[] args) {
		SpringApplication.run(ChatappApplication.class, args);
	}

	@Bean
	public CommandLineRunner demonstration() {
		return args -> {
			try {
				System.out.println("\n=== Design Patterns Demonstration ===\n");

				// 1. Observer Pattern Demonstration
				System.out.println("1. Observer Pattern Demonstration:");
				// Create two observers
				ChatObserver observer1 = message -> System.out.println("Observer 1 received: " + message);
				ChatObserver observer2 = message -> System.out.println("Observer 2 received: " + message);

				// Add observers
				chatService.addObserver(observer1);
				chatService.addObserver(observer2);

				// Send messages
				System.out.println("\nSending message to all observers:");
				chatService.notifyObservers("Hello from Chat Room!");
				
				// Remove one observer
				System.out.println("\nRemoving Observer 2 and sending another message:");
				chatService.removeObserver(observer2);
				chatService.notifyObservers("This message is only for Observer 1");

				// 2. Factory Pattern Demonstration
				System.out.println("\n2. Factory Pattern Demonstration:");
				ChatComponent room = factory.createComponent("room", "room123");
				ChatComponent user = factory.createComponent("user", "user456");

				if (room instanceof ChatRoom && user instanceof ChatUser) {
					System.out.println("Created ChatRoom: " + ((ChatRoom)room).getRoomId());
					System.out.println("Created ChatUser: " + ((ChatUser)user).getUserId());
				}

				System.out.println("\n=== Demonstration Complete ===\n");
			} catch (Exception e) {
				System.err.println("Error during demonstration: " + e.getMessage());
			}
		};
	}

	// Observer Pattern
	@FunctionalInterface
	public interface ChatObserver {
		void update(String message);
	}

	// Factory Pattern
	public interface ChatComponent {
		// Marker interface
	}

	@Component
	public static class ChatRoom implements ChatComponent {
		private final String roomId;
		
		public ChatRoom() {
			this.roomId = "";
		}
		
		public ChatRoom(String roomId) {
			this.roomId = roomId;
		}

		public String getRoomId() {
			return roomId;
		}

		@Override
		public String toString() {
			return "ChatRoom{roomId='" + roomId + "'}";
		}
	}

	@Component
	public static class ChatUser implements ChatComponent {
		private final String userId;
		
		public ChatUser() {
			this.userId = "";
		}
		
		public ChatUser(String userId) {
			this.userId = userId;
		}

		public String getUserId() {
			return userId;
		}

		@Override
		public String toString() {
			return "ChatUser{userId='" + userId + "'}";
		}
	}

	@Component
	public static class ChatService {
		private final List<ChatObserver> observers = new ArrayList<>();

		public void addObserver(ChatObserver observer) {
			if (observer != null) {
				observers.add(observer);
				System.out.println("New observer added to chat service");
			}
		}

		public void removeObserver(ChatObserver observer) {
			if (observer != null && observers.remove(observer)) {
				System.out.println("Observer removed from chat service");
			}
		}

		public void notifyObservers(String message) {
			if (message != null) {
				observers.forEach(observer -> observer.update(message));
			}
		}
	}

	@Component
	public static class ChatComponentFactory {
		public ChatComponent createComponent(String type, String id) {
			if (type == null || id == null) {
				return null;
			}
			
			return switch (type.toLowerCase()) {
				case "room" -> new ChatRoom(id);
				case "user" -> new ChatUser(id);
				default -> null;
			};
		}
	}
}
