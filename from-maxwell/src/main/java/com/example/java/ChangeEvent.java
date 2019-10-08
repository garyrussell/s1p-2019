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

/**
 * @author Gary Russell
 *
 */
public class ChangeEvent {

	private String database;

	private String table;

	private Type type;

	private Currency data;

	public String getDatabase() {
		return this.database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getTable() {
		return this.table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public Type getType() {
		return this.type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Currency getData() {
		return this.data;
	}

	public void setData(Currency data) {
		this.data = data;
	}

	public enum Type {
		insert, update, delete
	}

	@Override
	public String toString() {
		return "ChangeEvent [database=" + this.database + ", table=" + this.table + ", type=" + this.type
				+ ", currency=" + this.data + "]";
	}

	public static class Currency {

		private String symbol;

		private int rate;

		public String getSymbol() {
			return this.symbol;
		}

		public void setSymbol(String abbrev) {
			this.symbol = abbrev;
		}

		public int getRate() {
			return this.rate;
		}

		public void setRate(int rate) {
			this.rate = rate;
		}

		@Override
		public String toString() {
			return "Currency [symbol=" + this.symbol + ", rate=" + this.rate + "]";
		}
	}

}
