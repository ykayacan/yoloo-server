package com.yoloo.server.relationship.infrastructure.queue.pull

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.appengine.api.taskqueue.Queue
import com.google.appengine.api.taskqueue.TaskHandle
import com.googlecode.objectify.cmd.Query
import com.yoloo.server.common.id.generator.LongIdGenerator
import com.yoloo.server.objectify.ObjectifyProxy.ofy
import com.yoloo.server.relationship.domain.entity.Relationship
import com.yoloo.server.relationship.infrastructure.event.RelationshipEvent
import com.yoloo.server.user.domain.entity.User
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class RelationshipPullServlet(
    @Qualifier("relationship-queue") private val queue: Queue,
    private val mapper: ObjectMapper,
    @Qualifier("cached") private val idGenerator: LongIdGenerator
) : HttpServlet() {

    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        val tasks = queue.leaseTasks(3600, TimeUnit.SECONDS, NUMBER_OF_TASKS_TO_LEASE)

        processTasks(tasks, queue, mapper, idGenerator)
    }

    companion object {
        private val log = LoggerFactory.getLogger(RelationshipPullServlet::class.java)

        private const val NUMBER_OF_TASKS_TO_LEASE = 500L

        private fun processTasks(
            tasks: List<TaskHandle>,
            q: Queue,
            mapper: ObjectMapper,
            idGenerator: LongIdGenerator
        ) {
            if (tasks.isEmpty()) {
                log.info("Task Queue has no tasks available for lease.")
                return
            }

            val pendingSaves = mutableListOf<Any>()

            val incCountUserIds = mutableSetOf<Long>()
            val decCountUserIds = mutableSetOf<Long>()

            var query: Query<Relationship> = ofy().load().type(Relationship::class.java)

            for (task in tasks) {
                if (task.name == RelationshipEvent.Follow::class.java.simpleName) {
                    val payload = mapper.readValue(task.payload, RelationshipEvent.Follow.Payload::class.java)
                    log.info("Processing: taskName='{}'  payload='{}'", task.name, payload)

                    val relationship = createRelationship(idGenerator, payload)

                    pendingSaves.add(relationship)
                    incCountUserIds.add(payload.toUserId)
                } else if (task.name == RelationshipEvent.Unfollow::class.java.simpleName) {
                    val payload = mapper.readValue(task.payload, RelationshipEvent.Unfollow.Payload::class.java)
                    log.info("Processing: taskName='{}'  payload='{}'", task.name, payload)

                    query = query.filter(Relationship.FROM_ID, payload.fromUserId)
                        .filter(Relationship.TO_ID, payload.toUserId)

                    decCountUserIds.add(payload.toUserId)
                }
            }

            val fetchIds = mutableListOf<Long>()
            fetchIds.addAll(incCountUserIds)
            fetchIds.addAll(decCountUserIds)

            ofy()
                .load()
                .type(User::class.java)
                .ids(fetchIds)
                .values
                .map {
                    if (incCountUserIds.contains(it.id)) {
                        it.profile.countData.followerCount.inc()
                    } else {
                        it.profile.countData.followerCount.dec()
                    }
                    return@map it
                }
                .let(pendingSaves::addAll)

            ofy().save().entities(pendingSaves)
            if (decCountUserIds.isNotEmpty()) {
                ofy().delete().keys(query.keys().list())
            }

            q.deleteTaskAsync(tasks)

            log.info("Processed and deleted ${tasks.size} tasks from the task queue (max: $NUMBER_OF_TASKS_TO_LEASE).")
        }

        private fun createRelationship(
            idGenerator: LongIdGenerator,
            payload: RelationshipEvent.Follow.Payload
        ): Relationship {
            return Relationship(
                id = idGenerator.generateId(),
                fromId = payload.fromUserId,
                toId = payload.toUserId,
                displayName = payload.fromDisplayName,
                avatarImage = payload.fromAvatarImage
            )
        }
    }
}
