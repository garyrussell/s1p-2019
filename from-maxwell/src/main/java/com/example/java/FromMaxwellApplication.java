/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.java;

import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;

import com.example.java.ChangeEvent.Type;

@SpringBootApplication
public class FromMaxwellApplication {

	private static final Logger logger = LoggerFactory.getLogger(FromMaxwellApplication.class);

	@Autowired
	private KafkaTemplate<String, Double> template;

	public static void main(String[] args) {
		SpringApplication.run(FromMaxwellApplication.class, args);
	}

	@KafkaListener(id = "cdc", topics = "maxwell")
	public void listen(ChangeEvent change) {
		logger.info("Received: " + change);
		if (change.getTable().equals("currency")) {
			Type type = change.getType();
			if (type.equals(Type.insert) || type.equals(Type.update)) {
				String symbol = change.getData().getSymbol();
				double rate = ((change.getData().getRate())) / 1000.;
				this.template.send("currency", symbol, rate);
			}
			if (type.equals(Type.delete)) {
				String symbol = change.getData().getSymbol();
				this.template.send("currency", symbol, null);
			}
		}
	}

	@Bean
	public NewTopic topic() {
		return TopicBuilder.name("currency").build();
	}

	@Bean
	public NewTopic maxwell() {
		return TopicBuilder.name("maxwell").compact().build();
	}

	@Bean
	public RecordMessageConverter converter() {
		return new StringJsonMessageConverter();
	}

}
