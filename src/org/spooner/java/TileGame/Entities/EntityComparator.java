package org.spooner.java.TileGame.Entities;

import java.util.Comparator;

public class EntityComparator implements Comparator<Entity>{
	//methods
	@Override
	public int compare(Entity e1, Entity e2) {
		int pos = 0;
		//if both present
		if(e1 != null && e2 != null){
			//compare y values
			int yThis = e1.getTileY();
			int yThat = e2.getTileY();
			//different values
			if(yThis != yThat){
				pos = yThis < yThat ? -1 : 1;
			}
		}
		else if(e1 != null){
			pos = -1;
		}
		else if(e2 != null){
			pos = 1;
		}
		return pos;
	}

}
