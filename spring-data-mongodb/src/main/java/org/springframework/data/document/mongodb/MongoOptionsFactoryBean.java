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

import com.mongodb.Mongo;
import com.mongodb.MongoOptions;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * A factory bean for construction a MongoOptions instance
 *
 * @author Graeme Rocher
 */
public class MongoOptionsFactoryBean implements FactoryBean<MongoOptions>, InitializingBean {

  private static final MongoOptions MONGO_OPTIONS = new MongoOptions();
  /**
   * number of connections allowed per host
   * will block if run out
   */
  private int connectionsPerHost;

  /**
   * multiplier for connectionsPerHost for # of threads that can block
   * if connectionsPerHost is 10, and threadsAllowedToBlockForConnectionMultiplier is 5,
   * then 50 threads can block
   * more than that and an exception will be throw
   */
  private int threadsAllowedToBlockForConnectionMultiplier;

  /**
   * max wait time of a blocking thread for a connection
   */
  private int maxWaitTime;

  /**
   * connect timeout in milliseconds. 0 is default and infinite
   */
  private int connectTimeout;

  /**
   * socket timeout.  0 is default and infinite
   */
  private int socketTimeout;

  /**
   * socket keep alive.  false is default
   */
  private int socketKeepAlive;

  /**
   * this controls whether or not on a connect, the system retries automatically
   */
  private boolean autoConnectRetry;

 /**
     * Specifies if the driver is allowed to read from secondaries
     * or slaves.
     *
     * defaults to false
     */
  private boolean slaveOk;

  /**
     * If <b>true</b> the driver sends a getLastError command after
     * every update to ensure it succeeded (see also w and wtimeout)
     * If <b>false</b>, the driver does not send a getlasterror command
     * after every update.
     *
     * defaults to false
     */
  private boolean safe;

  /**
     * If set, the w value of WriteConcern for the connection is set
     * to this.
     *
     * Defaults to 0; implies safe = true
     */
  private int w;

  /**
     * If set, the wtimeout value of WriteConcern for the
     * connection is set to this.
     *
     * Defaults to 0; implies safe = true
     */
  private int wtimeout;

  /**
     * Sets the fsync value of WriteConcern for the connection.
     *
     * Defaults to false; implies safe = true
     */
  private boolean fsync;

  /**
   * number of connections allowed per host
   * will block if run out
   */
  public void setConnectionsPerHost(int connectionsPerHost) {
    this.connectionsPerHost = connectionsPerHost;
  }

  /**
   * multiplier for connectionsPerHost for # of threads that can block
   * if connectionsPerHost is 10, and threadsAllowedToBlockForConnectionMultiplier is 5,
   * then 50 threads can block
   * more than that and an exception will be throw
   */
  public void setThreadsAllowedToBlockForConnectionMultiplier(
      int threadsAllowedToBlockForConnectionMultiplier) {
    this.threadsAllowedToBlockForConnectionMultiplier = threadsAllowedToBlockForConnectionMultiplier;
  }

  /**
   * max wait time of a blocking thread for a connection
   */
  public void setMaxWaitTime(int maxWaitTime) {
    this.maxWaitTime = maxWaitTime;
  }

  /**
   * connect timeout in milliseconds. 0 is default and infinite
   */
  public void setConnectTimeout(int connectTimeout) {
    this.connectTimeout = connectTimeout;
  }

  /**
   * socket timeout.  0 is default and infinite
   */
  public void setSocketTimeout(int socketTimeout) {
    this.socketTimeout = socketTimeout;
  }

  /**
   * this controls whether or not on a connect, the system retries automatically
   */
  public void setAutoConnectRetry(boolean autoConnectRetry) {
    this.autoConnectRetry = autoConnectRetry;
  }

  /**
   * socket keep alive.  false is default
   */
  public void setSocketKeepAlive(boolean socketKeepAlive) {
        this.socketKeepAlive = socketKeepAlive;
    }

 /**
     * Specifies if the driver is allowed to read from secondaries
     * or slaves.
     *
     * defaults to false
     */
  public void setSlaveOk(boolean slaveOk) {
        this.slaveOk = slaveOk;
    }

  /**
     * If <b>true</b> the driver sends a getLastError command after
     * every update to ensure it succeeded (see also w and wtimeout)
     * If <b>false</b>, the driver does not send a getlasterror command
     * after every update.
     *
     * defaults to false
     */
  public void setSafe(boolean safe) {
        this.safe = safe;
    }

  /**
     * If set, the w value of WriteConcern for the connection is set
     * to this.
     *
     * Defaults to 0; implies safe = true
     */
  public void setW(boolean w) {
        this.w = w;
    }

  /**
     * If set, the wtimeout value of WriteConcern for the
     * connection is set to this.
     *
     * Defaults to 0; implies safe = true
     */
  public void setWtimeout(boolean wtimeout) {
        this.wtimeout = wtimeout;
    }

  /**
     * Sets the fsync value of WriteConcern for the connection.
     *
     * Defaults to false; implies safe = true
     */
  public void setSocketKeepAlive(boolean socketKeepAlive) {
        this.socketKeepAlive = socketKeepAlive;
    }
  private boolean fsync = MONGO_OPTIONS.fsync;

  public void afterPropertiesSet() {
    if (MONGO_OPTIONS.connectionsPerHost != connectionsPerHost) {
    MONGO_OPTIONS.connectionsPerHost = connectionsPerHost;
    }
    if (MONGO_OPTIONS.threadsAllowedToBlockForConnectionMultiplier != threadsAllowedToBlockForConnectionMultiplier) {
    MONGO_OPTIONS.threadsAllowedToBlockForConnectionMultiplier = threadsAllowedToBlockForConnectionMultiplier;
    }
    if (MONGO_OPTIONS.maxWaitTime != maxWaitTime) {
    MONGO_OPTIONS.maxWaitTime = maxWaitTime;
    }
    if (MONGO_OPTIONS.connectTimeout != connectTimeout) {
    MONGO_OPTIONS.connectTimeout = connectTimeout;
    }
    if (MONGO_OPTIONS.socketTimeout != socketTimeout) {
    MONGO_OPTIONS.socketTimeout = socketTimeout;
    }
    if (MONGO_OPTIONS.autoConnectRetry != autoConnectRetry) {
    MONGO_OPTIONS.autoConnectRetry = autoConnectRetry;
    }
    if (Mongo.MAJOR_VERSION >= 2 && Mongo.MINOR_VERSION >= 5 && MONGO_OPTIONS.autoConnectRetry != autoConnectRetry) {
    MONGO_OPTIONS.socketKeepAlive = socketKeepAlive;
    }
    if (Mongo.MAJOR_VERSION >= 2 && Mongo.MINOR_VERSION >= 5 && MONGO_OPTIONS.autoConnectRetry != autoConnectRetry) {
    MONGO_OPTIONS.slaveOk = slaveOk;
    }
    if (Mongo.MAJOR_VERSION >= 2 && Mongo.MINOR_VERSION >= 5 && MONGO_OPTIONS.autoConnectRetry != autoConnectRetry) {
    MONGO_OPTIONS.safe = safe;
    }
    if (Mongo.MAJOR_VERSION >= 2 && Mongo.MINOR_VERSION >= 5 && MONGO_OPTIONS.autoConnectRetry != autoConnectRetry) {
    MONGO_OPTIONS.w = w;
    }
    if (Mongo.MAJOR_VERSION >= 2 && Mongo.MINOR_VERSION >= 5 && MONGO_OPTIONS.autoConnectRetry != autoConnectRetry) {
    MONGO_OPTIONS.wtimeout = wtimeout;
    }
    if (Mongo.MAJOR_VERSION >= 2 && Mongo.MINOR_VERSION >= 5 && MONGO_OPTIONS.autoConnectRetry != autoConnectRetry) {
    MONGO_OPTIONS.fsync = fsync;
    }

  }

  public MongoOptions getObject() {
    return MONGO_OPTIONS;
  }

  public Class<?> getObjectType() {
    return MongoOptions.class;
  }

  public boolean isSingleton() {
    return true;
  }

}
