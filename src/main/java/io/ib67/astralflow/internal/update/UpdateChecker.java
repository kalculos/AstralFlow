/*
 *
 *   AstralFlow - The plugin enriches bukkit servers
 *   Copyright (C) 2022 The Inlined Lambdas and Contributors
 *
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 *   USA
 */

package io.ib67.astralflow.internal.update;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.ib67.astralflow.internal.AstralConstants;
import io.ib67.internal.util.bukkit.Log;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static io.ib67.astralflow.util.LogCategory.UPDATE_CHECKER;

@RequiredArgsConstructor
public final class UpdateChecker extends BukkitRunnable {
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final int BUILD = 2;
    private static final Pattern REGEX = Pattern.compile("build\\+(?=\\d+)(\\d+)");
    public static List<String> updateMessages;
    private final String sourceUrl;
    private final String pluginVersion;

    @Override
    public void run() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(sourceUrl))
                .header("User-Agent", "AstralFlow Update Checker/" + pluginVersion + " , Like Gecko")
                .GET().build();
        try {
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
            var r1 = JsonParser.parseString(response).getAsJsonArray();
            int counter = 0;
            for (JsonElement ErelInf : r1) {
                if (counter > 30) { // only search for the latest 15 formal updates
                    Log.warn(UPDATE_CHECKER, "Your AstralFlow is too old to check more updates. Please update to the latest version.");
                    return;
                }
                var relInf = ErelInf.getAsJsonObject();
                var tagName = relInf.get("tag_name").getAsString();
                var latestRel = relInf.get("name").getAsString();
                var preRel = relInf.get("prerelease").getAsBoolean();
                var draft = relInf.get("draft").getAsBoolean();
                if (preRel || draft) {
                    continue; // skip this
                }
                // search build number
                var matcher = REGEX.matcher(tagName);
                if (!matcher.find()) {
                    continue;
                }
                var build = Integer.parseInt(matcher.group(1));
                if (build > BUILD) {
                    var messageComposer = new ArrayList<String>();
                    // update available
                    messageComposer.add(ChatColor.GREEN + "A new update is available: " + latestRel);
                    var body = relInf.get("body").getAsString();
                    String[] strings = body.split("\\r\\n"); // github does...
                    for (int i = 0; i < strings.length; i++) {
                        if (i > 3) {
                            messageComposer.add("... and more.");
                            break;
                        }
                        messageComposer.add(strings[i]);
                    }
                    messageComposer.add(ChatColor.GREEN + "You can download it from: " + ChatColor.AQUA + ChatColor.UNDERLINE + relInf.get("html_url").getAsString());
                    updateMessages = messageComposer;
                    var targetPlayers = Bukkit.getOnlinePlayers().stream().filter(e -> e.hasPermission("astralflow.notification.update")).toList();

                    for (String updateMessage : updateMessages) {
                        Log.info(UPDATE_CHECKER, updateMessage);
                        targetPlayers.forEach(e -> e.sendMessage(updateMessage));
                    }
                    return; // end
                }
                counter++;
            }
            // latest version!
            updateMessages = Collections.emptyList();
            if (AstralConstants.DEBUG) Log.info(UPDATE_CHECKER, "No updates available.");
        } catch (InterruptedException | IOException e) {
            Log.warn(UPDATE_CHECKER, "Failed to check for updates. " + e.getMessage());
            Log.warn(UPDATE_CHECKER, "Do we connected to the internet?");
        }
    }
}
