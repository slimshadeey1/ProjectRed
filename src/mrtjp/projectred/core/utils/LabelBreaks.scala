package mrtjp.projectred.core.utils

import scala.util.control.ControlThrowable

class LabelBreaks
{
    val throwable = new LabelThrowable()

    def label(tag:String)(op: => Any):Unit =
    {
        try
        {
            op
        }
        catch
        {
            case ex:LabelThrowable if ex.ident.equals(tag) => /** broken **/
            case ex:Throwable => throw ex
        }
    }

    def break(tag:String):Unit = throw throwable(tag)

    //Shorthand single break
    def label(op: => Any):Unit = label("$1")(op)
    def break():Unit = break("$1")
}

class LabelThrowable extends ControlThrowable
{
    var ident = ""

    def apply(tag:String) =
    {
        ident = tag
        this
    }
}

object LabelBreaks extends LabelBreaks