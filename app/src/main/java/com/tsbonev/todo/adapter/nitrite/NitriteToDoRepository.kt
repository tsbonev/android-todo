package com.tsbonev.todo.adapter.nitrite

import com.tsbonev.todo.core.*
import org.dizitart.kno2.filters.eq
import org.dizitart.kno2.filters.gte
import org.dizitart.kno2.filters.lt
import org.dizitart.kno2.filters.text
import org.dizitart.no2.IndexType
import org.dizitart.no2.Nitrite
import org.dizitart.no2.objects.Id
import org.dizitart.no2.objects.Index
import org.dizitart.no2.objects.Indices
import org.dizitart.no2.objects.ObjectRepository
import org.dizitart.no2.objects.filters.ObjectFilters
import org.dizitart.no2.objects.filters.ObjectFilters.not
import org.threeten.bp.LocalDateTime

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


    override fun add(request: AddToDoRequest): ToDo {
        val toDo = NitriteToDo(
            idGenerator.generateId(),
            request.createdOn,
            request.dueDate,
            request.content,
            ToDoStatus.CURRENT.name
        )

        repo.insert(toDo)
        return toDo.toDomain()
    }

    override fun edit(request: EditToDoRequest): ToDo {
        val toDo = repo.find(NitriteToDo::id eq request.id).firstOrNull() ?: throw ToDoNotFoundException(request.id)

        val newToDo = toDo.copy(dueDate = request.newDueDate,
            content = request.newContent)

        repo.update(NitriteToDo::id eq request.id, newToDo)

        return newToDo.toDomain()
    }


    override fun complete(id: String): ToDo {
        val toDo = repo.find(NitriteToDo::id eq id).firstOrNull() ?: throw ToDoNotFoundException(id)

        val completedToDo = toDo.copy(status = ToDoStatus.COMPLETED.name)

        repo.update(NitriteToDo::id eq id, completedToDo)

        return completedToDo.toDomain()
    }

    override fun revert(id: String): ToDo {
        val toDo = repo.find(NitriteToDo::id eq id).firstOrNull() ?: throw ToDoNotFoundException(id)

        val completedToDo = toDo.copy(status = ToDoStatus.CURRENT.name)

        repo.update(NitriteToDo::id eq id, completedToDo)

        return completedToDo.toDomain()
    }

    override fun remove(id: String): ToDo {
        val toDo = repo.find(NitriteToDo::id eq id).firstOrNull() ?: throw ToDoNotFoundException(id)

        repo.remove(NitriteToDo::id eq id)

        return toDo.toDomain()
    }

    override fun getById(id: String): ToDo? {
        val toDo = repo.find(NitriteToDo::id eq id).firstOrNull() ?: return null

        return toDo.toDomain()
    }

    override fun searchByContent(searchString: String, sortOrder: SortOrder, cursor: Cursor): List<ToDo> {
        return repo.find(NitriteToDo::content text  searchString).toList().map { it.toDomain() }
    }

    override fun getAllOverdue(cursor: Cursor, time: LocalDateTime): List<ToDo> {
        return repo.find(ObjectFilters.and(
            NitriteToDo::dueDate lt time,
            not(NitriteToDo::status eq ToDoStatus.COMPLETED.name))).toList().map { it.toDomain() }
    }

    override fun getAllCompleted(cursor: Cursor): List<ToDo> {
        return repo.find(NitriteToDo::status eq ToDoStatus.COMPLETED.name).toList().map { it.toDomain() }
    }

    override fun getAllCurrent(cursor: Cursor, time: LocalDateTime): List<ToDo> {
        return repo.find(ObjectFilters.and(
            NitriteToDo::dueDate gte time,
            not(NitriteToDo::status eq ToDoStatus.COMPLETED.name))).toList().map { it.toDomain() }
    }
}

@Indices(
    Index(value = "content", type = IndexType.Fulltext),
    Index(value = "dueDate", type = IndexType.NonUnique),
    Index(value = "status", type = IndexType.NonUnique)
)
data class NitriteToDo(
    @Id val id: String,
    val createdOn: LocalDateTime,
    val dueDate: LocalDateTime?,
    val content: String,
    val status: String
) {
    fun toDomain(): ToDo {
        return ToDo(
            this.id,
            this.content,
            this.dueDate,
            this.createdOn,
            ToDoStatus.valueOf(this.status)
        )
    }
}