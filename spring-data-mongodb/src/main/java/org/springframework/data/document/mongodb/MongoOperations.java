/*
 * Copyright 2010-2011 the original author or authors.
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
package org.springframework.data.document.mongodb;

import java.util.List;
import java.util.Set;

import com.mongodb.CommandResult;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

import org.springframework.data.document.mongodb.index.IndexDefinition;
import org.springframework.data.document.mongodb.query.Query;
import org.springframework.data.document.mongodb.query.Update;

/**
 * Interface that specifies a basic set of MongoDB operations.  Implemented by {@link MongoTemplate}.
 * Not often used but a useful option for extensibility and testability (as it can be easily mocked, stubbed, or be
 * the target of a JDK proxy).
 *
 * @author Thomas Risberg
 * @author Mark Pollack
 * @author Oliver Gierke
 */
public interface MongoOperations {

  /**
   * The collection name used for the specified class by this template.
   *
   * @return
   */
  String getCollectionName(Class<?> clazz);

  /**
   * Execute the a MongoDB command expressed as a JSON string.  This will call the method
   * JSON.parse that is part of the MongoDB driver to convert the JSON string to a DBObject.
   * Any errors that result from executing this command will be converted into Spring's DAO
   * exception hierarchy.
   *
   * @param jsonCommand a MongoDB command expressed as a JSON string.
   */
  CommandResult executeCommand(String jsonCommand);

  /**
   * Execute a MongoDB command.  Any errors that result from executing this command will be converted
   * into Spring's DAO exception hierarchy.
   *
   * @param command a MongoDB command
   */
  CommandResult executeCommand(DBObject command);

  /**
   * Executes a {@link DbCallback} translating any exceptions as necessary.
   * <p/>
   * Allows for returning a result object, that is a domain object or a collection of domain objects.
   *
   * @param <T>    return type
   * @param action callback object that specifies the MongoDB actions to perform on the passed in DB instance.
   * @return a result object returned by the action or <tt>null</tt>
   */
  <T> T execute(DbCallback<T> action);

  /**
   * Executes the given {@link CollectionCallback} on the entity collection of the specified class.
   * <p/>
   * Allows for returning a result object, that is a domain object or a collection of domain objects.
   *
   * @param entityClass class that determines the collection to use
   * @param <T>    return type
   * @param action callback object that specifies the MongoDB action
   * @return a result object returned by the action or <tt>null</tt>
   */
  <T> T execute(Class<?> entityClass, CollectionCallback<T> action);

  /**
   * Executes the given {@link CollectionCallback} on the collection of the given name.
   * <p/>
   * Allows for returning a result object, that is a domain object or a collection of domain objects.
   *
   * @param <T>            return type
   * @param collectionName the name of the collection that specifies which DBCollection instance will be passed into
   * @param action         callback object that specifies the MongoDB action
   *                       the callback action.
   * @return a result object returned by the action or <tt>null</tt>
   */
  <T> T execute(String collectionName, CollectionCallback<T> action);

  /**
   * Executes the given {@link DbCallback} within the same connection to the database so as to ensure
   * consistency in a write heavy environment where you may read the data that you wrote.  See the
   * comments on {@see <a href=http://www.mongodb.org/display/DOCS/Java+Driver+Concurrency>Java Driver Concurrency</a>}
   * <p/>
   * Allows for returning a result object, that is a domain object or a collection of domain objects.
   *
   * @param <T>    return type
   * @param action callback that specified the MongoDB actions to perform on the DB instance
   * @return a result object returned by the action or <tt>null</tt>
   */
  <T> T executeInSession(DbCallback<T> action);

  /**
   * Create an uncapped collection with the provided name.
   *
   * @param collectionName name of the collection
   * @return the created collection
   */
  DBCollection createCollection(String collectionName);

  /**
   * Create a collect with the provided name and options.
   *
   * @param collectionName    name of the collection
   * @param collectionOptions options to use when creating the collection.
   * @return the created collection
   */
  DBCollection createCollection(String collectionName, CollectionOptions collectionOptions);

  /**
   * A set of collection names.
   *
   * @return list of collection names
   */
  Set<String> getCollectionNames();

  /**
   * Get a collection by name, creating it if it doesn't exist.
   * <p/>
   * Translate any exceptions as necessary.
   *
   * @param collectionName name of the collection
   * @return an existing collection or a newly created one.
   */
  DBCollection getCollection(String collectionName);

  /**
   * Check to see if a collection with a given name exists.
   * <p/>
   * Translate any exceptions as necessary.
   *
   * @param collectionName name of the collection
   * @return true if a collection with the given name is found, false otherwise.
   */
  boolean collectionExists(String collectionName);

  /**
   * Drop the collection with the given name.
   * <p/>
   * Translate any exceptions as necessary.
   *
   * @param collectionName name of the collection to drop/delete.
   */
  void dropCollection(String collectionName);

  /**
   * Query for a list of objects of type T from the default collection.
   * <p/>
   * The object is converted from the MongoDB native representation using an instance of
   * {@see MongoConverter}.  Unless configured otherwise, an
   * instance of SimpleMongoConverter will be used.
   * <p/>
   * If your collection does not contain a homogeneous collection of types, this operation will not be an efficient
   * way to map objects since the test for class type is done in the client and not on the server.
   *
   * @param targetClass the parameterized type of the returned list
   * @return the converted collection
   */
  <T> List<T> getCollection(Class<T> targetClass);

  /**
   * Query for a list of objects of type T from the specified collection.
   * <p/>
   * The object is converted from the MongoDB native representation using an instance of
   * {@see MongoConverter}.  Unless configured otherwise, an
   * instance of SimpleMongoConverter will be used.
   * <p/>
   * If your collection does not contain a homogeneous collection of types, this operation will not be an efficient
   * way to map objects since the test for class type is done in the client and not on the server.
   *
   * @param collectionName name of the collection to retrieve the objects from
   * @param targetClass    the parameterized type of the returned list.
   * @return the converted collection
   */
  <T> List<T> getCollection(String collectionName, Class<T> targetClass);

  /**
   * Ensure that an index for the provided {@link IndexDefinition} exists for the default collection.
   * If not it will be created.
   *
   * @param entityClass class that determines the collection to use
   * @param indexDefinition
   */
  void ensureIndex(Class<?> entityClass, IndexDefinition indexDefinition);

  /**
   * Ensure that an index for the provided {@link IndexDefinition} exists. If not it will be
   * created.
   *
   * @param collectionName
   * @param index
   */
  void ensureIndex(String collectionName, IndexDefinition indexDefinition);

  /**
   * Map the results of an ad-hoc query on the default MongoDB collection to a single instance of an object
   * of the specified type.
   * <p/>
   * The object is converted from the MongoDB native representation using an instance of
   * {@see MongoConverter}.  Unless configured otherwise, an
   * instance of SimpleMongoConverter will be used.
   * <p/>
   * The query is specified as a {@link Query} which can be created either using the {@link BasicQuery} or the more
   * feature rich {@link Query}.
   *
   * @param query       the query class that specifies the criteria used to find a record and also an optional fields specification
   * @param targetClass the parameterized type of the returned list.
   * @return the converted object
   */
  <T> T findOne(Query query, Class<T> targetClass);

  /**
   * Map the results of an ad-hoc query on the specified collection to a single instance of an object
   * of the specified type.
   * <p/>
   * The object is converted from the MongoDB native representation using an instance of
   * {@see MongoConverter}.  Unless configured otherwise, an
   * instance of SimpleMongoConverter will be used.
   * <p/>
   * The query is specified as a {@link Query} which can be created either using the {@link BasicQuery} or the more
   * feature rich {@link Query}.
   *
   * @param collectionName name of the collection to retrieve the objects from
   * @param query          the query class that specifies the criteria used to find a record and also an optional fields specification
   * @param targetClass    the parameterized type of the returned list.
   * @return the converted object
   */
  <T> T findOne(String collectionName, Query query,
                Class<T> targetClass);

  /**
   * Map the results of an ad-hoc query on the default MongoDB collection to a List of the specified type.
   * <p/>
   * The object is converted from the MongoDB native representation using an instance of
   * {@see MongoConverter}.  Unless configured otherwise, an
   * instance of SimpleMongoConverter will be used.
   * <p/>
   * The query is specified as a {@link Query} which can be created either using the {@link BasicQuery} or the more
   * feature rich {@link Query}.
   *
   * @param query       the query class that specifies the criteria used to find a record and also an optional fields specification
   * @param targetClass the parameterized type of the returned list.
   * @return the List of converted objects
   */
  <T> List<T> find(Query query, Class<T> targetClass);

  /**
   * Map the results of an ad-hoc query on the specified collection to a List of the specified type.
   * <p/>
   * The object is converted from the MongoDB native representation using an instance of
   * {@see MongoConverter}.  Unless configured otherwise, an
   * instance of SimpleMongoConverter will be used.
   * <p/>
   * The query is specified as a {@link Query} which can be created either using the {@link BasicQuery} or the more
   * feature rich {@link Query}.
   *
   * @param collectionName name of the collection to retrieve the objects from
   * @param query          the query class that specifies the criteria used to find a record and also an optional fields specification
   * @param targetClass    the parameterized type of the returned list.
   * @return the List of converted objects
   */
  <T> List<T> find(String collectionName, Query query,
                   Class<T> targetClass);

  /**
   * Map the results of an ad-hoc query on the specified collection to a List of the specified type.
   * <p/>
   * The object is converted from the MongoDB native representation using an instance of
   * {@see MongoConverter}.  Unless configured otherwise, an
   * instance of SimpleMongoConverter will be used.
   * <p/>
   * The query is specified as a {@link Query} which can be created either using the {@link BasicQuery} or the more
   * feature rich {@link Query}.
   *
   * @param collectionName name of the collection to retrieve the objects from
   * @param query          the query class that specifies the criteria used to find a record and also an optional fields specification
   * @param targetClass    the parameterized type of the returned list.
   * @param preparer       allows for customization of the DBCursor used when iterating over the result set,
   *                       (apply limits, skips and so on).
   * @return the List of converted objects.
   */
  <T> List<T> find(String collectionName, Query query, Class<T> targetClass, CursorPreparer preparer);

  /**
   * Map the results of an ad-hoc query on the default MongoDB collection to a single instance of an object
   * of the specified type. The first document that matches the query is returned and also removed from the
   * collection in the database.
   * <p/>
   * The object is converted from the MongoDB native representation using an instance of
   * {@see MongoConverter}.  Unless configured otherwise, an
   * instance of SimpleMongoConverter will be used.
   * <p/>
   * The query is specified as a {@link Query} which can be created either using the {@link BasicQuery} or the more
   * feature rich {@link Query}.
   *
   * @param query       the query class that specifies the criteria used to find a record and also an optional fields specification
   * @param targetClass the parameterized type of the returned list.
   * @return the converted object
   */
  <T> T findAndRemove(Query query, Class<T> targetClass);

  /**
   * Map the results of an ad-hoc query on the specified collection to a single instance of an object
   * of the specified type. The first document that matches the query is returned and also removed from the
   * collection in the database.
   * <p/>
   * The object is converted from the MongoDB native representation using an instance of
   * {@see MongoConverter}.  Unless configured otherwise, an
   * instance of SimpleMongoConverter will be used.
   * <p/>
   * The query is specified as a {@link Query} which can be created either using the {@link BasicQuery} or the more
   * feature rich {@link Query}.
   *
   * @param collectionName name of the collection to retrieve the objects from
   * @param query          the query class that specifies the criteria used to find a record and also an optional fields specification
   * @param targetClass    the parameterized type of the returned list.
   * @return the converted object
   */
  <T> T findAndRemove(String collectionName, Query query,
                Class<T> targetClass);

  /**
   * Insert the object into the default collection.
   * <p/>
   * The object is converted to the MongoDB native representation using an instance of
   * {@see MongoConverter}.  Unless configured otherwise, an
   * instance of SimpleMongoConverter will be used.
   * <p/>
   * If you object has an "Id' property, it will be set with the generated Id from MongoDB.  If your Id property
   * is a String then MongoDB ObjectId will be used to populate that string.  Otherwise, the conversion from
   * ObjectId to your property type will be handled by Spring's BeanWrapper class that leverages Spring 3.0's
   * new Type Conversion API.
   * See <a href="http://static.springsource.org/spring/docs/3.0.x/reference/validation.html#core-convert">Spring 3 Type Conversion"</a>
   * for more details.
   * <p/>
   * <p/>
   * Insert is used to initially store the object into the database.
   * To update an existing object use the save method.
   *
   * @param objectToSave the object to store in the collection.
   */
  void insert(Object objectToSave);

  /**
   * Insert the object into the specified collection.
   * <p/>
   * The object is converted to the MongoDB native representation using an instance of
   * {@see MongoConverter}.  Unless configured otherwise, an
   * instance of SimpleMongoConverter will be used.
   * <p/>
   * Insert is used to initially store the object into the
   * database.  To update an existing object use the save method.
   *
   * @param collectionName name of the collection to store the object in
   * @param objectToSave   the object to store in the collection
   */
  void insert(String collectionName, Object objectToSave);

  /**
   * Insert a list of objects into the default collection in a single batch write to the database.
   *
   * @param listToSave the list of objects to save.
   */
  void insertList(List<? extends Object> listToSave);

  /**
   * Insert a list of objects into the specified collection in a single batch write to the database.
   *
   * @param collectionName name of the collection to store the object in
   * @param listToSave     the list of objects to save.
   */
  void insertList(String collectionName, List<? extends Object> listToSave);

  /**
   * Save the object to the default collection.  This will perform an insert if the object is not already
   * present, that is an 'upsert'.
   * <p/>
   * The object is converted to the MongoDB native representation using an instance of
   * {@see MongoConverter}.  Unless configured otherwise, an
   * instance of SimpleMongoConverter will be used.
   * <p/>
   * If you object has an "Id' property, it will be set with the generated Id from MongoDB.  If your Id property
   * is a String then MongoDB ObjectId will be used to populate that string.  Otherwise, the conversion from
   * ObjectId to your property type will be handled by Spring's BeanWrapper class that leverages Spring 3.0's
   * new Type Conversion API.
   * See <a href="http://static.springsource.org/spring/docs/3.0.x/reference/validation.html#core-convert">Spring 3 Type Conversion"</a>
   * for more details.
   *
   * @param objectToSave the object to store in the collection
   */
  void save(Object objectToSave);

  /**
   * Save the object to the specified collection.  This will perform an insert if the object is not already
   * present, that is an 'upsert'.
   * <p/>
   * The object is converted to the MongoDB native representation using an instance of
   * {@see MongoConverter}.  Unless configured otherwise, an
   * instance of SimpleMongoConverter will be used.
   * <p/>
   * If you object has an "Id' property, it will be set with the generated Id from MongoDB.  If your Id property
   * is a String then MongoDB ObjectId will be used to populate that string.  Otherwise, the conversion from
   * ObjectId to your property type will be handled by Spring's BeanWrapper class that leverages Spring 3.0's
   * new Type Cobnversion API.
   * See <a href="http://static.springsource.org/spring/docs/3.0.x/reference/validation.html#core-convert">Spring 3 Type Conversion"</a>
   * for more details.
   *
   * @param collectionName name of the collection to store the object in
   * @param objectToSave   the object to store in the collection
   */
  void save(String collectionName, Object objectToSave);

  /**
   * Updates the first object that is found in the default collection that matches the query document
   * with the provided updated document.
   *
   * @param entityClass class that determines the collection to use
   * @param queryDoc  the query document that specifies the criteria used to select a record to be updated
   * @param updateDoc the update document that contains the updated object or $ operators to manipulate the
   *                  existing object.
   */
  WriteResult updateFirst(Class<?> entityClass, Query query, Update update);

  /**
   * Updates the first object that is found in the specified collection that matches the query document criteria
   * with the provided updated document.
   *
   * @param collectionName name of the collection to update the object in
   * @param queryDoc       the query document that specifies the criteria used to select a record to be updated
   * @param updateDoc      the update document that contains the updated object or $ operators to manipulate the
   *                       existing object.
   */
  WriteResult updateFirst(String collectionName, Query query,
                          Update update);

  /**
   * Updates all objects that are found in the default collection that matches the query document criteria
   * with the provided updated document.
   *
   * @param entityClass class that determines the collection to use
   * @param queryDoc  the query document that specifies the criteria used to select a record to be updated
   * @param updateDoc the update document that contains the updated object or $ operators to manipulate the
   *                  existing object.
   */
  WriteResult updateMulti(Class<?> entityClass, Query query, Update update);

  /**
   * Updates all objects that are found in the specified collection that matches the query document criteria
   * with the provided updated document.
   *
   * @param collectionName name of the collection to update the object in
   * @param queryDoc       the query document that specifies the criteria used to select a record to be updated
   * @param updateDoc      the update document that contains the updated object or $ operators to manipulate the
   *                       existing object.
   */
  WriteResult updateMulti(String collectionName, Query query,
                          Update update);

  /**
   * Remove the given object from the collection by Id
   * @param object
   */
  void remove(Object object);
  
 
  
  /**
   * Remove all documents from the default collection that match the provided query document criteria.
   *
   * @param queryDoc the query document that specifies the criteria used to remove a record
   */
  void remove(Query query);
  
  /**
   * Remove all documents from the default collection that match the provided query document criteria.  The
   * Class parameter is used to help convert the Id of the object if it is present in the query.
   * @param <T>
   * @param query
   * @param targetClass
   */
  <T> void remove(Query query, Class<T> targetClass);
  
  /**
   * Remove all documents from the specified collection that match the provided query document criteria.
   *
   * @param collectionName name of the collection where the objects will removed
   * @param queryDoc       the query document that specifies the criteria used to remove a record
   */
  void remove(String collectionName, Query query);
  
  /**
   * Remove all documents from the specified collection that match the provided query document criteria.
   * The Class parameter is used to help convert the Id of the object if it is present in the query.
   * @param collectionName
   * @param query
   * @param targetClass
   */
  <T> void remove(String collectionName, Query query, Class<T> targetClass);

}