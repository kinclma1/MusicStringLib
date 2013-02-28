/*
 * Created on 26-dic-2005
 *
 */
package org.herac.tuxguitar.song.models.effects;

import org.herac.tuxguitar.song.factory.TGFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author julian
 *
 */
public abstract class TGEffectTremoloBar {
	public static final int MAX_POSITION_LENGTH = 12;
	public static final int MAX_VALUE_LENGTH = 12;
	
	private List<TremoloBarPoint> points;
	
	public TGEffectTremoloBar(){
        points = new ArrayList<TremoloBarPoint>();
	}
	
	public void addPoint(int position,int value){
        points.add(new TremoloBarPoint(position, value));
	}
	
	public List<TremoloBarPoint> getPoints(){
		return points;
	}
	
	public TGEffectTremoloBar clone(TGFactory factory){
		TGEffectTremoloBar effect = factory.newEffectTremoloBar();
		Iterator<TremoloBarPoint> it = points.iterator();
		while(it.hasNext()){
			TremoloBarPoint point = it.next();
			effect.addPoint(point.getPosition(),point.getValue());
		}
		
		return effect;
	}
	
	public class TremoloBarPoint{
		private int position;
		private int value;
		
		public TremoloBarPoint(int position,int value){
			this.position = position;
			this.value = value;
		}
		
		public int getPosition() {
			return position;
		}
		
		public int getValue() {
			return value;
		}
		
		public long getTime(long duration){
			return (duration * position / MAX_POSITION_LENGTH);
		}
		
		@Override
        public Object clone(){
			return new TremoloBarPoint(position, value);
		}
	}
	
}
