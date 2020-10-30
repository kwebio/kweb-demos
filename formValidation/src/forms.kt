package kweb.demos.helloWorld

import com.github.salomonbrys.kotson.fromJson
import kweb.*
import kweb.plugins.fomanticUI.fomantic
import kweb.state.KVar
import kweb.state.render
import kweb.util.gson
import kweb.util.random
import kotlin.math.abs

class FormControl{

    private val inputs: MutableList<FormInput> = mutableListOf()
    val errors: KVar<List<String>> = KVar(listOf())
    val formLevelValidations = mutableListOf<()->String?>()

    fun add(fi:FormInput){
        inputs.add(fi)
    }

    private fun validate(){
        logger.info("Running validations")
        errors.value = inputs.mapNotNull { it.checkInput() }.plus(formLevelValidations.mapNotNull { it.invoke() }).also {
            it.forEach { error->
                logger.warn(error)
            }
            logger.info("Done. Found ${it.size} errors")
        }
    }

    val isValid: Boolean
        get() {
            validate()
            return errors.value.isEmpty()
        }

    fun withValidation(func: ()->String?){
        formLevelValidations.add(func)
    }
}

fun ElementCreator<*>.formControl(block: ElementCreator<*>.(form:FormControl)->Unit) : FormControl {
    val fc = FormControl()

    form(fomantic.ui.form).new(){
        fc.errors.addListener { old, new ->
            logger.info("Form Error state did change.")

            if(old.isNotEmpty() && new.isEmpty()){
                this.parent.removeClasses("error")
                logger.info("Removing Error class")
            }
            if(old.isEmpty() && new.isNotEmpty()){
                this.parent.addClasses("error")
                logger.info("Adding Error class")
            }
        }

        block(fc)
    }

    return fc
}

typealias ValidationFunc = (String?)->String?

interface FormInput {
    val actualValue: KVar<String>
    val isRequired: Boolean
    val label: String?
    val errorMessage: KVar<String?>
    val inputElement: Element

    fun runValidation(): String?
}

inline fun <T : FormInput> T.with(fc: FormControl) : T{
    fc.add(this)
    return this
}

fun FormInput.checkInput() : String?{
    errorMessage.value = runValidation()
    return errorMessage.value
}

class BasicFormInput(
        override val actualValue: KVar<String>,
        override val isRequired:Boolean,
        override val label:String?,
        private var validator: ValidationFunc?=null,
        override val errorMessage: KVar<String?> = KVar(null),
        private var inputMissingErrorMessage: String? = null) : FormInput{

    private lateinit var _inputElement: Element

    override val inputElement: Element
        get() = _inputElement


    fun setInputElement(e: Element){
        this._inputElement = e
    }


    fun withInputMissingErrorMessage(message:String) : FormInput{
        inputMissingErrorMessage = message
        return this
    }

    override fun runValidation(): String? {
        if(isRequired && actualValue.value.isBlank()){
            return inputMissingErrorMessage ?: "The Field '${label}' is required"
        }
        return validator?.invoke(actualValue.value)
    }

    fun validate(func: ValidationFunc): FormInput{
        validator = func
        return this
    }
}

fun ElementCreator<*>.formInput(label: String?=null, placeholder:String?=null, required:Boolean=false, bindTo: KVar<String>, inputType: InputType = InputType.text): BasicFormInput = absFormInput(label, required, bindTo){
    lateinit var input: InputElement
    div(fomantic.ui.input).new() {
        input = input(inputType, placeholder = placeholder).apply { value=bindTo }
    }
    input
}

fun ElementCreator<*>.checkBoxInput(label:String, bindTo: KVar<Boolean>) {
    val bindToStr = KVar(bindTo.toString())
    bindToStr.addListener { oldVal, newVal ->
        val currentVal = newVal.toBoolean()
        bindTo.value = currentVal
    }
    div(fomantic.field).new {
        render(bindTo){isChecked->
            div(fomantic.ui.checkbox.checked(bindTo.value))
                .apply {
                    on.click {
                        bindTo.value = !bindTo.value
                    }
                }
                .new {
                    input(InputType.checkbox,attributes = mapOf("class" to "hidden")).apply {
                        if(isChecked) {
                            checked(true)
                        }
                    }
                    label().text(label)
                }
        }
    }
}

fun ElementCreator<*>.absFormInput(label: String?=null, required:Boolean=false, bindTo: KVar<String>, inputElementFunc: ElementCreator<*>.()-> InputElement) : BasicFormInput{
    val formInput = BasicFormInput(bindTo, required, label)

    div(fomantic.ui.field).new {

        formInput.errorMessage.addListener { old, newError ->
            if(newError!=null){
                this.parent.addClasses("error")
            }
            else{
                this.parent.removeClasses("error")
            }
        }

        label?.let {
            label().text(label)
        }

        val input = inputElementFunc()
        formInput.setInputElement(input)
    }
    return formInput
}

class DropdownValueSelectEvent(val selectedValue: String?, val selectedText: String?)
class DropdownElement {
    internal val callbacks: MutableList<(key: String?)->Unit> = mutableListOf()
    fun onSelect(callback:(key: String?)->Unit)
    {
        callbacks.add(callback)
    }
}

fun ElementCreator<*>.dropdownField(label: String, options: Map<String?, String>, currentValue: KVar<String?> = KVar(null)) : DropdownElement{
    lateinit var element : DropdownElement
    div(fomantic.ui.field).new {
        label().text(label)

        element = dropdown(options, currentValue)
    }
    return element
}

fun ElementCreator<*>.dropdown(options: Map<String?, String>, currentValue: KVar<String?> = KVar(null)):DropdownElement {

    val result = DropdownElement()

    val dropdown = div(fomantic.ui.selection.dropdown)
    dropdown.new {
        input(type=InputType.hidden, name="dropdown", initialValue = currentValue.value)
        i(fomantic.icon.dropdown)
        div(fomantic.text.default).text("Select")
        div(fomantic.menu).new{
            options.forEach { (key, displayText) ->
                div(fomantic.item).apply { this.setAttributeRaw("data-value", key) }.text(displayText)
            }
        }
    }

    val callbackId = abs(random.nextInt())
    browser.executeWithCallback("""
        $('#${dropdown.id}').dropdown({
            action: 'activate',
            onChange: function(value, text) {
              callbackWs($callbackId,{selectedValue: value, selectedText: text});
            }
        });
        """.trimIndent(), callbackId) {inputData->
        val selectedData : DropdownValueSelectEvent = gson.fromJson(inputData.toString())
        if(currentValue != null) {
            currentValue.value = selectedData.selectedValue ?: ""
        }
        result.callbacks.forEach{cb->cb.invoke(selectedData.selectedValue)}
    }

    return result
}

fun ElementCreator<*>.radioInput(label:String?=null, options: Map<String,String>, required: Boolean=false, isInline:Boolean=false, bindTo: KVar<String>) : BasicFormInput {
    val formInput = BasicFormInput(bindTo, required, label)

    render(bindTo){
        div(fomantic.ui.fields.inline(isInline).grouped).new {
            formInput.errorMessage.addListener { old, newError ->
                if(newError!=null){
                    this.parent.addClasses("error")
                }
                else{
                    this.parent.removeClasses("error")
                }
            }
            label?.let {
                label().text(label)
            }
            options.forEach{ (labelName, labelValue) ->
                div(fomantic.ui.field).new {
                    div(fomantic.ui.radio.checkbox.checked(labelValue == bindTo.value)).apply {
                        on.click {
                            bindTo.value = labelValue
                        }
                    }.new {
                        input(type = InputType.radio, name = labelValue, attributes = mapOf("class" to "hidden")).apply {
                            if(labelValue == bindTo.value){
                                checked(true)
                            }
                        }
                        label().text(labelName)
                    }
                }
            }
        }
    }

    return formInput
}
