/*
 *
 *   AstralFlow - The plugin who is turning bukkit into mod-pack
 *   Copyright (C) 2022 iceBear67
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

package io.ib67.astralflow.api.external;

import lombok.Builder;

import java.util.Objects;

/**
 * Information about a {@link AstralExtension}
 *
 * @param extensionName        name of extension. *not nullable*
 * @param issueTrackerUrl      an url linked to feedback pages, nullable
 * @param extensionDescription description for the extension, nullable
 * @param extensionVersion     version of the extension, *not nullable*.
 * @param extensionAuthors     authors, nullable
 */
public record ExtensionInfo(
        String extensionName,
        String issueTrackerUrl,
        String extensionDescription,
        String extensionVersion,
        String[] extensionAuthors
) {
    @Builder
    public ExtensionInfo {
        Objects.requireNonNull(extensionName, "extensionName cannot be null");
        Objects.requireNonNull(extensionVersion, "extensionVersion cannot be null");
        if (issueTrackerUrl == null || issueTrackerUrl.isEmpty()) {
            issueTrackerUrl = "Not Available.";
        }
        if (extensionDescription == null || extensionDescription.isEmpty()) {
            extensionDescription = "Not Available.";
        }
        if (extensionAuthors == null || extensionAuthors.length == 0) {
            extensionAuthors = new String[]{"Unknown"};
        }
    }

    @Override
    public String toString() {
        return String.format("%s v%s by %s", extensionName, extensionVersion, extensionAuthors);
    }
}
