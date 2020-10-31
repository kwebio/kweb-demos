package kweb.demos.helloWorld

import kweb.DivElement
import kweb.ElementCreator
import kweb.div
import kweb.new
import kweb.plugins.fomanticUI.fomantic
import kweb.state.KVar
import kweb.state.render
import kweb.util.gson

class ModalViewOptions(
        val autofocus: Boolean=true
)

class ModalView(val ec: ElementCreator<*>, val id: String = (modalCounter++).toString(), var isOpen: KVar<Boolean> = KVar(false), val autoFocus: Boolean=true) {
    companion object {
        var modalCounter: Int = 0
    }
    val options = ModalViewOptions(autofocus=autoFocus)


    fun close(){
        ec.browser.evaluate("""
            console.log("Hiding Modal " + ${this.id})
            $('#${this.id}').modal(${gson.toJson(options)}).modal('hide');
        """.trimIndent())

        isOpen.value = false
    }

    fun open(){
        isOpen.value = true
        ec.browser.evaluate("""
            $('#${this.id}').modal(${gson.toJson(options)}).modal('show',${gson.toJson(options)});
        """.trimIndent())
    }
}

fun ElementCreator<*>.modal(header: String, autoFocus: Boolean=true, content: ElementCreator<DivElement>.(modal: ModalView) -> Unit) : ModalView {
    val mv = ModalView(this, autoFocus = autoFocus)

    val classes = fomantic.ui.modal
    render(mv.isOpen){isOpen ->
        if(isOpen) {
            div(classes.plus("id" to mv.id)).new {
                div(fomantic.ui.header).text(header)
                div(fomantic.ui.content).new {
                    content(mv)
                }
            }
        }
    }

    return mv
}