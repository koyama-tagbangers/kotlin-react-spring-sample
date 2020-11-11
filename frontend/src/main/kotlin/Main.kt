import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.w3c.fetch.RequestInit
import react.*
import react.dom.div
import react.dom.h1
import react.dom.render
import kotlin.js.json

@Serializable
data class Todo(
        var id: Long = 0,
        var content: String = "",
        var done: Boolean = false
)

val app = functionalComponent<RProps> {
    val (loading, setLoading) = useState(true)
    val (todoList, setTodoList) = useState(emptyList<Todo>())

    val toggleSummit: (String, () -> Unit) -> Unit = { content, onSucceed ->
        GlobalScope.launch {
            try {
                setLoading(true)
                val data = window.fetch("/api/todo", object : RequestInit {
                    override var method: String? = "POST"
                    override var headers = json().apply {
                        this["Content-Type"] = "application/json"
                    }
                    override var body = Json.encodeToString(Todo.serializer(), Todo(content = content))
                })
                        .await()
                        .text()
                        .await()
                        .let { Json.decodeFromString(Todo.serializer(), it) }
                setTodoList(todoList.toMutableList().apply { add(data) })
                onSucceed()
            } catch (err: Throwable) {
                throw err
            } finally {
                setLoading(false)
            }
        }
    }

    val toggleHandle: (Int) -> () -> Unit = { index ->
        {
            GlobalScope.launch {
                try {
                    val data = todoList[index].copy().apply { this.done = !this.done }
                    setLoading(true)
                    window.fetch("/api/todo/${data.id}", object : RequestInit {
                        override var method: String? = "PUT"
                        override var headers = json().apply {
                            this["Content-Type"] = "application/json"
                        }
                        override var body = Json.encodeToString(Todo.serializer(), data)
                    })
                            .await()
                            .text()
                            .await()
                    setTodoList(todoList.toMutableList().apply { this[index] = data })
                } catch (err: Throwable) {
                    throw err
                } finally {
                    setLoading(false)
                }
            }
        }
    }

    val toggleDelete: (Int) -> () -> Unit = { index ->
        {
            GlobalScope.launch {
                try {
                    val data = todoList[index]
                    setLoading(true)
                    window.fetch("/api/todo/${data.id}", object : RequestInit {
                        override var method: String? = "DELETE"
                    })
                            .await()
                            .text()
                            .await()
                    setTodoList(todoList.toMutableList().apply {
                        remove(data)
                    })
                } catch (err: Throwable) {
                    throw err
                } finally {
                    setLoading(false)
                }
            }
        }
    }

    useEffect(emptyList()) {
        GlobalScope.launch {
            try {
                setLoading(true)
                val data = window.fetch("/api/todo")
                        .await()
                        .text()
                        .await()
                        .let { Json.decodeFromString(ListSerializer(Todo.serializer()), it) }
                setTodoList(data)
            } catch (err: Throwable) {
                throw err
            } finally {
                setLoading(false)
            }
        }
    }

    div {
        inputForm {
            this.loading = loading
            onSubmit = toggleSummit
        }
        todoList.forEachIndexed { index, todo ->
            todoCard {
                key = todo.id.toString()
                this.todo = todo
                this.loading = loading
                onToggle = toggleHandle(index)
                onDelete = toggleDelete(index)
            }
        }
    }
}

fun main() {
    render(document.getElementById("root")) {
        h1 {
            +"React + Spring Todo App in React"
        }
        child(app)
    }
}
