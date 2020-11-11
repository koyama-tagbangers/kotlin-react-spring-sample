import kotlinx.html.js.onClickFunction
import react.RBuilder
import react.RProps
import react.child
import react.dom.button
import react.dom.div
import react.functionalComponent

interface TodoProps : RProps {
    var todo: Todo
    var onToggle: () -> Unit
    var onDelete: () -> Unit
    var loading: Boolean
}

val todoCard = functionalComponent<TodoProps> { props ->
    div {
        button {
            attrs {
                disabled = props.loading
                onClickFunction = { props.onToggle() }
            }
            +"${if (props.todo.done) '✅' else '⬜'}"
        }
        +"${props.todo.content} (id: ${props.todo.id})"
        button {
            attrs {
                disabled = props.loading
                onClickFunction = { props.onDelete() }
            }
            +"DELETE"
        }
    }
}

fun RBuilder.todoCard(handler: TodoProps.() -> Unit) = child(todoCard) {
    attrs { handler() }
}
