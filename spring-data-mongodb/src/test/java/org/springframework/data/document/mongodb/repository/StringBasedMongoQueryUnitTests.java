/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.document.mongodb.repository;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.document.mongodb.MongoTemplate;
import org.springframework.data.document.mongodb.convert.MongoConverter;
import org.springframework.data.document.mongodb.convert.SimpleMongoConverter;
import org.springframework.data.document.mongodb.query.BasicQuery;
import org.springframework.data.document.mongodb.repository.MongoRepositoryFactoryBean.EntityInformationCreator;
import org.springframework.data.repository.support.RepositoryMetadata;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Unit tests for {@link StringBasedMongoQuery}.
 *
 * @author Oliver Gierke
 */
@RunWith(MockitoJUnitRunner.class)
public class StringBasedMongoQueryUnitTests {

  @Mock
  MongoTemplate template;
  @Mock
  RepositoryMetadata metadata;
  @Mock
  EntityInformationCreator creator;
  
  MongoConverter converter = new SimpleMongoConverter();

  @Before
  public void setUp() {
    when(template.getConverter()).thenReturn(converter);
  }

  @Test
  public void bindsSimplePropertyCorrectly() throws Exception {

    Method method = SampleRepository.class.getMethod("findByLastname", String.class);
    MongoQueryMethod queryMethod = new MongoQueryMethod(method, metadata, creator);
    StringBasedMongoQuery mongoQuery = new StringBasedMongoQuery(queryMethod, template);
    ConvertingParameterAccessor accesor = StubParameterAccessor.getAccessor(converter, "Matthews");

    org.springframework.data.document.mongodb.query.Query query = mongoQuery.createQuery(accesor);
    org.springframework.data.document.mongodb.query.Query reference = new BasicQuery("{'lastname' : 'Matthews'}");

    assertThat(query.getQueryObject(), is(reference.getQueryObject()));
  }
  
  @Test
  public void bindsComplexPropertyCorrectly() throws Exception {

    Method method = SampleRepository.class.getMethod("findByAddress", Address.class);
    MongoQueryMethod queryMethod = new MongoQueryMethod(method, metadata, creator);
    StringBasedMongoQuery mongoQuery = new StringBasedMongoQuery(queryMethod, template);
    
    Address address = new Address("Foo", "0123", "Bar");
    ConvertingParameterAccessor accesor = StubParameterAccessor.getAccessor(converter, address);
    
    DBObject dbObject = new BasicDBObject();
    converter.write(address, dbObject);
    
    org.springframework.data.document.mongodb.query.Query query = mongoQuery.createQuery(accesor);
    org.springframework.data.document.mongodb.query.Query reference = new BasicQuery(new BasicDBObject("address", dbObject));

    assertThat(query.getQueryObject(), is(reference.getQueryObject()));
  }

  private interface SampleRepository {

    @Query("{ 'lastname' : ?0 }")
    Person findByLastname(String lastname);
    
    @Query("{ 'address' : ?0 }")
    Person findByAddress(Address address);
  }
}
