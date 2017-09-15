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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.app.tcp.EncoderDecoderFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.ip.config.TcpConnectionFactoryFactoryBean;
import org.springframework.integration.ip.tcp.TcpReceivingChannelAdapter;
import org.springframework.integration.ip.tcp.TcpSendingMessageHandler;
import org.springframework.integration.ip.tcp.connection.AbstractConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpMessageMapper;
import org.springframework.integration.ip.tcp.serializer.AbstractByteArraySerializer;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * A processor application that acts as a TCP client.
 *
 * @author Ilayaperumal Gopinathan
 * @author Gary Russell
 */
@Configuration
@EnableConfigurationProperties(TcpClientProcessorProperties.class)
public class TcpClientProcessorConfiguration {

	@Autowired
	private TcpClientProcessorProperties properties;

	@Bean
	public SubscribableChannel input() {
		return new DirectChannel();
	}

	@Bean
	public PollableChannel output() {
		return new QueueChannel();
	}

	@Bean
	public MessageChannel tcpOutput() {
		return new DirectChannel();
	}

	@Bean
	public SentimentService sentimentService() {
		return new SentimentService(input(), output());
	}

	@Bean
	public TcpReceivingChannelAdapter adapter(
		@Qualifier("tcpClientConnectionFactory") AbstractConnectionFactory connectionFactory) {
		TcpReceivingChannelAdapter adapter = new TcpReceivingChannelAdapter();
		adapter.setConnectionFactory(connectionFactory);
		adapter.setClientMode(true);
		adapter.setRetryInterval(this.properties.getRetryInterval());
		adapter.setOutputChannel(output());
		return adapter;
	}

	@Bean
	@ServiceActivator(inputChannel = "input")
	public TcpSendingMessageHandler sendingMessageHandler(
		@Qualifier("tcpClientConnectionFactory") AbstractConnectionFactory connectionFactory) {
		TcpSendingMessageHandler sendingMessageHandler = new TcpSendingMessageHandler();
		sendingMessageHandler.setConnectionFactory(connectionFactory);
		return sendingMessageHandler;
	}

	@Bean
	public TcpConnectionFactoryFactoryBean tcpClientConnectionFactory(
		@Qualifier("tcpClientEncoder") AbstractByteArraySerializer encoder,
		@Qualifier("tcpClientMapper") TcpMessageMapper mapper,
		@Qualifier("tcpClientDecoder") AbstractByteArraySerializer decoder) throws Exception {
		TcpConnectionFactoryFactoryBean factoryBean = new TcpConnectionFactoryFactoryBean();
		factoryBean.setType("client");
		factoryBean.setHost("localhost");
		factoryBean.setPort(this.properties.getPort());
		factoryBean.setUsingNio(this.properties.isNio());
		factoryBean.setUsingDirectBuffers(this.properties.isUseDirectBuffers());
		factoryBean.setLookupHost(this.properties.isReverseLookup());
		factoryBean.setSerializer(encoder);
		factoryBean.setDeserializer(decoder);
		factoryBean.setSoTimeout(this.properties.getSocketTimeout());
		factoryBean.setMapper(mapper);
		factoryBean.setSingleUse(Boolean.FALSE);
		return factoryBean;
	}

	@Bean
	public EncoderDecoderFactoryBean tcpClientEncoder() {
		return new EncoderDecoderFactoryBean(this.properties.getEncoder());
	}

	@Bean
	public TcpMessageMapper tcpClientMapper() {
		TcpMessageMapper mapper = new TcpMessageMapper();
		mapper.setCharset(this.properties.getCharset());
		return mapper;
	}

	@Bean
	public EncoderDecoderFactoryBean tcpClientDecoder() {
		EncoderDecoderFactoryBean factoryBean = new EncoderDecoderFactoryBean(this.properties.getDecoder());
		factoryBean.setMaxMessageSize(this.properties.getBufferSize());
		return factoryBean;
	}

}
