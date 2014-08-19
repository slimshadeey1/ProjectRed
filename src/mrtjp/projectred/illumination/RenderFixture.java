package mrtjp.projectred.illumination;

import codechicken.lib.lighting.LightModel;
import codechicken.lib.render.*;
import codechicken.lib.vec.*;
import mrtjp.projectred.ProjectRedIllumination;
import mrtjp.projectred.core.InvertX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

import java.util.Map;

public class RenderFixture implements IItemRenderer
{
    public static RenderFixture instance = new RenderFixture();

    private static CCModel[] base;

    public static final Cuboid6 lightBounds[] = new Cuboid6[6];

    static
    {
        Map<String, CCModel> models = CCModel.parseObjModels(new ResourceLocation("projectred", "textures/obj/lights/fixture.obj"), 7, new InvertX());
        base = new CCModel[6];
        for (int i = 0; i < 6; i++)
        {
            CCModel m = models.get("base").copy();
            m.apply(new Translation(.5, 0, .5));
            m.apply(Rotation.sideOrientation(i, 0).at(Vector3.center));
            m.computeLighting(LightModel.standardLightModel);
            m.shrinkUVs(0.0005);
            base[i] = m.copy();

            Transformation t = Rotation.sideRotations[i].at(Vector3.center);
            lightBounds[i] = new Cuboid6(5 / 32D, 0, 5 / 32D, 27 / 32D, 17 / 32D, 27 / 32D).apply(t).expand(-0.001);
        }
    }

    public void renderFixture(FixturePart l)
    {
        Icon icon = l.isOn() ? RenderLantern.onIcons[l.type] : RenderLantern.offIcons[l.type];
        TextureUtils.bindAtlas(0);
        CCRenderState.reset();
        CCRenderState.setBrightness(l.world(), l.x(), l.y(), l.z());
        CCRenderState.useModelColours(true);
        renderPart(icon, base[l.side], l.x(), l.y(), l.z());
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
        int color = item.getItemDamage();
        boolean on = item.getItem() == ProjectRedIllumination.itemPartInvFixture();

        switch (type) {
        case ENTITY:
            renderInventory(on, color, -0.25D, 0D, -0.25D, 0.75D);
            return;
        case EQUIPPED:
            renderInventory(on, color, -0.15D, -0.15D, -0.15D, 1.5D);
            return;
        case EQUIPPED_FIRST_PERSON:
            renderInventory(on, color, -0.15D, -0.15D, -0.15D, 1.5D);
            return;
        case INVENTORY:
            renderInventory(on, color, 0D, -0.05D, 0D, 1D);
            return;
        default:
            return;
        }
    }

    public void renderInventory(boolean on, int color, double x, double y, double z, double scale)
    {
        if (color > 15 || color < 0) return;

        Icon icon = on ? RenderLantern.onIcons[color] : RenderLantern.offIcons[color];

        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glScaled(scale, scale, scale);
        CCRenderState.reset();
        TextureUtils.bindAtlas(0);
        CCRenderState.useNormals(true);
        CCRenderState.startDrawing(7);
        renderPart(icon, base[0], x, y, z);
        CCRenderState.draw();
        if (on)
        {
            RenderHalo.prepareRenderState();
            RenderHalo.renderHalo(Tessellator.instance, lightBounds[0], color, new Translation(x, y, z));
            RenderHalo.restoreRenderState();
        }
        GL11.glPopMatrix();
    }

    public void renderPart(Icon icon, CCModel cc, double x, double y, double z)
    {
        cc.render(0, cc.verts.length, new Translation(x, y, z), new IconTransformation(icon), ColourModifier.instance);
    }
}
