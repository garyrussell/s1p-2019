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

package com.example.demo;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class UpdateCurrencyApplication {

	public static void main(String[] args) {
		SpringApplication.run(UpdateCurrencyApplication.class, args);
	}

}

@RestController
@Transactional
class Rest {

	@Autowired
	EntityManager em;

	@PostMapping(path = "update/{symbol}/{rate}")
	public void post(@PathVariable String symbol, @PathVariable int rate) {
		em.merge(new Currency(symbol, rate));
	}

	@PostMapping(path = "delete/{symbol}")
	public void delete(@PathVariable String symbol) {
		Currency found = em.find(Currency.class, symbol);
		em.remove(found);
	}

}

@Entity
class Currency {

	@Id
	String symbol;

	int rate;

	public Currency() {
		super();
	}

	public Currency(String symbol, int rate) {
		this.symbol = symbol;
		this.rate = rate;
	}

	protected String getSymbol() {
		return this.symbol;
	}

	protected void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	protected int getRate() {
		return this.rate;
	}

	protected void setRate(int rate) {
		this.rate = rate;
	}

}
