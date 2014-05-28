/*
 * Copyright (c) 2010-2013, MoPub Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *  Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 *  Neither the name of 'MoPub Inc.' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.adform.sdk2.mraid;

import com.adform.sdk2.mraid.commands.MraidBaseCommand;
import com.adform.sdk2.mraid.commands.MraidCommandClose;
import com.adform.sdk2.mraid.commands.MraidCommandOpen;
import com.adform.sdk2.view.inner.AdWebView;

import java.util.Map;

public class MraidCommandFactory {
    protected static MraidCommandFactory instance = new MraidCommandFactory();

    public enum MraidJavascriptCommand {
        CLOSE("close"),
        EXPAND("expand"),
        OPEN("open"),
        RESIZE("resize"),
        UNSPECIFIED("");

        private String mCommand;

        private MraidJavascriptCommand(String command) {
            mCommand = command;
        }

        private static MraidJavascriptCommand fromString(String string) {
            for (MraidJavascriptCommand command : MraidJavascriptCommand.values()) {
                if (command.mCommand.equals(string)) {
                    return command;
                }
            }

            return UNSPECIFIED;
        }

        public String getCommand() {
            return mCommand;
        }
    }

    public static MraidBaseCommand create(String command, Map<String, String> params, AdWebView view) {
        return instance.internalCreate(command, params, view);
    }

    protected MraidBaseCommand internalCreate(String command, Map<String, String> params, AdWebView view) {
        MraidJavascriptCommand mraidJavascriptCommand = MraidJavascriptCommand.fromString(command);

        switch (mraidJavascriptCommand) {
            case OPEN:
                return new MraidCommandOpen(params, view);
            case CLOSE:
                return new MraidCommandClose(params, view);
            case UNSPECIFIED:
                return null;
            default:
                return null;
        }
    }
}
