/*
  Copyright 2011-2014 Red Hat, Inc

  This file is part of PressGang CCMS.

  PressGang CCMS is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  PressGang CCMS is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with PressGang CCMS.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.jboss.pressgang.ccms.utils.structures;

import java.util.ArrayList;
import java.util.List;

public class InjectionError {
    private final String injection;
    private List<String> messages = new ArrayList<String>();

    public InjectionError(final String injection) {
        this.injection = injection;
    }

    public String getInjection() {
        return injection;
    }

    public void addMessage(final String message) {
        messages.add(message);
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(final List<String> messages) {
        this.messages = messages;
    }
}
