/*
 * Created on 26-dic-2005
 *
 */
package org.herac.tuxguitar.song.models.effects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.herac.tuxguitar.song.factory.TGFactory;

/**
 * @author julian
 *
 */
public abstract class TGEffectBend {
	public static final int SEMITONE_LENGTH = 1;
	public static final int MAX_POSITION_LENGTH = 12;
	public static final int MAX_VALUE_LENGTH = (12);
	
	private List<BendPoint> points;
	
	public TGEffectBend(){
        points = new ArrayList<BendPoint>();
	}
	
	public void addPoint(int position,int value){
        points.add(new BendPoint(position, value));
	}
	
	public List<BendPoint> getPoints(){
		return points;
	}
	
	public TGEffectBend clone(TGFactory factory){
		TGEffectBend effect = factory.newEffectBend();
		Iterator<BendPoint> it = points.iterator();
		while(it.hasNext()){
			BendPoint point = (BendPoint)it.next();
			effect.addPoint(point.getPosition(),point.getValue());
		}
		return effect;
	}
	
	public class BendPoint{
		private int position;
		private int value;
		
		public BendPoint(int position,int value){
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
			return new BendPoint(position, value);
		}
	}
	
}
