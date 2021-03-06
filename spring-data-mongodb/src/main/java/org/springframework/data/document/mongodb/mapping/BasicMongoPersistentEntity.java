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

import org.springframework.data.mapping.BasicPersistentEntity;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.data.mapping.model.PersistentEntity;
import org.springframework.data.util.TypeInformation;
import org.springframework.util.StringUtils;


/**
 * Mongo specific {@link PersistentEntity} implementation that adds Mongo specific meta-data such as the collection name
 * and the like.
 *
 * @author Jon Brisbin <jbrisbin@vmware.com>
 * @author Oliver Gierke
 */
public class BasicMongoPersistentEntity<T> extends BasicPersistentEntity<T, MongoPersistentProperty> implements MongoPersistentEntity<T> {

  private final String collection;
  private final boolean isRootEntity;

  /**
   * Creates a new {@link BasicMongoPersistentEntity} with the given {@link TypeInformation}. Will
   * default the collection name to the entities simple type name.
   *
   * @param typeInformation
   */
  public BasicMongoPersistentEntity(TypeInformation<T> typeInformation) {
    
    super(typeInformation);
    
    Class<?> rawType = typeInformation.getType();
    String fallback = rawType.getSimpleName().toLowerCase();
    
    if (rawType.isAnnotationPresent(Document.class)) {
      Document d = rawType.getAnnotation(Document.class);
      this.collection = StringUtils.hasText(d.collection()) ? d.collection() : fallback;
      this.isRootEntity = true;
    } else {
      this.collection = fallback;
      this.isRootEntity = false;
    }
  }

  /**
   * Returns the collection the entity should be stored in.
   *
   * @return
   */
  public String getCollection() {
    return collection;
  }

  /* (non-Javadoc)
   * @see org.springframework.data.mapping.BasicPersistentEntity#verify()
   */
  @Override
  public void verify() {
    if (isRootEntity && idProperty == null) {
      throw new MappingException(String.format("Root entity %s has to have an id property!", getType().getName()));
    }
  }
}
