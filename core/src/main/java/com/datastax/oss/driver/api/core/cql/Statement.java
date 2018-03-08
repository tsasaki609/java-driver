/*
 * Copyright DataStax, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.datastax.oss.driver.api.core.cql;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DriverConfigProfile;
import com.datastax.oss.driver.api.core.metadata.token.Token;
import com.datastax.oss.driver.api.core.session.Request;
import com.datastax.oss.driver.api.core.session.Session;
import com.datastax.oss.driver.api.core.time.TimestampGenerator;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.CompletionStage;

/**
 * A request to execute a CQL query.
 *
 * @param <T> the "self type" used for covariant returns in subtypes.
 */
public interface Statement<T extends Statement<T>> extends Request {
  // Implementation note: "CqlRequest" would be a better name, but we keep "Statement" to match
  // previous driver versions.

  /**
   * The type returned when a CQL statement is executed synchronously.
   *
   * <p>Most users won't use this explicitly. It is needed for the generic execute method ({@link
   * Session#execute(Request, GenericType)}), but CQL statements will generally be run with one of
   * the driver's built-in helper methods (such as {@link CqlSession#execute(Statement)}).
   */
  GenericType<ResultSet> SYNC = GenericType.of(ResultSet.class);

  /**
   * The type returned when a CQL statement is executed asynchronously.
   *
   * <p>Most users won't use this explicitly. It is needed for the generic execute method ({@link
   * Session#execute(Request, GenericType)}), but CQL statements will generally be run with one of
   * the driver's built-in helper methods (such as {@link CqlSession#executeAsync(Statement)}).
   */
  GenericType<CompletionStage<AsyncResultSet>> ASYNC =
      new GenericType<CompletionStage<AsyncResultSet>>() {};

  /**
   * Sets the name of the driver configuration profile that will be used for execution.
   *
   * <p>For all the driver's built-in implementations, this method has no effect if {@link
   * #getConfigProfile()} has been called with a non-null argument.
   *
   * <p>All the driver's built-in implementations are immutable, and return a new instance from this
   * method. However custom implementations may choose to be mutable and return the same instance.
   */
  T setConfigProfileName(String newConfigProfileName);

  /**
   * Sets the configuration profile to use for execution.
   *
   * <p>All the driver's built-in implementations are immutable, and return a new instance from this
   * method. However custom implementations may choose to be mutable and return the same instance.
   */
  T setConfigProfile(DriverConfigProfile newProfile);

  /**
   * Sets the keyspace to use for token-aware routing.
   *
   * <p>See {@link Request#getRoutingKey()} for a description of the token-aware routing algorithm.
   */
  T setRoutingKeyspace(CqlIdentifier newRoutingKeyspace);

  /**
   * Sets the key to use for token-aware routing.
   *
   * <p>See {@link Request#getRoutingKey()} for a description of the token-aware routing algorithm.
   */
  T setRoutingKey(ByteBuffer newRoutingKey);

  /**
   * Sets the token to use for token-aware routing.
   *
   * <p>See {@link Request#getRoutingKey()} for a description of the token-aware routing algorithm.
   */
  T setRoutingToken(Token newRoutingToken);

  /**
   * Sets the custom payload to use for execution.
   *
   * <p>All the driver's built-in implementations are immutable, and return a new instance from this
   * method. However custom implementations may choose to be mutable and return the same instance.
   */
  T setCustomPayload(Map<String, ByteBuffer> newCustomPayload);

  /**
   * Sets the idempotence to use for execution.
   *
   * <p>All the driver's built-in implementations are immutable, and return a new instance from this
   * method. However custom implementations may choose to be mutable and return the same instance.
   *
   * @param newIdempotence a boolean instance to set a statement-specific value, or {@code null} to
   *     use the default idempotence defined in the configuration.
   */
  T setIdempotent(Boolean newIdempotence);

  /**
   * Sets tracing for execution.
   *
   * <p>All the driver's built-in implementations are immutable, and return a new instance from this
   * method. However custom implementations may choose to be mutable and return the same instance.
   */
  T setTracing(boolean newTracing);

  long getTimestamp();

  /**
   * Sets the query timestamp to send with the statement.
   *
   * <p>If this is equal to {@link Long#MIN_VALUE}, the {@link TimestampGenerator} configured for
   * this driver instance will be used to generate a timestamp.
   *
   * <p>All the driver's built-in implementations are immutable, and return a new instance from this
   * method. However custom implementations may choose to be mutable and return the same instance.
   */
  T setTimestamp(long newTimestamp);

  ByteBuffer getPagingState();

  /**
   * Sets the paging state to send with the statement.
   *
   * <p>All the driver's built-in implementations are immutable, and return a new instance from this
   * method. However custom implementations may choose to be mutable and return the same instance;
   * if you do so, you must override {@link #copy(ByteBuffer)}.
   */
  T setPagingState(ByteBuffer newPagingState);

  /**
   * Creates a <b>new instance</b> with a different paging state.
   *
   * <p>Since all the built-in statement implementations in the driver are immutable, this method's
   * default implementation delegates to {@link #setPagingState(ByteBuffer)}. However, if you write
   * your own mutable implementation, make sure it returns a different instance.
   */
  default T copy(ByteBuffer newPagingState) {
    return setPagingState(newPagingState);
  }
}