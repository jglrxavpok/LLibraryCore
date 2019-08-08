package net.ilexiconn.llibrary.client.event;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.model.ModelPlayer;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@OnlyIn(Dist.CLIENT)
public class RenderArmEvent extends Event {
    private AbstractClientPlayer player;
    private RenderPlayer renderPlayer;
    private ModelPlayer model;
    private EnumHandSide side;

    RenderArmEvent(AbstractClientPlayer player, RenderPlayer renderPlayer, ModelPlayer model, EnumHandSide side) {
        this.player = player;
        this.renderPlayer = renderPlayer;
        this.model = model;
        this.side = side;
    }

    public AbstractClientPlayer getPlayer() {
        return this.player;
    }

    public RenderPlayer getRenderPlayer() {
        return this.renderPlayer;
    }

    public ModelPlayer getModel() {
        return this.model;
    }

    public EnumHandSide getSide() {
        return this.side;
    }

    @Cancelable
    public static class Pre extends RenderArmEvent {
        public Pre(AbstractClientPlayer player, RenderPlayer renderPlayer, ModelPlayer model, EnumHandSide side) {
            super(player, renderPlayer, model, side);
        }
    }

    public static class Post extends RenderArmEvent {
        public Post(AbstractClientPlayer player, RenderPlayer renderPlayer, ModelPlayer model, EnumHandSide side) {
            super(player, renderPlayer, model, side);
        }
    }

    @Deprecated
    public static class Left extends RenderArmEvent {
        Left(AbstractClientPlayer player, RenderPlayer renderPlayer, ModelPlayer model) {
            super(player, renderPlayer, model, EnumHandSide.LEFT);
        }

        @Deprecated
        @Cancelable
        public static class Pre extends Left {
            public Pre(AbstractClientPlayer player, RenderPlayer renderPlayer, ModelPlayer model) {
                super(player, renderPlayer, model);
            }
        }

        @Deprecated
        public static class Post extends Left {
            public Post(AbstractClientPlayer player, RenderPlayer renderPlayer, ModelPlayer model) {
                super(player, renderPlayer, model);
            }
        }
    }

    @Deprecated
    public static class Right extends RenderArmEvent {
        Right(AbstractClientPlayer player, RenderPlayer renderPlayer, ModelPlayer model) {
            super(player, renderPlayer, model, EnumHandSide.RIGHT);
        }

        @Deprecated
        @Cancelable
        public static class Pre extends Right {
            public Pre(AbstractClientPlayer player, RenderPlayer renderPlayer, ModelPlayer model) {
                super(player, renderPlayer, model);
            }
        }

        @Deprecated
        public static class Post extends Right {
            public Post(AbstractClientPlayer player, RenderPlayer renderPlayer, ModelPlayer model) {
                super(player, renderPlayer, model);
            }
        }
    }
}
