package com.yoloo.server.objectify.repository;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Result;

import java.util.Arrays;
import java.util.Map;

interface ObjectifySaverRepository {

  /**
   * Asynchronously save a single entity in the datastore.
   *
   * <p>
   *
   * <p>If the entity has a null Long id, the value will be autogenerated and populated on the
   * entity object when the async operation completes. If you require this value, call now() on the
   * result.
   *
   * <p>Puts do not cascade.
   *
   * @param entity must be a registered entity type
   * @return an asynchronous result. To force a synchronous save, call Result.now().
   */
  <E> Result<Key<E>> saveAsync(E entity);

  /**
   * Asynchronously save a batch of entities in the datastore.
   *
   * <p>If any entities have null Long ids, the values will be autogenerated and populated on the
   * entity objects when the async operation completes. If you require these values, call now() on
   * the result.
   *
   * <p>Puts do not cascade.
   *
   * @param entities must be registered entity types
   * @return an asynchronous result. To force a synchronous save, call Result.now().
   */
  default <E> Result<Map<Key<E>, E>> saveAllAsync(E... entities) {
    return saveAllAsync(Arrays.asList(entities));
  }

  /**
   * Asynchronously save a batch of entities in the datastore.
   *
   * <p>If any entities have null Long ids, the values will be autogenerated and populated on the
   * entity objects when the async operation completes. If you require these values, call now() on
   * the result.
   *
   * <p>Puts do not cascade.
   *
   * @param entities must be registered entity types
   * @return an asynchronous result. To force a synchronous save, call Result.now().
   */
  <E> Result<Map<Key<E>, E>> saveAllAsync(Iterable<E> entities);

  /**
   * Synchronously save a single entity in the datastore.
   *
   * <p>
   *
   * <p>If the entity has a null Long id, the value will be autogenerated and populated on the
   * entity object when the async operation completes. If you require this value, call now() on the
   * result.
   *
   * <p>Puts do not cascade.
   *
   * @param entity must be a registered entity type
   * @return an synchronous result.
   */
  default <E> Key<E> save(E entity) {
    return saveAsync(entity).now();
  }

  /**
   * Synchronously save a batch of entities in the datastore.
   *
   * <p>
   *
   * <p>If the entity has a null Long id, the value will be autogenerated and populated on the
   * entity object when the async operation completes. If you require this value, call now() on the
   * result.
   *
   * <p>Puts do not cascade.
   *
   * @param entities must be a registered entity type
   * @return an synchronous result.
   */
  default <E> Map<Key<E>, E> saveAll(E... entities) {
    return saveAllAsync(Arrays.asList(entities)).now();
  }
}
