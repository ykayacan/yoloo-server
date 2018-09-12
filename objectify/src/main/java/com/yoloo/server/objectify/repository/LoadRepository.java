package com.yoloo.server.objectify.repository;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Query;
import com.google.common.collect.Lists;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.QueryResultIterable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Objectify repository for loading entities.
 */
@NoRepositoryBean
public interface LoadRepository<E> extends ObjectifyAware, EntityManager<E>, Repository {
  /**
   * List all entities.
   * This will load all entities into memory, so should only be used where the number of entities is constrained.
   *
   * @return List of entities.
   */
  @Nonnull
  default List<E> findAll() {
    return ofy()
        .load()
        .type(getEntityType())
        .list();
  }

  /**
   * List keys of all entities.
   *
   * @return List of keys belonging to all entities.
   */
  @Nonnull
  default List<Key<E>> findAllKeys() {
    return ofy()
        .load()
        .type(getEntityType())
        .keys()
        .list();
  }

  /**
   * List {@code limit} entities.
   * This will load all entities into memory, so should only be used where the number of entities is constrained.
   *
   * @param limit Max number of entities to retrieve.
   * @return List of entities.
   */
  @Nonnull
  default List<E> findAll(int limit) {
    return ofy()
        .load()
        .type(getEntityType())
        .limit(limit)
        .list();
  }

  /**
   * List {@code limit} entity keys.
   *
   * @param limit Max number of entities to retrieve.
   * @return List of keys belonging to all entities.
   */
  @Nonnull
  default Supplier<List<Key<E>>> findAllKeysAsync(int limit) {
    QueryResultIterable<Key<E>> iterable = ofy()
        .load()
        .type(getEntityType())
        .limit(limit)
        .keys()
        .iterable();
    return () -> Lists.newArrayList(iterable);
  }

  /**
   * List {@code limit} entity keys.
   *
   * @param limit Max number of entities to retrieve.
   * @return List of keys belonging to all entities.
   */
  @Nonnull
  default List<Key<E>> findAllKeys(int limit) {
    return findAllKeysAsync(limit).get();
  }

  /**
   * Get the entities with the given keys, if they exist.
   *
   * @param keys keys to load.
   * @return A list of loaded entities keyed by the entity key.
   */
  @Nonnull
  default List<E> findAll(Iterable<Key<E>> keys) {
    return new ArrayList<>(
        ofy()
            .load()
            .keys(keys)
            .values()
    );
  }

  /**
   * Get the entities with the given keys, if they exist.
   *
   * @param keys List of keys to load.
   * @return A list of loaded entities keyed by the entity key.
   */
  @Nonnull
  @SuppressWarnings("unchecked")
  default List<E> findAll(Key<E>... keys) {
    return findAll(Arrays.asList(keys));
  }

  /**
   * Get the entities with the given web-safe key strings, if they exist.
   *
   * @param webSafeStrings List of keys to load.
   * @return A list of loaded entities keyed by the web-safe key string.
   */
  @Nonnull
  @SuppressWarnings("unchecked")
  default List<E> findAllByWebSafeKey(Collection<String> webSafeStrings) {
    List<Key<E>> keys = webSafeStrings.stream()
        .map(string -> (Key<E>) Key.create(string))
        .collect(Collectors.toList());

    return new ArrayList<>(
        ofy()
            .load()
            .keys(keys)
            .values()
    );
  }

  /**
   * Get the entities with the given web-safe key strings, if they exist.
   *
   * @param webSafeStrings List of keys to load.
   * @return A list of loaded entities keyed by the web-safe key string.
   */
  @Nonnull
  @SuppressWarnings("unchecked")
  default List<E> findAllByWebSafeKey(String... webSafeStrings) {
    return findAllByWebSafeKey(Arrays.asList(webSafeStrings));
  }

  /**
   * Get all entities whose field has the value of the given object.
   * Note that the given field must be indexed for anything to be returned.
   * This will load all entities into memory, so should only be used where the number of entities is constrained.
   *
   * @param field Name of the field to filterIn by.
   * @param value The value to filterIn by.
   * @return List of entities matching the given value.
   */
  @Nonnull
  default List<E> findAllByField(String field, @Nullable Object value) {
    return ofy()
        .load()
        .type(getEntityType())
        .filter(field, value)
        .list();
  }

  /**
   * Get all entities whose field has the values of any of the given objects.
   * Note that the given field must be indexed for anything to be returned.
   * This will load all entities into memory, so should only be used where the number of entities is constrained.
   *
   * @param field  Name of the field to filterIn by.
   * @param values List of values to filterIn by.
   * @return List of entities matching the given values.
   */
  @Nonnull
  default List<E> findAllByField(String field, List<?> values) {
    return ofy()
        .load()
        .type(getEntityType())
        .filter(String.format("%s %s", field, Query.FilterOperator.IN.toString()), values)
        .list();
  }

  /**
   * Get all entities whose field has the values of any of the given objects.
   * Note that the given field must be indexed for anything to be returned.
   * This will load all entities into memory, so should only be used where the number of entities is constrained.
   *
   * @param field  Name of the field to filterIn by.
   * @param values List of values to filterIn by.
   * @return List of entities matching the given values.
   */
  @Nonnull
  default List<E> findAllByField(String field, Object... values) {
    return findAllByField(field, Arrays.asList(values));
  }

  /**
   * Get the entity with the given key.
   *
   * @param key The key.
   * @return The entity or an empty {@link Optional} if none exists.
   */
  @Nonnull
  default Optional<E> findByKey(Key<E> key) {
    return Optional.ofNullable(
        ofy()
            .load()
            .key(key)
            .now()
    );
  }

  /**
   * Get the entity with the given key or throw an exception if not found.
   *
   * @param key The key.
   * @return The entity.
   * @throws EntityNotFoundException if entity not found by key.
   */
  @Nonnull
  default E getByKey(Key<E> key) throws EntityNotFoundException {
    return findByKey(key)
        .orElseThrow(() -> new EntityNotFoundException(key));
  }

  /**
   * Find an entity by its web-safe key string.
   *
   * @param webSafeString Entity string.
   * @return The entity or an empty {@link Optional} if none exists.
   */
  @SuppressWarnings("unchecked")
  default Optional<E> findByWebSafeKey(String webSafeString) {
    return Optional.ofNullable(
        ofy()
            .load()
            .key((Key<E>) Key.create(webSafeString))
            .now()
    );
  }
}
