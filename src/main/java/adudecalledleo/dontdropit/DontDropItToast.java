package adudecalledleo.dontdropit;

import java.util.List;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

// copy of SystemToast lmao
public class DontDropItToast implements Toast {
	public enum Type {
		DROP_DELAY_DISABLED_TOGGLED;

		private final long displayDuration;

		Type(long displayDuration) {
			this.displayDuration = displayDuration;
		}

		Type() {
			this(5000L);
		}

		public long getDisplayDuration() {
			return this.displayDuration;
		}
	}
	private static final Identifier TEXTURE = new Identifier("toast/advancement");

	private final Type type;
	private final int width;

	private Text title;
	private List<OrderedText> lines;
	private long startTime;
	private boolean justUpdated;


	public DontDropItToast(Type type, Text title, @Nullable Text description) {
		this(type, title, getTextAsList(description), Math.max(160,
				30 + Math.max(MinecraftClient.getInstance().textRenderer.getWidth(title),
						description == null ? 0 : MinecraftClient.getInstance().textRenderer.getWidth(description))));
	}

	private DontDropItToast(Type type, Text title, List<OrderedText> lines, int width) {
		this.type = type;
		this.title = title;
		this.lines = lines;
		this.width = width;
	}

	private static ImmutableList<OrderedText> getTextAsList(@Nullable Text text) {
		return text == null ? ImmutableList.of() : ImmutableList.of(text.asOrderedText());
	}

	@Override
	public int getWidth() {
		return this.width;
	}

	@Override
	public int getHeight() {
		return 20 + this.lines.size() * 12;
	}

	@Override
	public int getRequiredSpaceCount() {
		return Toast.super.getRequiredSpaceCount();
	}

	@Override
	public Visibility draw(DrawContext context, ToastManager manager, long startTime) {
		if (this.justUpdated) {
			this.startTime = startTime;
			this.justUpdated = false;
		}
		boolean textShadow = false;

		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		int width = this.getWidth();
		int height;
		if (width == 160 && this.lines.size() <= 1) {
			context.drawGuiTexture(TEXTURE, 0, 0, width, this.getHeight());
		} else {
			height = this.getHeight();
			int l = Math.min(4, height - 28);
			this.drawPart(context, width, 0, 0, 28);

			for (int m = 28; m < height - l; m += 10) {
				this.drawPart(context, width, 16, m, Math.min(16, height - m - l));
			}

			this.drawPart(context, width, 32 - l, height - l, l);
		}

		if (this.lines == null) {
			context.drawText(manager.getClient().textRenderer, this.title, 18, 12, -256, textShadow);
		} else {
			context.drawText(manager.getClient().textRenderer, this.title, 18, 7, -256, textShadow);

			for(height = 0; height < this.lines.size(); ++height) {
				context.drawText(manager.getClient().textRenderer, this.lines.get(height), 18, (18 + height * 12), 0xFFFFFFFF, textShadow);
			}
		}

		return startTime - this.startTime < this.type.getDisplayDuration() ? Visibility.SHOW : Visibility.HIDE;
	}

	private void drawPart(DrawContext context, int width, int textureV, int y, int height) {
		int i = textureV == 0 ? 20 : 5;
		int j = Math.min(60, width - i);
		context.drawGuiTexture(TEXTURE,0, y, i, height);

		for(int k = i; k < width - j; k += 64) {
			context.drawGuiTexture(TEXTURE, k, y, Math.min(64, width - k - j), height);
		}

		context.drawGuiTexture(TEXTURE, width - j, y, j, height);
	}

	public void setContent(Text title, @Nullable Text description) {
		this.title = title;
		this.lines = getTextAsList(description);
		this.justUpdated = true;
	}

	public static void add(ToastManager manager, Type type, Text title, @Nullable Text description) {
		manager.add(new DontDropItToast(type, title, description));
	}

	public static void show(ToastManager manager, Type type, Text title, @Nullable Text description) {
		var toast = (DontDropItToast) manager.getToast(DontDropItToast.class, type);
		if (toast == null) {
			add(manager, type, title, description);
		} else {
			toast.setContent(title, description);
		}
	}

	public static void showDropDelayDisabledToggled(ToastManager manager, boolean newValue) {
		show(manager, Type.DROP_DELAY_DISABLED_TOGGLED, Text.translatable("key.dontdropit.toggleDropDelay"),
				newValue
						? Text.translatable("dontdropit.toast.toggleDropDelay.disabled")
						: Text.translatable("dontdropit.toast.toggleDropDelay.enabled"));
	}

	@Override
	public Type getType() {
		return type;
	}
}
