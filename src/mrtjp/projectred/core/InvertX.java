package mrtjp.projectred.core;

import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Transformation;
import codechicken.lib.vec.VariableTransformation;
import codechicken.lib.vec.Vector3;

public class InvertX extends VariableTransformation
{
    public InvertX()
    {
        super(new Matrix4(1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 1));
    }

    @Override
    public void apply(Vector3 vec)
    {
        vec.x = vec.x * -1;
    }

    @Override
    public Transformation inverse()
    {
        return this;
    }
}
