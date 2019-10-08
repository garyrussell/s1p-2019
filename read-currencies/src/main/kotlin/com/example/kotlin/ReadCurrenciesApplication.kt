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

package com.example.kotlin

import java.util.concurrent.ConcurrentHashMap

import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.common.TopicPartition
import org.slf4j.LoggerFactory

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.listener.ConsumerAwareRebalanceListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@SpringBootApplication
open class ReadCurrenciesApplication {

    private val currencies = ConcurrentHashMap<String, Double>()

    @KafkaListener(id = "currency1", topics = ["currency"])
    fun listen(@Payload(required = false) rate: Double?,
               @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) key: String) {

        if (rate == null) {
            this.currencies.remove(key)
        } else {
            this.currencies[key] = rate
        }
        logger.info("Currencies now: " + this.currencies)
    }

    @Bean
    open fun topic(): NewTopic {
        return TopicBuilder.name("currency")
                .compact()
                .partitions(1)
                .replicas(1)
                .build()
    }

    companion object {

        private val logger = LoggerFactory.getLogger(ReadCurrenciesApplication::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(ReadCurrenciesApplication::class.java, *args)
        }
    }

}

@Component
internal class FactoryConfigurer(factory: ConcurrentKafkaListenerContainerFactory<*, *>) {

    init {
        factory.containerProperties.consumerRebalanceListener = object : ConsumerAwareRebalanceListener {

            override fun onPartitionsAssigned(consumer: Consumer<*, *>?, partitions: Collection<TopicPartition>?) {
                consumer!!.seekToBeginning(partitions)
            }

        }
    }

}
