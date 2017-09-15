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

import org.springframework.cloud.stream.binder.Binder;
import org.springframework.cloud.stream.binder.BinderFactory;
import org.springframework.cloud.stream.binder.BinderType;
import org.springframework.cloud.stream.binder.BinderTypeRegistry;
import org.springframework.cloud.stream.binder.Binding;
import org.springframework.cloud.stream.binder.ConsumerProperties;
import org.springframework.cloud.stream.binder.ProducerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author David Turanski
 **/
@Configuration
public class DummyBinderConfiguration {
	@Bean
	public BinderTypeRegistry binderTypeRegistry() {
		return new BinderTypeRegistry() {

			@Override
			public BinderType get(String s) {
				return new BinderType(null, null);
			}

			@Override
			public Map<String, BinderType> getAll() {
				return new HashMap<>();
			}
		};
	}

	@Bean
	BinderFactory binderFactory() {
		return new BinderFactory() {
			@Override
			public <T> Binder<T, ? extends ConsumerProperties, ? extends ProducerProperties> getBinder(String s,
				Class<? extends T> aClass) {
				return new Binder<T, ConsumerProperties, ProducerProperties>() {

					@Override
					public Binding<T> bindConsumer(String s, String s1, T t, ConsumerProperties consumerProperties) {
						return new Binding<T>() {
							@Override
							public void unbind() {

							}
						};
					}

					@Override
					public Binding<T> bindProducer(String s, T t, ProducerProperties producerProperties) {
						return new Binding<T>() {
							@Override
							public void unbind() {

							}
						};
					}
				};
			}
		};
	}
}
