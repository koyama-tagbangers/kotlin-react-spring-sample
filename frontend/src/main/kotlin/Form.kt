import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onSubmitFunction
import org.w3c.dom.HTMLInputElement
import react.*
import react.dom.form
import react.dom.input

interface FormProps : RProps {
    var loading: Boolean
    var onSubmit: (String, () -> Unit) -> Unit
}

val inputForm = functionalComponent<FormProps> { props ->
    val (inputText, setInputText) = useState("")

    form {
        attrs {
            onSubmitFunction = {
                props.onSubmit(inputText) {
                    setInputText("")
                }
                it.preventDefault()
            }
        }
        input {
            attrs {
                type = InputType.text
                value = inputText
                disabled = props.loading
                onChangeFunction = {
                    setInputText((it.target as HTMLInputElement).value)
                }
            }
        }
        input {
            attrs {
                type = InputType.submit
                value = "Add"
                disabled = inputText.isBlank() or props.loading
            }
        }
    }
}

fun RBuilder.inputForm(handler: FormProps.() -> Unit) = child(inputForm) {
    attrs { handler() }
}
