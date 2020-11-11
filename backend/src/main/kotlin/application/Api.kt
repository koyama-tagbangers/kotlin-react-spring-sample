package application

import org.springframework.data.repository.CrudRepository
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class Todo(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,
        val content: String = "",
        val done: Boolean = false
)

interface TodoRepository : CrudRepository<Todo, Long>

@RestController
@RequestMapping("/api/todo")
class TodoController(private val repository: TodoRepository) {
    @GetMapping
    fun findAll() = repository.findAll()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun save(@RequestBody todo: Todo) = repository.save(todo)

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(@RequestBody todo: Todo, @PathVariable id: Long) = repository.findById(id).ifPresent {
        repository.save(todo.copy(id = id))
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteById(@PathVariable id: Long) = repository.deleteById(id)
}
