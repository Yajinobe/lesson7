package test;

import java.io.IOException;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
/**
 * 
 * @author Wangjiajie
 *
 */
public class RabbitMQ {
	static final String QUEUE_NAME = "hello";
	static ConnectionFactory factory;
	private static final String EXCHANGE_NAME = "RabbitChatRoom";
	public static void main(String[] args) {
		(new Thread(new Publisher())).start();
		(new Thread(new Rec())).start();
	}
	
	
	static class Publisher implements Runnable{
		@Override
		public void run() {
			try {
				factory = new ConnectionFactory();
				factory.setHost("localhost");
				factory.setHost("5672");
				Connection connection = factory.newConnection();
				Channel channel = connection.createChannel();
				channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
				channel.queueDeclare(QUEUE_NAME,true,false,false,null);
				String message = "Hello World";
				channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
				connection.close();
				channel.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	static class Rec implements Runnable{
		@Override
		public void run() {
			try {
				factory.setHost("localhost");
				Connection connection = factory.newConnection();
				Channel channel = connection.createChannel();
				channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
				channel.queueDeclare(QUEUE_NAME,false,false,false,null);
				Consumer consumer = new DefaultConsumer(channel) {
					@Override
					public void handleDelivery(String consumerTag, Envelope envelope,
							com.rabbitmq.client.AMQP.BasicProperties properties, byte[] body) throws IOException {
						// TODO Auto-generated method stub
						super.handleDelivery(consumerTag, envelope, properties, body);
					}
				};
				channel.basicConsume(QUEUE_NAME, true,consumer);
				connection.close();
				channel.close();
			} catch (Exception e) {
			}			
		}
	}
}
