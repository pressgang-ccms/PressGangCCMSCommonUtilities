/*
  Copyright 2011-2014 Red Hat

  This file is part of PresGang CCMS.

  PresGang CCMS is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  PresGang CCMS is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with PresGang CCMS.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.jboss.pressgang.ccms.utils.structures;

import java.util.ArrayList;

import org.jboss.pressgang.ccms.utils.common.CollectionUtilities;
import org.w3c.dom.Node;

/**
 * This class is used to map a translation string to collections of XML Nodes.
 * This way the Nodes can be replaced with the XML formed by the translation
 * string.
 */
public class StringToNodeCollection {
    private String translationString;
    /**
     * The translationString may be unique, while mapping to several sequences
     * of nodes
     */
    private ArrayList<ArrayList<Node>> nodeCollections;

    public ArrayList<ArrayList<Node>> getNodeCollections() {
        return nodeCollections;
    }

    public void setNodeCollections(ArrayList<ArrayList<Node>> nodeCollections) {
        this.nodeCollections = nodeCollections;
    }

    public String getTranslationString() {
        return translationString;
    }

    public void setTranslationString(String translationString) {
        this.translationString = translationString;
    }

    public StringToNodeCollection() {

    }

    public StringToNodeCollection(final String translationString, final ArrayList<ArrayList<Node>> nodeCollections) {
        this.translationString = translationString;
        this.nodeCollections = nodeCollections;
    }

    public StringToNodeCollection(final String translationString, final Node node) {
        this.translationString = translationString;
        addNode(node);
    }

    public StringToNodeCollection(final String translationString) {
        this.translationString = translationString;
    }

    public StringToNodeCollection addNodeCollection(final ArrayList<Node> nodes) {
        if (nodeCollections == null)
            nodeCollections = new ArrayList<ArrayList<Node>>();
        nodeCollections.add(nodes);
        return this;
    }

    public StringToNodeCollection addNode(final Node node) {
        if (nodeCollections == null)
            nodeCollections = new ArrayList<ArrayList<Node>>();
        nodeCollections.add(CollectionUtilities.toArrayList(new Node[]{node}));
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null) return false;
        if (!(o instanceof StringToNodeCollection)) return false;
        if (o == this) return true;

        final StringToNodeCollection stringToNodeCollection = (StringToNodeCollection) o;
        if (translationString == null && stringToNodeCollection.translationString != null) return false;
        if (translationString != null && stringToNodeCollection.translationString == null) return false;
        if (translationString == null && stringToNodeCollection.translationString == null) return true;
        if (!translationString.equals(stringToNodeCollection.translationString)) return false;

        return true;
    }
}
