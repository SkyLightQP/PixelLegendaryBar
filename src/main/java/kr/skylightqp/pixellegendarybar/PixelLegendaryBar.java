package kr.skylightqp.pixellegendarybar;

import com.google.inject.Inject;
import com.pixelmonmod.pixelmon.api.spawning.AbstractSpawner;
import com.pixelmonmod.pixelmon.spawning.LegendarySpawner;
import com.pixelmonmod.pixelmon.spawning.PixelmonSpawning;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.boss.BossBarColors;
import org.spongepowered.api.boss.BossBarOverlays;
import org.spongepowered.api.boss.ServerBossBar;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import java.util.concurrent.TimeUnit;

@Plugin(
        id = "pixellegendarybar",
        name = "PixelLegendaryBar",
        version = "1.0",
        description = "Showing left time about Legendary Pokemon to use bossbar",
        dependencies = {
                @Dependency(id = "pixelmon")
        }
)
public class PixelLegendaryBar {
    @Inject
    private PluginContainer pluginContainer;

    Task.Builder taskBuilder = Task.builder();

    private ServerBossBar prevBar;

    @Listener
    public void onServerStart(GamePreInitializationEvent event) {
        pluginContainer = Sponge.getPluginManager().getPlugin("pixellegendarybar").get();
    }

    @Listener
    public void onServerStart(GameStartedServerEvent e) {
        taskBuilder.execute(() -> {
            AbstractSpawner aSpawner = PixelmonSpawning.coordinator.getSpawner("legendary");
            LegendarySpawner lSpawner = (LegendarySpawner) aSpawner;

            long timeToGo = lSpawner.nextSpawnTime - System.currentTimeMillis();
            int minutes = (int) Math.ceil((float) timeToGo / 1000.0F / 60.0F);

            setBossbar("전설의 포켓몬 소환까지 " + minutes + "분...");
        }).interval(1, TimeUnit.SECONDS).submit(pluginContainer);
    }

    private void setBossbar(String content) {
        if (prevBar != null) {
            Sponge.getServer().getOnlinePlayers().forEach(prevBar::removePlayer);
        }

        ServerBossBar bar = ServerBossBar.builder()
                .color(BossBarColors.RED)
                .name(Text.of(content))
                .overlay(BossBarOverlays.PROGRESS)
                .percent(1f)
                .build();

        prevBar = bar;
        Sponge.getServer().getOnlinePlayers().forEach(bar::addPlayer);
    }
}
