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

import org.apache.kafka.clients.admin.NewTopic
import org.slf4j.LoggerFactory

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.converter.RecordMessageConverter
import org.springframework.kafka.support.converter.StringJsonMessageConverter

import com.example.java.ChangeEvent
import com.example.java.ChangeEvent.Type

@SpringBootApplication
open class FromMaxwellApplication {

    @Autowired
    private val template: KafkaTemplate<String, Double>? = null

    @KafkaListener(id = "cdc", topics = ["maxwell"])
    fun listen(change: ChangeEvent) {
        logger.info("Received: $change")
        if (change.table == "currency") {
            val type = change.type
            if (type == Type.insert || type == Type.update) {
                val symbol = change.data.symbol
                val rate = change.data.rate / 1000.0
                this.template!!.send("currency", symbol, rate)
            }
            if (type == Type.delete) {
                val symbol = change.data.symbol
                this.template!!.send("currency", symbol, null)
            }
        }
    }

    @Bean
    open fun topic(): NewTopic {
        return TopicBuilder.name("currency").compact().build()
    }

    @Bean
    open fun maxwell(): NewTopic {
        return TopicBuilder.name("maxwell").build()
    }

    @Bean
    open fun converter(): RecordMessageConverter {
        return StringJsonMessageConverter()
    }

    companion object {

        private val logger = LoggerFactory.getLogger(FromMaxwellApplication::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(FromMaxwellApplication::class.java, *args)
        }
    }

}
