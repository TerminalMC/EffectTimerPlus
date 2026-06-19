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

import dev.terminalmc.effecttimerplus.config.Config;
import dev.terminalmc.effecttimerplus.util.Logging;
import org.apache.logging.log4j.Logger;

public class EffectTimerPlus {

    public static final String MOD_ID = "effecttimerplus";
    public static final String MOD_NAME = "EffectTimerPlus";
    public static final Logger LOG = Logging.getLogger(MOD_ID);

    private EffectTimerPlus() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    public static void init() {
        Config.getAndSave();
    }

    /**
     * Config save listener.
     */
    public static void onConfigSaved(Config config) {
        // If you are maintaining caches based on config, update them here.
    }
}
