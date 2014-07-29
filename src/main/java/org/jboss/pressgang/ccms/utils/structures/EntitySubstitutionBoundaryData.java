/*
  Copyright 2011-2014 Red Hat

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

public class EntitySubstitutionBoundaryData
{
	private String substitution;
	private String entityName;
	private Pair<Integer, Integer> boundary;

	public String getSubstitution()
	{
		return substitution;
	}

	public void setSubstitution(String substitution)
	{
		this.substitution = substitution;
	}

	public String getEntityName()
	{
		return entityName;
	}

	public void setEntityName(String entityName)
	{
		this.entityName = entityName;
	}

	public Pair<Integer, Integer> getBoundary()
	{
		return boundary;
	}

	public void setBoundary(Pair<Integer, Integer> boundary)
	{
		this.boundary = boundary;
	}
	
	public EntitySubstitutionBoundaryData(final String entityName, final String substitution, final Pair<Integer, Integer> boundary)
	{
		this.entityName = entityName;
		this.substitution = substitution;
		this.boundary = boundary;
	}
}
