/*
 * EffectTimerPlus
 * Copyright (C) 2026 TerminalMC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.terminalmc.effecttimerplus;

import dev.terminalmc.effecttimerplus.command.Commands;
import dev.terminalmc.effecttimerplus.gui.screen.ConfigScreenProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(
        value = EffectTimerPlus.MOD_ID,
        dist = Dist.CLIENT
)
public class EffectTimerPlusNeoForge {

    public EffectTimerPlusNeoForge() {
        // Register config screen
        ModLoadingContext.get().registerExtensionPoint(
                IConfigScreenFactory.class,
                () -> (mc, parent) -> ConfigScreenProvider.getConfigScreen(parent)
        );

        // Initialize client
        EffectTimerPlus.init();
    }

    @EventBusSubscriber(
            modid = EffectTimerPlus.MOD_ID,
            value = Dist.CLIENT
    )
    static class ClientEventHandler {

        /**
         * Registers all commands.
         */
        @SubscribeEvent
        public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
            Commands.register(event.getDispatcher(), event.getBuildContext());
        }
    }
}
