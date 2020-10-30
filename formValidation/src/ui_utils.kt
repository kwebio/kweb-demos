package kweb.demos.helloWorld

import kweb.*
import kweb.plugins.fomanticUI.FomanticUIClasses
import kweb.plugins.fomanticUI.fomantic

val FomanticUIClasses.server : FomanticUIClasses
    get() {
        classes("server")
        return this
    }

fun ElementCreator<*>.centeredBox(contentBlock: ElementCreator<DivElement>.()->Unit){

    this.browser.doc.head.new(){
        element("style", mapOf("type" to "text/css")).innerHTML("""
            body {
              background-color: #DADADA;
            }
            #K1 {
              height: 100%;    
            }
            .grid{
              height: 100%;
            }
            .image {
              margin-top: -100px;
            }
            .column {
              max-width: 450px;
            }
            .footer {
                clear: both;
                position: relative;
                height: 20px;
                margin-top: -15px;
                width: 100%;
                display: table;
                text-align: center;
            }
            .footerContent {
                display: table-cell;
                vertical-align: middle;
            }
        """.trimIndent())
    }

    div(fomantic.ui.middle.aligned.center.aligned.grid).new {

        div(fomantic.left.aligned.column).new(){
            div(fomantic.ui.segment).new(){
                contentBlock(this)
            }
        }
    }

    div(attributes = mapOf("class" to "footer")).new{
        div(attributes = mapOf("class" to "footerContent")).new{
            div(fomantic.ui.label).new {
                i(fomantic.ui.icon.server)
                span().text("Powered by KWeb")
            }
        }
    }
}

fun FomanticUIClasses.checked(isChecked: Boolean): FomanticUIClasses {
    if(isChecked){
        classes("checked")
    }
    return this
}

fun FomanticUIClasses.inline(isInline: Boolean):FomanticUIClasses{
    if(isInline){
        classes("inline")
    }
    return this
}

val FomanticUIClasses.default : FomanticUIClasses
    get() {
        classes("default")
        return this
    }