package io.spring.sentiment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.app.tcp.client.processor.TcpClientProcessorConfiguration;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.handler.MessageProcessor;
import org.springframework.messaging.Message;
import org.springframework.messaging.SubscribableChannel;

@SpringBootApplication
@Import(TcpClientProcessorConfiguration.class)
@EnableBinding(SentimentApplication.SentimentSource.class)
public class SentimentApplication {
	private static Logger logger = LoggerFactory.getLogger(SentimentApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SentimentApplication.class, args);
	}

	@Autowired
	public SentimentSource sentimentSource;

	@Bean
	@ServiceActivator(inputChannel = Processor.OUTPUT, outputChannel = "sentiments")
	public MessageProcessor<String> sentimentTransformer() {
		return new MessageProcessor<String>() {
			@Override
			public String processMessage(Message<?> message) {
				logger.info("processing {}", message.getPayload());
				return new String((byte[]) message.getPayload());
			}
		};
	}

	public static interface SentimentSource {
		@Output
		SubscribableChannel sentiments();
	}
}
