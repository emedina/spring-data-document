/*
 * Copyright (c) 2011 by the original author(s).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.data.document.mongodb.mapping;

import static org.junit.Assert.*;

import java.util.List;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.data.document.mongodb.CollectionCallback;
import org.springframework.data.document.mongodb.MongoTemplate;
import org.springframework.data.document.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.document.mongodb.mapping.event.LoggingEventListener;
import org.springframework.data.document.mongodb.mapping.event.MongoMappingEvent;

/**
 * @author Jon Brisbin <jbrisbin@vmware.com>
 */
public class GeoIndexedTests {

	private final String[] collectionsToDrop = new String[]{
			GeoIndexedAppConfig.GEO_COLLECTION
	};

	ApplicationContext applicationContext;
	MongoTemplate template;
	MongoMappingContext mappingContext;

	@Before
	public void setUp() throws Exception {
		Mongo mongo = new Mongo();
		DB db = mongo.getDB(GeoIndexedAppConfig.GEO_DB);
		for (String coll : collectionsToDrop) {
			db.getCollection(coll).drop();
		}
		applicationContext = new AnnotationConfigApplicationContext(GeoIndexedAppConfig.class);
		template = applicationContext.getBean(MongoTemplate.class);
		mappingContext = applicationContext.getBean(MongoMappingContext.class);
	}

	@Test
	public void testGeoLocation() {
		GeoLocation geo = new GeoLocation(new double[]{40.714346, -74.005966});
		template.insert(geo);

		boolean hasIndex = template.execute("geolocation", new CollectionCallback<Boolean>() {
			public Boolean doInCollection(DBCollection collection) throws MongoException, DataAccessException {
				List<DBObject> indexes = collection.getIndexInfo();
				for (DBObject dbo : indexes) {
					if ("location_2d".equals(dbo.get("name"))) {
						return true;
					}
				}
				return false;
			}
		});

		assertTrue(hasIndex);
	}
	
}
