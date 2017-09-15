/*
 * Copyright 2017 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package io.spring.sentiment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.app.tcp.client.processor.TcpClientProcessorConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.integration.annotation.BridgeFrom;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;

/**
 * A processor application that acts as a TCP client.
 *
 * @author David Turanski
 */
@Configuration
@DisableBinding
@Import(TcpClientProcessorConfiguration.class)
public class ServiceConfiguration {
	@Autowired
	private MessageChannel input;

	@Bean
	@BridgeFrom("output")
	public PollableChannel tcpOutput() {
		return new QueueChannel();
	}

	@Bean
	public SentimentService sentimentService() {
		return new SentimentService(input, tcpOutput());
	}

}
