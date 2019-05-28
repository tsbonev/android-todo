package com.tsbonev.todo.adapter.nitrite

import com.tsbonev.todo.core.AddToDoRequest
import com.tsbonev.todo.core.EditToDoRequest
import com.tsbonev.todo.core.IdGenerator
import com.tsbonev.todo.core.ToDo
import com.tsbonev.todo.core.ToDoNotFoundException
import com.tsbonev.todo.core.ToDoRepository
import com.tsbonev.todo.core.UUIDGenerator
import org.dizitart.kno2.filters.elemMatch
import org.dizitart.kno2.filters.eq
import org.dizitart.kno2.filters.gte
import org.dizitart.kno2.filters.lt
import org.dizitart.kno2.filters.text
import org.dizitart.kno2.filters.within
import org.dizitart.no2.FindOptions
import org.dizitart.no2.IndexType
import org.dizitart.no2.Nitrite
import org.dizitart.no2.objects.Id
import org.dizitart.no2.objects.Index
import org.dizitart.no2.objects.Indices
import org.dizitart.no2.objects.ObjectFilter
import org.dizitart.no2.objects.ObjectRepository
import org.dizitart.no2.objects.filters.ObjectFilters
import java.time.LocalDateTime
import java.util.Optional

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class NitriteToDoRepository(
    private val nitriteDb: Nitrite,
    private val collection: String = "ToDos",
    private val idGenerator: IdGenerator = UUIDGenerator()
) : ToDoRepository {

    private val repo: ObjectRepository<NitriteToDo>
        get() = nitriteDb.getRepository(collection, NitriteToDo::class.java)


    override fun addToDo(request: AddToDoRequest): ToDo {
        val toDo = NitriteToDo(
            idGenerator.generateId(),
            request.createdOn,
            request.endDate,
            request.title,
            request.content,
            request.tags,
            buildSearchIndex(request.title, request.content, *request.tags.toTypedArray())
        )

        repo.insert(toDo)
        return toDo.toDomain()
    }

    override fun editToDo(request: EditToDoRequest): ToDo {
        val toDo = repo.find(NitriteToDo::id eq request.id).firstOrNull() ?: throw ToDoNotFoundException(request.id)

        val newSearchIndex = buildSearchIndex(request.newContent,
            request.newTitle, *request.newTags.toTypedArray())

        val newToDo = toDo.copy(endDate = request.newEndDate, title = request.newTitle,
            content = request.newContent, tags = request.newTags,
            searchIndex = newSearchIndex)

        repo.update(NitriteToDo::id eq request.id, newToDo)

        return newToDo.toDomain()
    }


    override fun completeToDo(id: String): ToDo {
        val toDo = repo.find(NitriteToDo::id eq id).firstOrNull() ?: throw ToDoNotFoundException(id)

        val completedToDo = toDo.copy(completed = true)

        repo.update(NitriteToDo::id eq id, completedToDo)

        return completedToDo.toDomain()
    }

    override fun revertToDo(id: String): ToDo {
        val toDo = repo.find(NitriteToDo::id eq id).firstOrNull() ?: throw ToDoNotFoundException(id)

        val completedToDo = toDo.copy(completed = false)

        repo.update(NitriteToDo::id eq id, completedToDo)

        return completedToDo.toDomain()
    }

    override fun removeToDo(id: String): ToDo {
        val toDo = repo.find(NitriteToDo::id eq id).firstOrNull() ?: throw ToDoNotFoundException(id)

        repo.remove(NitriteToDo::id eq id)

        return toDo.toDomain()
    }

    override fun findToDosByTags(tags: Set<String>): List<ToDo> {
        return tags.map {
            repo.find(NitriteToDo::tags elemMatch (NitriteToDo::tags eq it)).toList().map { it.toDomain() }
        }.flatten()
    }

    override fun searchToDos(searchString: String): List<ToDo> {
        return repo.find(NitriteToDo::searchIndex text searchString).toList().map { it.toDomain() }
    }

    override fun getToDoById(id: String): Optional<ToDo> {
        val toDo = repo.find(NitriteToDo::id eq id).firstOrNull() ?: return Optional.empty()

        return Optional.of(toDo.toDomain())
    }

    override fun getExpiredToDos(date: LocalDateTime): List<ToDo> {
        return repo.find(ObjectFilters.and(
            NitriteToDo::endDate lt date,
            NitriteToDo::completed eq false)).toList().map { it.toDomain() }
    }

    override fun getActiveToDos(date: LocalDateTime): List<ToDo> {
        return repo.find(ObjectFilters.and(
            NitriteToDo::endDate gte date,
            NitriteToDo::completed eq false)).toList().map { it.toDomain() }
    }
}

@Indices(
    Index(value = "searchIndex", type = IndexType.Fulltext),
    Index(value = "title", type = IndexType.NonUnique),
    Index(value = "content", type = IndexType.NonUnique),
    Index(value = "endDate", type = IndexType.NonUnique),
    Index(value = "completed", type = IndexType.NonUnique)
)
data class NitriteToDo(
    @Id val id: String,
    val createdOn: LocalDateTime,
    val endDate: LocalDateTime,
    val title: String,
    val content: String,
    val tags: Set<String>,
    val searchIndex: String,
    val completed: Boolean = false
) {
    fun toDomain(): ToDo {
        return ToDo(
            this.id,
            this.endDate,
            this.title,
            this.content,
            this.tags,
            this.completed
        )
    }
}

private fun buildSearchIndex(vararg args: String): String {
    return args.joinToString(",")
}