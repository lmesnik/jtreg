/*
 * Copyright (c) 2025, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.sun.javatest.regtest.config;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

public final class LibraryProperties {

    private final boolean isSharedLibrary;

    private final List<String> modules;

    private final boolean enablePreview;

    private final List<String> javacOptions;

    private final List<String> dependencies;

    private static final LibraryProperties privateLibraryProperties
            = new LibraryProperties();

    private LibraryProperties() {
        isSharedLibrary = false;
        enablePreview = false;
        modules = List.of();
        javacOptions = List.of();
        dependencies = List.of();
    }

    public boolean isSharedLibrary() {
        return isSharedLibrary;
    }

    public List<String> getRequiredModules() {
        return modules;
    }
    
    public boolean enablePreview() {
        return enablePreview;
    }

    public List<String> getJavacOptions() {
        return javacOptions;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public static LibraryProperties of(Path root) throws UncheckedIOException {
        if (Files.isDirectory(root)) {
            Path file = root.resolve("LIBRARY.ROOT");
            if (Files.exists(file)) {
                return new LibraryProperties(file);
            }
        }
        return privateLibraryProperties();
    }

    public static LibraryProperties privateLibraryProperties() {
        return privateLibraryProperties;
    }
    private LibraryProperties(Path libRoot) throws UncheckedIOException {
        isSharedLibrary = true;
        Properties properties = new Properties();
        try (var stream = Files.newInputStream(libRoot)) {
            properties.load(stream);
            String modulesLine = properties.getProperty("jdk.modules", "");
            modules = List.of(modulesLine.split(" "));

            enablePreview = Boolean.parseBoolean(properties.getProperty("enablePreview", "false"));

            String javacOptionsLine = properties.getProperty("javacOptions", "");
            javacOptions = List.of(javacOptionsLine.split(" "));

            String libRootsLine = properties.getProperty("dependencies", "");
            dependencies = List.of(libRootsLine.split(" "));

        } catch (IOException exception) {
            throw new UncheckedIOException("Reading from file failed: " + libRoot.toUri(), exception);
        }
    }


}